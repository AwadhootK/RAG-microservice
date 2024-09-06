package com.project.ragchatbot.chatbot.chat.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer messageID;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private ChatRole chatRole;

    @ManyToOne
    @JoinColumn(name = "chatID")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "chatID")
    @JsonIdentityReference(alwaysAsId = true)
    private SavedChat savedChat;
}
