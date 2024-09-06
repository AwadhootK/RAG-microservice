package com.project.ragchatbot.chatbot.savedChat.service;

import java.util.List;

import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

public interface SavedChatService {

    public void save(String username, String chatName);

    public List<SavedChat> findAllSavedChats(String chatName);

    public List<String> findAll(String username);
}
