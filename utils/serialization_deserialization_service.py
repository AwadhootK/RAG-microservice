from typing import List


def encode_list(list: List[str]) -> str:
    if len(list) == 1:
        return f'[{list[0]}]'
    return f'[{list[0]}]=[{list[1]}]'


def encode_chats(chats: List[List[str]]) -> str:
    delimiter = '<s>'
    s = ''
    s += delimiter.join([encode_list(i) for i in chats])
    s += delimiter
    return s


def decode_chats(chats: str) -> List[List[str]]:
    l = []
    for chat in chats.split("<s>"):
        t = chat.split('=')
        if len(t) == 1:
            a = t[0][1:-1]
            l.append([a])
        else:
            q, a = t[0][1:-1], t[1][1:-1]
            l.append([q, a])
    return l
