package com.example.chatservice.Chat.Model;

import com.example.chatservice.Message.Model.MessageModel;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "UserChat")
@Data
public class ChatModel implements Serializable {

    private static final long serialVersionUID = 937935837;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "chat_id")
    private BigInteger chatId;

    @Column(name = "user_id")
    private String userID;

    @Column(name = "chat_name")
    private String chatName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "chat")
    private List<MessageModel> chats;
}
