import json

from fastapi import UploadFile
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import PyPDFLoader
from utils.chroma_service import *
from utils.folder_service import *
from utils.redis_service import *
from utils.chroma_service import *


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
