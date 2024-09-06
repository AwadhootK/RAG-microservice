package com.project.ragchatbot.chatbot.savedChat.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ragchatbot.chatbot.chat.service.ChatService;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;
import com.project.ragchatbot.chatbot.savedChat.jpa.repository.SavedChatRepository;
import com.project.ragchatbot.chatbot.savedChat.service.SavedChatService;
import com.project.ragchatbot.security.config.UserSecurityDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SavedChatsServiceImpl implements SavedChatService {
    private final SavedChatRepository savedChatRepository;
    private final ChatService chatService;

    public void save(String username, String chatName) {
        // if chatName already exists means I just need to add and delete older entry.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(chatName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String chatName1 = jsonNode.get("chatName").asText();

        // try to find existing chat in db with given chatName, in case it was
        // previously saved so it
        // needs to just be updated
        SavedChat savedChat = savedChatRepository.findByChatName(chatName1);

        if (savedChat == null) {
            // if the current chat is not saved in the db yet, create new one
            savedChat = new SavedChat();
            savedChat.setChatName(chatName1);
            savedChat.setUserID(username);
            savedChatRepository.save(savedChat);
        }
        chatService.saveChatsToDatabase(savedChat);

    }

    public List<SavedChat> findAllSavedChats(String chatName) {
        String username = UserSecurityDetails.getUsername();
        return savedChatRepository.findAllByChatNameAndUserID(chatName, username);
    }

    public List<String> findAll(String username) {
        return savedChatRepository.findAllChatNames(username);
    }
}
