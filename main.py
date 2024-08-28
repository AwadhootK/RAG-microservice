import uvicorn
from chatbot import *
from dotenv import load_dotenv
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from model import AnswerLLMModel, QueryModel
from starlette.middleware.cors import CORSMiddleware
from utils.response import *

app = FastAPI()

origins = [
    "*",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,  # Allow all origins by using ["*"]
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods (GET, POST, etc.)
    allow_headers=["*"],  # Allow all headers
)


def configure_llm():
    GOOGLE_API_KEY = os.getenv('GOOGLE_API_KEY')
    genai.configure(api_key=GOOGLE_API_KEY)


@app.get("/ping")
async def root():
    return create_json_response({"message": "pong"})


@app.post("/ask")
async def respond(queryBody: QueryModel):
    ans = answer(query=queryBody.query, userID=queryBody.username)
    return create_json_response({'query': queryBody.query, 'answer': ans})


@app.get("/summarize/{username}")
async def summarize_doc(username):
    summary = summarize_from_llm(userID=username)
    return create_json_response({'summary': summary})


@app.post("/semantic_search")
async def semantic_search(queryBody: QueryModel):
    semantic_result = sematic_doc_search_by_vector(
        query=queryBody.query, userID=queryBody.username)
    return create_json_response({'similar_doc': semantic_result})


@app.post("/answer-llm")
async def answer_llm(queryBody: AnswerLLMModel):
    answer = answer_from_llm(query=queryBody.query)
    return create_json_response({'answer': answer})


@app.delete("/empty-context/{username}")
async def delete_context(username):
    clear_context(userID=username)
    return create_json_response({'message': f'{username}\'s context cleared successfully!'})


@app.post("/temp")
async def push_queue(queryBody: QueryModel):
    push_index_queue(query=queryBody.query)
    return create_json_response({'message': 'Added to queue!'})

if __name__ == "__main__":
    # ? run: source ../../../../venvs/rag_env/bin/activate

    load_dotenv('.env')
    configure_llm()
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
