import json
import math
import os
import warnings
from typing import List, Optional

import chromadb
import google.generativeai as genai
import redis
from chromadb import Collection
from chromadb.utils import embedding_functions
from dotenv import load_dotenv
from fastapi import UploadFile
from langchain.chains import (create_history_aware_retriever,
                              create_retrieval_chain)
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.vectorstores import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_core.messages import HumanMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_google_genai import (ChatGoogleGenerativeAI,
                                    GoogleGenerativeAIEmbeddings)

warnings.filterwarnings("ignore")
load_dotenv('.env')


class EmbeddingAdapter(SentenceTransformerEmbeddings):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def _embed_documents(self, texts):
        return super().embed_documents(texts)

    def __call__(self, input):
        return self._embed_documents(input)


# class CustomGoogleGenerativeAIEmbeddings(GoogleGenerativeAIEmbeddings):
#     def embed_documents(self, texts: List[str],
#                         task_type: Optional[str] = None,
#                         titles: Optional[List[str]] = None,
#                         output_dimensionality: Optional[int] = None) -> List[List[float]]:
#         embeddings_repeated = super().embed_documents(texts)
#         embeddings = [list(emb) for emb in embeddings_repeated]
#         return embeddings

#     def as_retriever(self, search_kwargs):
#         super().as_retriever(search_kwargs=search_kwargs)


def get_redis_connection():
    return redis.Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)


def get_llm():
    return ChatGoogleGenerativeAI(
        model="gemini-pro", temperature=0.7, convert_system_message_to_human=True)


# def get_embedding_model():
#     return CustomGoogleGenerativeAIEmbeddings(
#         model="models/embedding-001")


def get_summary_model():
    return genai.GenerativeModel("gemini-pro")


def get_embedding_function():
    return EmbeddingAdapter(model_name='sentence-transformers/all-mpnet-base-v2')
    # return embedding_functions.SentenceTransformerEmbeddingFunction(model_name="all-mpnet-base-v2")


def get_chroma_client():
    return chromadb.HttpClient(
        host=os.getenv("CHROMA_HOST"),
        port=os.getenv("CHROMA_PORT"))


def get_chromadb_instance(userID) -> Chroma:
    return Chroma(client=get_chroma_client(),
                  collection_name=userID,
                  embedding_function=get_embedding_function())


def get_vector_index(userID):
    return get_chromadb_instance(userID).as_retriever(search_kwargs={"k": 5})


def get_chromadb_collection_intsance(userID) -> Collection:
    return get_chroma_client().get_or_create_collection(name=userID,
                                                        embedding_function=get_embedding_function())


def create_custom_rag_chain(userID):
    contextualize_q_system_prompt = """Given a chat history and the latest user question \
        which might reference context in the chat history, formulate a standalone question \
        which can be understood without the chat history. Do NOT answer the question, \
        just reformulate it if needed and otherwise return it as is."""
    contextualize_q_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", contextualize_q_system_prompt),
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )

    history_aware_retriever = create_history_aware_retriever(
        get_llm(), get_vector_index(userID), contextualize_q_prompt)

    qa_system_prompt = """You are an assistant for question-answering tasks. \
        Use the following pieces of retrieved context to answer the question. \
        If you don't know the answer, just say that you don't know. \
        Use three sentences maximum and keep the answer concise.\

        {context}"""

    qa_prompt = ChatPromptTemplate.from_messages(
        [
            ("system", qa_system_prompt),
            MessagesPlaceholder("chat_history"),
            ("human", "{input}"),
        ]
    )

    question_answer_chain = create_stuff_documents_chain(
        get_llm(), qa_prompt)

    rag_chain = create_retrieval_chain(
        history_aware_retriever, question_answer_chain)

    return rag_chain


def load_files(userID):
    pass


def save_file(file, userID):
    file_location = f"docs/{userID}/{file.filename}"

    try:
        os.makedirs(f"docs/{userID}")
    except FileExistsError:
        pass

    with open(file_location, "wb") as f:
        contents = file.file.read()
        f.write(contents)


def empty_folder(folder_path):
    for item in os.listdir(folder_path):
        item_path = os.path.join(folder_path, item)
        if os.path.isfile(item_path):
            os.remove(item_path)
        elif os.path.isdir(item_path):
            empty_folder(item_path)
            os.rmdir(item_path)


