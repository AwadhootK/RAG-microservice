import json
import os
import warnings

import redis
from dotenv import load_dotenv
from fastapi import UploadFile
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.vectorstores import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_community.embeddings import SentenceTransformerEmbeddings

import chromadb
from chromadb import Collection

warnings.filterwarnings("ignore")
load_dotenv('.env')


def get_redis_connection():
    return redis.Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)


class EmbeddingAdapter(SentenceTransformerEmbeddings):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def _embed_documents(self, texts):
        return super().embed_documents(texts)

    def __call__(self, input):
        return self._embed_documents(input)


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


def empty_folder(folder_path):
    for item in os.listdir(folder_path):
        item_path = os.path.join(folder_path, item)
        if os.path.isfile(item_path):
            os.remove(item_path)
        elif os.path.isdir(item_path):
            empty_folder(item_path)
            os.rmdir(item_path)


def save_file(file, userID):
    file_location = f"docs/{userID}/{file.filename}"

    try:
        os.makedirs(f"docs/{userID}")
    except FileExistsError:
        pass

    with open(file_location, "wb") as f:
        contents = file.file.read()
        f.write(contents)


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
