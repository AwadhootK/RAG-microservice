package com.example.chatservice.Message.Model;

import com.example.chatservice.Chat.Model.ChatModel;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name = "Chat")
public class MessageModel implements Serializable {

    private static final long serialVersionUID = 856444116;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private BigInteger messageId;

    @Column(name = "message",nullable = false)
    private String message;

    @Column(name = "role")
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_id")
    private ChatModel chat;
}
