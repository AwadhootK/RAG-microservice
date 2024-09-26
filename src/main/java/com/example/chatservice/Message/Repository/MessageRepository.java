package com.example.chatservice.Message.Repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chatservice.Message.Model.MessageModel;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, BigInteger> {
    public List<MessageModel> findAllByChat_ChatId(BigInteger chatId);
}
