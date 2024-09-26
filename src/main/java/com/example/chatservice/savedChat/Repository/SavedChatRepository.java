package com.example.chatservice.savedChat.Repository;

import com.example.chatservice.savedChat.Model.SavedChatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface SavedChatRepository extends JpaRepository<SavedChatModel, BigInteger> {
}
