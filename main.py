import uvicorn
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from fastapi.responses import JSONResponse
from starlette.middleware.cors import CORSMiddleware

from chatbot import *
from model import QueryModel

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


def create_json_response(content, status_code=200):
    return JSONResponse(content=content, status_code=status_code)


@app.get("/ping")
async def root():
    return create_json_response({"message": "pong"})


@app.post("/upload")
async def upload_file(
    index: str = Form(...),
    save: str = Form(...),
    username: str = Form(...),
    file: UploadFile = File(...)
):
    # can use a messaging queue to make it asynchronous
    res = {'index': False, 'saved': False}

    #! if save == 'true':
    # upload to azure file storage

    if index == 'true':
        print('indexing')
        index_files(file=file, userID=username)
        if not res:
            return {"error": "Gemini Rate Limit Exceeded"}
        res['index'] = True

    return create_json_response({"info": res})


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

if __name__ == "__main__":
    #? run: source ../../../venvs/rag_env/bin/activate

    configure_llm()
    uvicorn.run("main:app", host="0.0.0.0", port=8000)
