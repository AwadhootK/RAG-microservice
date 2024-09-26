package com.example.chatservice.Redis.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Repository.ChatRepository;
import com.example.chatservice.Message.Model.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, List<MessageModel>> redisTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public List<MessageModel> getMessagesFromRedis(String redisKey) throws Exception {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            return redisTemplate.opsForValue().get(redisKey);
        }
        return List.of();
    }

    @Override
    public ChatModel saveChatsFromRedis(String username, String newChatName, String redisChatKey) throws Exception {
        List<MessageModel> messageList = getMessagesFromRedis(redisChatKey);
        ChatModel chatModel = new ChatModel();
        chatModel.setUserID(username);
        chatModel.setChatName(newChatName);
        chatModel.setChats(messageList);

        chatRepository.save(chatModel);

        return chatModel;
    }

    @Override
    public void deleteChatsFromRedis(String username, String redisChatKey) throws Exception {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisChatKey))) {
            redisTemplate.delete(redisChatKey);
        }
    }
}
