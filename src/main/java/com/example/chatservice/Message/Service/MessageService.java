package com.example.chatservice.Message.Service;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Chat.Model.ChatModel;

import java.math.BigInteger;

public interface MessageService {
    ChatModel getAllChats();

    MessageModel saveChat(MessageModel chat);

    MessageModel deleteChat(BigInteger chatID);

}
