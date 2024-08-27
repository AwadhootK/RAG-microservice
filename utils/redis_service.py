import os
from typing import List

from redis import Redis
from utils.serialization_deserialization_service import *


def get_redis_connection():
    return Redis(
        host=os.getenv("REDIS_HOST"),
        port=os.getenv("REDIS_PORT"),
        db=0)


def get_from_redis(userID) -> List:
    redis_client = get_redis_connection()
    if redis_client.exists(f"{userID}/chats"):
        return decode_chats(redis_client.get(f"{userID}/chats").decode('utf-8'))
    return []
