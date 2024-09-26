package com.example.chatservice.Message.Service;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public ChatModel getAllChats() {
        return null;
    }

    @Override
    public MessageModel saveChat(MessageModel chat) {
        return null;
    }

    @Override
    public MessageModel deleteChat(BigInteger chatID) {
        return null;
    }
}
