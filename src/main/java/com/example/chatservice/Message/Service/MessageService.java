package com.example.chatservice.Message.Service;

import com.example.chatservice.Message.Model.MessageModel;

import java.math.BigInteger;
import java.util.List;

public interface MessageService {
    List<MessageModel> getAllMessages(BigInteger chatID);

    MessageModel saveMessage(MessageModel chat) throws Exception;

    MessageModel deleteMessage(BigInteger chatID) throws Exception;
}
