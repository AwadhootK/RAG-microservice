package com.example.chatservice.Redis.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Repository.ChatRepository;
import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Model.Role;
import com.example.chatservice.Message.Repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<MessageModel> getMessagesFromRedis(String redisKey, ChatModel chat) throws Exception {
            List<String> redisMessageList = redisTemplate.opsForList().range(redisKey, 0, -1);
            if (Objects.nonNull(redisMessageList)) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<MessageModel> messageModelList = new ArrayList<>();
                for (String redisMessage : redisMessageList) {
                    HashMap<String, String> result =
                            objectMapper.readValue(redisMessage, HashMap.class);

                    MessageModel messageModel = new MessageModel();
                    messageModel.setMessage(result.get("message"));
                    messageModel.setRole(result.get("role").equalsIgnoreCase("USER") ? Role.USER : Role.AI);
                    messageModel.setChat(chat);

                    messageModelList.add(messageModel);
                }
                return messageModelList;
            }
        return List.of();
    }

    @Override
    public ChatModel saveChatsFromRedis(String username, String newChatName, String redisChatKey) throws Exception {

        ChatModel chatModel = new ChatModel();
        chatModel.setUserID(username);
        chatModel.setChatName(newChatName);

        ChatModel savedChat = chatRepository.save(chatModel);
        List<MessageModel> messageList = getMessagesFromRedis(redisChatKey, savedChat);

        for (MessageModel message : messageList) {
            message.setChat(savedChat);
        }

        messageRepository.saveAll(messageList);

        return savedChat;
    }


    @Override
    public void deleteChatsFromRedis(String username, String redisChatKey) throws Exception {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisChatKey))) {
            redisTemplate.delete(redisChatKey);
        }
    }
}
