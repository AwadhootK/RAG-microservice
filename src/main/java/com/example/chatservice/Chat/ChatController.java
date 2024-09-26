package com.example.chatservice.Chat;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(path = "/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/")
    public ResponseEntity<List<ChatModel>> getChats() {
        return ResponseEntity.ok().body(chatService.getAllChats());
    }

    @PostMapping("/")
    public ResponseEntity<ChatModel> addChat(@RequestBody ChatModel chat) {
        return ResponseEntity.ok().body(chatService.saveChat(chat));
    }

    @DeleteMapping("/")
    public ResponseEntity<ChatModel> deleteChat(@RequestBody BigInteger chatID) {
        return ResponseEntity.ok().body(chatService.deleteChat(chatID));
    }
}
