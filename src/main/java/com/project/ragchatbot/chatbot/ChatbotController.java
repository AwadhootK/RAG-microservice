package com.project.ragchatbot.chatbot;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/chatbot")
@AllArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String query) {
        try {
            return ResponseEntity.ok(chatbotService.ask(query));
        } catch (JsonProcessingException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error submitting query");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload() {
        return ResponseEntity.ok("File Uploaded Successfully");
    }

    @GetMapping("/emptyContext")
    public ResponseEntity<String> emptyContext() {
        chatbotService.emptyContext();
        return ResponseEntity.ok("Context cleared successfully");
    }

    @PostMapping("/answerLLM")
    public ResponseEntity<String> answerLLM(@RequestBody String query) {
        return ResponseEntity.ok(chatbotService.answerLLM(query));
    }

    @GetMapping("/summarize")
    public ResponseEntity<String> summarize() {
        return ResponseEntity.ok(chatbotService.summarize());
    }

    @PostMapping("/sematicSearch")
    public ResponseEntity<String> semanticSearch(@RequestBody String query) {
        return ResponseEntity.ok(chatbotService.sematicSearch(query));
    }

    @PostMapping("/saveChats")
    public ResponseEntity<String> saveChats(@RequestBody String chatName) {
        chatbotService.saveChatsToDB(chatName);
        return ResponseEntity.ok("Chats saved successfully");
    }

    @PostMapping("/getSaveChats")
    public ResponseEntity<List<SavedChat>> getSavedChats(@RequestBody String chatName) {
        return ResponseEntity.ok(chatbotService.getAllSavedChats(chatName));
    }

    @GetMapping("/getAllSavedChatNames")
    public ResponseEntity<List<String>> getAllSavedChatNames() {
        return ResponseEntity.ok(chatbotService.getAllSavedChatNames());
    }

    @GetMapping("/restoreContext")
    public ResponseEntity<String> restoreContext() {
        return ResponseEntity.ok(chatbotService.restoreContext());
    }
}
