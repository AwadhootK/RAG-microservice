package com.example.chatservice.Message.Model;

import java.io.Serializable;
import java.math.BigInteger;

import com.example.chatservice.Chat.Model.ChatModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Chat")
@Data
public class MessageModel implements Serializable {

    private static final long serialVersionUID = 856444116;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private BigInteger messageId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "role")
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    private ChatModel chat;
}
