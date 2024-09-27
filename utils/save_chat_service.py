import json

from utils.redis_service import get_redis_connection


def generate_redis_chat_key(userID: str) -> str:
    return userID


def save_chat(message, role, userID):
    conn = get_redis_connection()
    redis_key = generate_redis_chat_key(userID)

    chat_list = []
    if conn.exists(redis_key):
        old_data = conn.get(redis_key)
        chat_list = json.loads(old_data)

    chat_list.append({"message": message, "role": role})

    conn.set(redis_key, json.dumps(chat_list))

    return redis_key
