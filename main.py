from typing import Annotated

import uvicorn
from chatbot import *
from dotenv import load_dotenv
from fastapi import FastAPI, File, Form, Header, HTTPException, UploadFile
from model import AnswerLLMModel, QueryModel
from starlette.middleware.cors import CORSMiddleware
from utils.response import *
from utils.save_chat_service import save_chat

app = FastAPI()

origins = [
    "*",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


def configure_llm():
    GOOGLE_API_KEY = os.getenv('GOOGLE_API_KEY')
    genai.configure(api_key=GOOGLE_API_KEY)


@app.get("/ping")
async def root():
    return create_json_response({"message": "pong"})


@app.get("/get_job_status/{job_id}")
async def redis(job_id):
    return create_json_response({"redis": get_all_redis(job_id=job_id)})


@app.post("/ask")
async def respond(queryBody: QueryModel, username: Annotated[str | None, Header()] = None):
    if not username:
        return create_json_response({'error': 'Username not present'}, status_code=401)
    ans = answer(query=queryBody.query, userID=username)
    redis_key = save_chat(message=queryBody.query,
                          role="User", userID=username)
    save_chat(message=ans, role="AI", userID=username)
    return create_json_response({'query': queryBody.query, 'answer': ans, 'redis_key': redis_key})


@app.post("/answer-llm")
async def answer_llm(queryBody: AnswerLLMModel, username: Annotated[str | None, Header()] = None):
    answer = answer_from_llm(query=queryBody.query)
    redis_key = save_chat(message=queryBody.query,
                          role="User", userID=username)
    save_chat(message=answer, role="AI", userID=username)
    return create_json_response({'answer': answer, 'redis_key': redis_key})


@app.get("/summarize")
async def summarize_doc(username: Annotated[str | None, Header()] = None):
    if not username:
        return create_json_response({'error': 'Username not present'}, status_code=401)
    summary = summarize_from_llm(userID=username)
    return create_json_response({'summary': summary})


@app.post("/semantic_search")
async def semantic_search(queryBody: QueryModel, username: Annotated[str | None, Header()] = None):
    if not username:
        return create_json_response({'error': 'Username not present'}, status_code=401)
    semantic_result = sematic_doc_search_by_vector(
        query=queryBody.query, userID=username)
    return create_json_response({'similar_doc': semantic_result})


@app.delete("/empty-context")
async def delete_context(username: Annotated[str | None, Header()] = None):
    if not username:
        return create_json_response({'error': 'Username not present'}, status_code=401)
    clear_context(userID=username)
    return create_json_response({'message': f'{username}\'s context cleared successfully!'})


@app.post("/upload")
async def upload_file(
    index: str = Form(...),
    save: str = Form(...),
    username: str = Form(...),
    file: UploadFile = File(...)
):
    #! if save == 'true':
    # upload to azure file storage

    job_id = None
    try:
        if index == 'true':
            print('indexing')
            # index_files(file=file, userID=username)
            job_id = push_index_queue(userfile=file, userID=username)
    except Exception as e:
        print(e)
        return create_json_response({'error': 'Error...'})

    if job_id is None:
        return create_json_response({'error': 'Could not add to processing queue...'})

    return create_json_response({
        "job_id": job_id,
        "status": "processing..."
    })

if __name__ == "__main__":
    # ? run: source ../../../../venvs/rag_env/bin/activate

    load_dotenv('.env')
    configure_llm()
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
