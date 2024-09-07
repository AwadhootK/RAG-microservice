import json
import math
import uuid

import pika
from fastapi import UploadFile
from langchain.chains import (create_history_aware_retriever,
                              create_retrieval_chain)
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_core.messages import HumanMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from utils.chroma_service import *
from utils.redis_service import *


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


def answer(query, userID):
    rag_chain = create_custom_rag_chain(userID=userID)
    if rag_chain == False:
        return 'The context is empty. Please add a file to query.'

    # get chat_history from redis
    chat_history = retrieve_chat_history(userID=userID)

    response = rag_chain.invoke({
        'input': query,
        'chat_history': []  # ! add the history here
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


def retrieve_chat_history(userID):
    chat_history = get_from_redis(userID)
    chat_history = [[HumanMessage(chat[0])] if len(chat) == 1 else [HumanMessage(content=chat[0]), chat[1]]
                    for chat in chat_history]
    return chat_history


def summarize_chat_history(prev_msgs: List) -> List:
    return []


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


def push_index_queue(userfile: UploadFile, userID):
    credentials = pika.PlainCredentials(
        os.getenv("RABBITMQ_USERNAME"), os.getenv("RABBITMQ_PASSWORD"))
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=os.getenv("RABBITMQ_HOST"), port=os.getenv("RABBITMQ_PORT"), connection_attempts=5, retry_delay=1, credentials=credentials))
    channel = connection.channel()

    try:
        channel.exchange_declare(
            exchange='index_exchange',
            exchange_type='direct'
        )
        print("Exchange declared successfully in publisher")
    except Exception as e:
        print(f"Error declaring exchange in publisher: {e}")
        connection.close()
        return

    file_content = userfile.file.read()
    job_id = uuid.uuid4().hex

    channel.basic_publish(
        exchange='index_exchange',
        routing_key='index',  # message routing key
        body=file_content,
        properties=pika.BasicProperties(
            content_type='application/octet-stream',
            delivery_mode=2,  # Make message persistent
            headers={"userID": userID, "job_id": job_id}
        )
    )

    print('Sent message')

    connection.close()
    return job_id


def get_all_redis(job_id):
    conn = get_redis_connection()
    if conn.exists(job_id):
        status = conn.get(job_id).decode()

        if status == "done":
            conn.delete(job_id)

        return status
    return f"No job with ID: {job_id} found!"
