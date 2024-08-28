import json

import pika


def start_consuming():
    connection = pika.BlockingConnection(
        pika.ConnectionParameters('rabbitmq'))
    channel = connection.channel()

    queue_name = 'index_queue'
    channel.queue_declare(queue_name)

    channel.queue_bind(
        exchange='index_exchange',
        queue=queue_name,
        routing_key='index'  # binding key
    )

    def callback(ch, method, properties, body):
        payload = json.loads(body)
        print(f'received {payload}\n')
        ch.basic_ack(delivery_tag=method.delivery_tag)

    channel.basic_consume(on_message_callback=callback, queue=queue_name)
    channel.start_consuming()
