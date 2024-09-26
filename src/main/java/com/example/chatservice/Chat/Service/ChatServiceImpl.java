package com.example.chatservice.Chat.Service;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public List<ChatModel> getAllChats(String username) {
        return chatRepository.findAllByUserID(username).orElse(new ArrayList<>());
    }

    @Override
    public ChatModel getChatByID(BigInteger chatID) {
        return chatRepository.findByChatId(chatID).orElse(null);
    }

    @Override
    public ChatModel saveChat(ChatModel chat) throws Exception {
        chatRepository.save(chat);
        return chat;
    }

    @Override
    public ChatModel deleteChat(BigInteger chatID) throws Exception {
        Optional<ChatModel> chat = chatRepository.findByChatId(chatID);
        if (chat.isPresent()) {
            chatRepository.deleteById(chatID);
            return chat.get();
        }
        throw new Exception("Chat ID not found");
    }
}
