package com.example.chatservice.Message;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Service.MessageService;
import com.example.chatservice.Chat.Model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping(path = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    public ResponseEntity<ChatModel> getChats() {
        return ResponseEntity.ok().body(messageService.getAllChats());
    }

    @PostMapping("/")
    public ResponseEntity<MessageModel> addChat(@RequestBody MessageModel chat) {
        return ResponseEntity.ok().body(messageService.saveChat(chat));
    }

    @DeleteMapping("/")
    public ResponseEntity<MessageModel> deleteChat(@RequestBody BigInteger messageID) {
        return ResponseEntity.ok().body(messageService.deleteChat(messageID));
    }
}
