package com.example.chatservice.Chat;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Chat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

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

    @DeleteMapping("/")
    public ResponseEntity<ChatModel> deleteChat(@RequestBody BigInteger chatID) {
        try {
            return ResponseEntity.ok().body(chatService.deleteChat(chatID));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
