package com.example.chatservice.Message.Service;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<MessageModel> getAllMessages(BigInteger chatID) {
        return messageRepository.findAllByChat_ChatId(chatID);
    }

    @Override
    public MessageModel saveMessage(MessageModel chat) throws Exception {
        return messageRepository.save(chat);
    }

    @Override
    public MessageModel deleteMessage(BigInteger messageID) throws Exception {
        Optional<MessageModel> messageModel = messageRepository.findById(messageID);
        if (messageModel.isPresent()) {
            messageRepository.deleteById(messageID);
            return messageModel.get();
        }
        throw new Exception("Message ID not found");
    }
}
