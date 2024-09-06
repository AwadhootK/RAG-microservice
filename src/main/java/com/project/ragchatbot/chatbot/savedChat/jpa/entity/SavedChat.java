package com.project.ragchatbot.chatbot.savedChat.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.project.ragchatbot.chatbot.chat.jpa.entity.Chat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saved_chat")
public class SavedChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer chatID;
    private String userID;
    private String chatName;

    @OneToMany(mappedBy = "savedChat")
    private List<Chat> chats;
}
