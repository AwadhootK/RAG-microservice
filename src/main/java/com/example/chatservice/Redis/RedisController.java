package com.example.chatservice.Redis;

import com.example.chatservice.Chat.Model.ChatModel;
import com.example.chatservice.Redis.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/saveChats")
public class RedisController {
    @Autowired
    private RedisService redisService;

    @PostMapping
    public ResponseEntity<String> saveChats(@RequestHeader("username") String username,
                                            @RequestParam String newChatName,
                                            @RequestParam String redisChatKey) {
        try {
            ChatModel savedChat = redisService.saveChatsFromRedis(username, newChatName, redisChatKey);
            return ResponseEntity.ok().body("Chat ID: " + savedChat.getChatId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> deleteChats(@RequestHeader("username") String username,
                                              @RequestParam String redisChatKey) {
        try {
            redisService.deleteChatsFromRedis(username, redisChatKey);
            return ResponseEntity.ok().body("Deleted chats of user: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
