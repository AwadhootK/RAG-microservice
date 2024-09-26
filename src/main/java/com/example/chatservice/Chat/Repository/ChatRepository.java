package com.example.chatservice.Chat.Repository;

import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ChatRepository extends JpaRepository<ChatModel, BigInteger> {
}
