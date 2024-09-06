import warnings
from time import sleep

from dotenv import load_dotenv
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from indexing import *
from starlette.middleware.cors import CORSMiddleware
from utils.rabbitmq_service import start_consuming
from utils.response import *

if __name__ == "__main__":
    # ? run: source ../../venvs/rag_env/bin/activate

    warnings.filterwarnings("ignore")
    load_dotenv('.env')

    print("Running indexing service...")

    with open("log.txt", "w") as f:
        pass

    start_consuming()
