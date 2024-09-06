package com.project.ragchatbot.chatbot.chat.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CachedChat implements Serializable {
    String message;
    ChatRole chatRole;
    public CachedChat() {}

    public CachedChat(String message, ChatRole chatRole) {
        this.message = message;
        this.chatRole = chatRole;
    }
}
