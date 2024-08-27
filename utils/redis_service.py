import os

from redis import Redis



def get_redis_connection():
    return Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)
