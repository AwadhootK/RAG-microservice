from fastapi.responses import JSONResponse


def create_json_response(content, status_code=200):
    return JSONResponse(content=content, status_code=status_code)
