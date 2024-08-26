from pydantic import BaseModel


class QueryModel(BaseModel):
    query: str
    username: str


class AnswerLLMModel(BaseModel):
    query: str
