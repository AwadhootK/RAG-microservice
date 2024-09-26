package com.example.chatservice.Chat.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService{
    @Override
    public List<ChatModel> getAllChats() {
        return List.of();
    }

    @Override
    public ChatModel saveChat(ChatModel chat) {
        return null;
    }

    @Override
    public ChatModel deleteChat(BigInteger chatID) {
        return null;
    }
}
