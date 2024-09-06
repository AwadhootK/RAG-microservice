package com.project.ragchatbot.chatbot.chat.jpa.repository;

import com.project.ragchatbot.chatbot.chat.jpa.entity.Chat;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<List<Chat>> findChatsBySavedChatOrderByMessageID(SavedChat savedChat);
}
