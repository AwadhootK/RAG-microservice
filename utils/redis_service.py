import os

from redis import Redis


def get_redis_connection():
    print("REDIS HOST = " + os.getenv("REDIS_HOST"))
    print("REDIS PORT = " + os.getenv("REDIS_PORT"))

    print("CHROMA HOST = " + os.getenv("CHROMA_HOST"))
    print("CHROMA PORT = " + os.getenv("CHROMA_PORT"))

    return Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)
