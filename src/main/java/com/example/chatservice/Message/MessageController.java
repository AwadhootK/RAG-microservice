package com.example.chatservice.Message;

import com.example.chatservice.Message.Model.MessageModel;
import com.example.chatservice.Message.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(path = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{chatID}")
    public ResponseEntity<List<MessageModel>> getAllMessages(@PathVariable BigInteger chatID) {
        return ResponseEntity.ok().body(messageService.getAllMessages(chatID));
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

    @DeleteMapping("/")
    public ResponseEntity<MessageModel> deleteMessage(@RequestBody BigInteger messageID) {
        try {
            return ResponseEntity.ok().body(messageService.deleteMessage(messageID));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
