import os
import warnings

from langchain.vectorstores import Chroma
from resources.custom_embedding_wrapper import EmbeddingAdapter
from utils.chroma_service import *
from utils.redis_service import *

import chromadb
from chromadb import Collection

warnings.filterwarnings("ignore")


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
