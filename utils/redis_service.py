import os

from redis import Redis


def get_redis_connection():
    print("REDIS HOST = " + os.getenv("REDIS_HOST"), flush=True)
    print("REDIS PORT = " + os.getenv("REDIS_PORT"), flush=True)

    print("CHROMA HOST = " + os.getenv("CHROMA_HOST"), flush=True)
    print("CHROMA PORT = " + os.getenv("CHROMA_PORT"), flush=True)

    return Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)
