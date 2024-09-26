package com.example.chatservice.Message.Repository;

import com.example.chatservice.Message.Model.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, BigInteger> {
}
