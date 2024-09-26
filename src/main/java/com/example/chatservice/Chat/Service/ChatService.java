package com.example.chatservice.Chat.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public interface ChatService {

    ChatModel getAllChats();

    ChatModel saveChat(ChatModel chat);

    ChatModel deleteChat(BigInteger messageID);
}
