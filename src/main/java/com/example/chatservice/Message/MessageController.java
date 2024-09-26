package com.example.chatservice.Message;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Service.MessageService;

@RestController
@RequestMapping(path = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }

    @GetMapping("/{messageID}")
    public ResponseEntity<List<MessageModel>> getAllMessages(@PathVariable BigInteger messageID) {
        return ResponseEntity.ok().body(messageService.getAllMessages(messageID));
    }

    @PostMapping("/")
    public ResponseEntity<MessageModel> addMessage(@RequestBody MessageModel message) {
        try {
            return ResponseEntity.ok().body(messageService.saveMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{messageID}")
    public ResponseEntity<MessageModel> deleteMessage(@PathVariable BigInteger messageID) {
        try {
            return ResponseEntity.ok().body(messageService.deleteMessage(messageID));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
