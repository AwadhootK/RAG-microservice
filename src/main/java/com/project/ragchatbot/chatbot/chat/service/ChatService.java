package com.project.ragchatbot.chatbot.chat.service;

import java.util.List;

import com.project.ragchatbot.chatbot.chat.jpa.entity.CachedChat;
import com.project.ragchatbot.chatbot.chat.jpa.entity.Chat;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

public interface ChatService {

    public void saveChatToCache(CachedChat cachedChat);

    public void saveChatsToDatabase(SavedChat savedChat);

    public List<Chat> findChatsByID(SavedChat savedChat) throws Exception;
}