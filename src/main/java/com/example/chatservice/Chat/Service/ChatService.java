package com.example.chatservice.Chat.Service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.chatservice.Chat.Model.ChatModel;

@Service
public interface ChatService {

    List<ChatModel> getAllChats(String username);

    ChatModel saveChat(ChatModel chat) throws Exception;

    ChatModel deleteChat(BigInteger chatID) throws Exception;

    ChatModel getChatByID(BigInteger chatID);
}
