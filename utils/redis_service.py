import os
from typing import List

from redis import Redis
from utils.serialization_deserialization_service import *

_redis_instance = None


def get_redis_connection():
    global _redis_instance
    if _redis_instance is None:
        return Redis(
            host=os.getenv("REDIS_HOST"),
            port=os.getenv("REDIS_PORT"),
            db=0
        )
    return _redis_instance


def get_from_redis(userID) -> List:
    redis_client = get_redis_connection()
    if redis_client.exists(f"{userID}/chats"):
        return decode_chats(redis_client.get(f"{userID}/chats").decode('utf-8'))
    return []
