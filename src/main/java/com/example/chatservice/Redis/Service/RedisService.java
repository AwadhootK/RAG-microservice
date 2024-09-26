package com.example.chatservice.Redis.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Message.Model.MessageModel;

import java.util.List;

public interface RedisService {
    List<MessageModel> getMessagesFromRedis(String username) throws Exception;

    ChatModel saveChatsFromRedis(String username, String newChatName, String redisChatKey) throws Exception;

    void deleteChatsFromRedis(String username, String redisChatKey) throws Exception;
}
