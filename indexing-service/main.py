import uvicorn
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from fastapi.responses import JSONResponse
from indexing import *
from starlette.middleware.cors import CORSMiddleware

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


if __name__ == "__main__":
    # ? run: source ../../../../venvs/rag_env/bin/activate

    uvicorn.run("main:app", host="0.0.0.0", port=8100, reload=True)
