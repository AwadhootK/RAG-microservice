import base64
import io
import json
import time

import pika
from fastapi import UploadFile
from indexing import index_files
from utils.redis_service import get_redis_connection


def try_connect():
    credentials = pika.PlainCredentials('admin', 'pass@123')
    return pika.BlockingConnection(
        pika.ConnectionParameters(host='rabbitmq', port=5672, connection_attempts=5, retry_delay=1, credentials=credentials))


def write_logs(log):
    with open("log.txt", 'a+') as f:
        f.write(f"${time.time()}  |  ${log}\n")


def start_consuming():
    # write_logs('started consuming')

    connection = try_connect()
    # None

    # start_count = 5

    # while connection is None or start_count != 0:
    #     try:
    #         connection = try_connect()
    #     except Exception as e:
    # write_logs(f"Connection failed: {e}, retrying in 5 seconds...")
    #         start_count -= 1
    #         time.sleep(5)

    if connection is None:
        # write_logs('Failed to establish connection with rabbitmq server...')
        return

    # write_logs("Connection established!")

    channel = connection.channel()

    if not connection or not channel:
        # write_logs('no connection or channel')
        return

    # write_logs('channel created!')
    # write_logs("Connection and channel established")

    # Declare exchange first
    try:
        channel.exchange_declare(
            exchange='index_exchange',
            exchange_type='direct'
        )
        # write_logs("Exchange declared successfully")
    except Exception as e:
        # write_logs(f"Error declaring exchange: {e}")
        connection.close()
        return

    queue_name = 'index_queue'
    try:
        channel.queue_declare(queue_name)
        # write_logs("Queue declared successfully")
    except Exception as e:
        # write_logs(f"Error declaring queue: {e}")
        connection.close()
        return

    try:
        channel.queue_bind(
            exchange='index_exchange',
            queue=queue_name,
            routing_key='index'  # binding key
        )
        # write_logs("Queue bound to exchange successfully")
    except Exception as e:
        # write_logs(f"Error binding queue to exchange: {e}")
        connection.close()
        return

    def callback(ch, method, properties, body):
        # payload = json.loads(body)
        # username = base64.b64decode(payload['userID'])
        # userfile = UploadFile(
        #     filename=f"${username}/received_file.pdf", file=io.BytesIO(payload['userfile']))

        file_like_object = io.BytesIO(body)
        file_name = properties.headers.get('userID', 'default')
        job_id = properties.headers.get('job_id', 'default')

        # ! figure out how to send userid
        userfile = UploadFile(file=file_like_object, filename=file_name)

        # write_logs(f'received payload')

        get_redis_connection().set(job_id, "processing")

        index_files(file=userfile, userID=userfile.filename)

        # write_logs(f'indexing of {userfile.filename} done')

        get_redis_connection().set(job_id, "done")

        ch.basic_ack(delivery_tag=method.delivery_tag)

    channel.basic_consume(on_message_callback=callback, queue=queue_name)
    channel.start_consuming()
