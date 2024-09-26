package com.example.chatservice.Message.Service;

import java.math.BigInteger;
import java.util.List;

import com.example.chatservice.Message.Model.MessageModel;

public interface MessageService {
    List<MessageModel> getAllMessages(BigInteger chatID);

    MessageModel saveMessage(MessageModel chat) throws Exception;

    MessageModel deleteMessage(BigInteger chatID) throws Exception;
}