def index_files(file: UploadFile, userID):
    pages = []
    texts = []

    file_location = f"docs/{userID}/{file.filename}"
    save_file(file=file, userID=userID)

    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=10000, chunk_overlap=1000)
    pdf_loader = PyPDFLoader(file_path=file_location)
    new_pages = pdf_loader.load_and_split()
    pages.extend(new_pages)
    context = "\n\n".join(str(p.page_content) for p in new_pages)
    texts.extend(text_splitter.split_text(context))

    empty_folder(f"docs")

    if texts == []:
        raise Exception("Could not read file")

    chromadb_collection = get_chromadb_collection_intsance(userID=userID)
    chromadb_collection.add(
        ids=[str(i) for i in range(len(texts))], documents=texts)

    # store texts in redis here
    get_redis_connection().set(f"{userID}/texts", json.dumps(texts))

    print('vector indexing done!')


def answer(query, userID):
    rag_chain = create_custom_rag_chain(userID=userID)
    if rag_chain == False:
        return 'The context is empty. Please add a file to query.'

    # get chat_history from redis
    chat_history = retrieve_chat_history(userID=userID)

    response = rag_chain.invoke({
        'input': query,
        'chat_history': []
    })

    store_summarize_chat_history(
        userID=userID, currentQ=query, currentA=response['answer'])
    return response['answer']


def answer_from_llm(query):
    response = get_llm().invoke(query)
    return response.content


def summarize_from_llm(userID):
    # get the saved texts from redis for userID
    redis_client = get_redis_connection()
    if not redis_client.exists(f"{userID}/texts"):
        raise Exception("Cannot summarize, context is empty")

    text = json.loads(redis_client.get(f"{userID}/texts").decode('utf-8'))

    prompt = f"""You are a highly skilled text summarizer.
        Please read the following text carefully and provide a concise summary that
        captures the main points and key details. Make sure the summary is clear, coherent,
        and retains the essential information from the original text.
        Do not apply any markdown and formatting to the summary.

        Here is the text to summarize: {text}"""

    summary = get_summary_model().generate_content(prompt)

    combined_summary = ''.join([part.text for part in summary.parts])
    return combined_summary


def embed_query(query):
    return get_embedding_function().embed_query(text=query)


def sematic_doc_search_by_vector(query, userID):
    query_vector = embed_query(query)
    print(query_vector)
    results = get_chromadb_instance(
        userID).similarity_search_by_vector(query_vector, k=1)
    if results:
        top_document = results[0].page_content
        return top_document
    else:
        return "No relevant documents found."


def get_from_redis(userID) -> List:
    redis_client = get_redis_connection()
    if redis_client.exists(f"{userID}/chats"):
        return decode_chats(redis_client.get(f"{userID}/chats").decode('utf-8'))
    return []


def retrieve_chat_history(userID):
    chat_history = get_from_redis(userID)
    chat_history = [[HumanMessage(chat[0])] if len(chat) == 1 else [HumanMessage(content=chat[0]), chat[1]]
                    for chat in chat_history]
    return chat_history


def summarize_chat_history(prev_msgs: List) -> List:
    return []


def encode_list(list: List[str]) -> str:
    if len(list) == 1:
        return f'[{list[0]}]'
    return f'[{list[0]}]=[{list[1]}]'


def encode_chats(chats: List[List[str]]) -> str:
    delimiter = '<s>'
    s = ''
    s += delimiter.join([encode_list(i) for i in chats])
    s += delimiter
    return s


def decode_chats(chats: str) -> List[List[str]]:
    l = []
    for chat in chats.split("<s>"):
        t = chat.split('=')
        if len(t) == 1:
            a = t[0][1:-1]
            l.append([a])
        else:
            q, a = t[0][1:-1], t[1][1:-1]
            l.append([q, a])
    return l


def store_summarize_chat_history(userID, currentQ, currentA):
    # store chat history in redis as -> (userID, chat_history)
    # if chat_history.length > 10 -> Eg: 14, store recent 50% messages and summarize last 50% messages into one single message

    max_len = 10
    percent = 0.5
    limit = math.floor(percent * max_len)

    chat_history = get_from_redis(userID)
    chat_history.append([currentQ, currentA])

    if len(chat_history) > max_len:
        prev_msg, recent_msg = chat_history[:limit], chat_history[limit:]
        chat_history = [summarize_chat_history(prev_msg)] + recent_msg

    redis_client = get_redis_connection()
    redis_client.set(f"{userID}/chats", encode_chats(chat_history))


def clear_context(userID):
    # clear redis and chromaDB
    get_redis_connection().delete(f"{userID}/chats")
    get_redis_connection().delete(f"{userID}/texts")
    get_chroma_client().delete_collection(userID)
