package com.example.chatservice.Chat.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

public interface ChatService {
    List<ChatModel> getAllChats();

    ChatModel saveChat(ChatModel chat);

    ChatModel deleteChat(BigInteger chatID);
}
