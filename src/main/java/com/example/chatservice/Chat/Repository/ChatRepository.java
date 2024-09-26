package com.example.chatservice.Chat.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatservice.Chat.Model.ChatModel;

@Repository
public interface ChatRepository extends JpaRepository<ChatModel, BigInteger> {
    public Optional<List<ChatModel>> findAllByUserID(String userID);

    public Optional<ChatModel> findByChatId(BigInteger userID);
}
