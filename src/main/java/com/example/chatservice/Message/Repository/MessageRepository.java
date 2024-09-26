package com.example.chatservice.Message.Repository;

import com.example.chatservice.Message.Model.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, BigInteger> {
    public List<MessageModel> findAllByChat_ChatId(BigInteger chatId);
}
