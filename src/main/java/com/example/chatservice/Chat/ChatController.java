package com.example.chatservice.Chat;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Service.ChatService;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }

    @GetMapping("/")
    public ResponseEntity<List<ChatModel>> getAllChats(@RequestHeader("username") String username) {
        return ResponseEntity.ok().body(chatService.getAllChats(username));
    }

    @GetMapping("/{chatID}")
    public ResponseEntity<ChatModel> getChat(@PathVariable("chatID") BigInteger chatID) {
        return ResponseEntity.ok().body(chatService.getChatByID(chatID));
    }

    @PostMapping("/")
    public ResponseEntity<ChatModel> saveChat(@RequestBody ChatModel chat) {
        try {
            return ResponseEntity.ok().body(chatService.saveChat(chat));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{chatID}")
    public ResponseEntity<ChatModel> deleteChat(@PathVariable("chatID") BigInteger chatID) {
        try {
            return ResponseEntity.ok().body(chatService.deleteChat(chatID));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
