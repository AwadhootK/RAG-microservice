
import os

import google.generativeai as genai
from langchain.vectorstores import Chroma
from langchain_google_genai import ChatGoogleGenerativeAI
from resources.custom_embedding_wrapper import *
from utils.redis_service import *

import chromadb


def get_llm():
    return ChatGoogleGenerativeAI(
        model="gemini-pro", temperature=0.7, convert_system_message_to_human=True)


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
