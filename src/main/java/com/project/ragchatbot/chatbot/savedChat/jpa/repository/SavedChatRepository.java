package com.project.ragchatbot.chatbot.savedChat.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

import java.util.List;

public interface SavedChatRepository extends JpaRepository<SavedChat, Integer> {
    public SavedChat findByChatName(String chatName);

    @Query("SELECT s.chatName FROM SavedChat s WHERE s.userID =  :username")
    List<String> findAllChatNames(@Param("username") String username);

    List<SavedChat> findAllByChatNameAndUserID(String chatName, String userID);
}
