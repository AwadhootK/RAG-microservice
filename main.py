import warnings

from dotenv import load_dotenv
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from indexing import *
from starlette.middleware.cors import CORSMiddleware
from utils.rabbitmq_service import start_consuming
from utils.response import *


if __name__ == "__main__":
    # ? run: source ../../venvs/rag_env/bin/activate

    warnings.filterwarnings("always")
    load_dotenv('.env')

    start_consuming()

    # uvicorn.run("main:app", host="0.0.0.0", port=8100, reload=True)
