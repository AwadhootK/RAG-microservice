package com.example.chatservice.Chat;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Service.ChatService;
import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/")
    public ResponseEntity<ChatModel> getChats() {
        return ResponseEntity.ok().body(chatService.getAllChats());
    }

    @PostMapping("/")
    public ResponseEntity<ChatModel> addChat(@RequestBody ChatModel chat) {
        return ResponseEntity.ok().body(chatService.saveChat(chat));
    }

    @DeleteMapping("/")
    public ResponseEntity<ChatModel> deleteChat(@RequestBody BigInteger messageID) {
        return ResponseEntity.ok().body(chatService.deleteChat(messageID));
    }
}
