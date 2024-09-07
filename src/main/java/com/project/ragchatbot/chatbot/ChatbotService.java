package com.project.ragchatbot.chatbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.ragchatbot.chatbot.chat.jpa.entity.CachedChat;
import com.project.ragchatbot.chatbot.chat.jpa.entity.ChatRole;
import com.project.ragchatbot.chatbot.chat.service.ChatService;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;
import com.project.ragchatbot.chatbot.savedChat.service.SavedChatService;
import com.project.ragchatbot.security.config.UserSecurityDetails;
import com.project.ragchatbot.util.FlaskAPIEndpoints;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatbotService {
    private final RestTemplate restTemplate;
    private final ChatService chatService;
    private final SavedChatService savedChatService;

    @Value("${RAG_HOST}")
    private String RAG_HOST;

    @Value("${RAG_PORT}")
    private String RAG_PORT;

    private String RAG_URL = "http://" + RAG_HOST + ":" + RAG_PORT;

    // @Value("${INDEXING_HOST}")
    // private String INDEXING_HOST;

    // @Value("${INDEXING_PORT}")
    // private String INDEXING_PORT;

    // private String INDEXING_URL = "http://" + RAG_HOST + ":" + RAG_PORT;

    private String callFlaskAPIPost(String url, String query) {
        String username = UserSecurityDetails.getUsername();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("query", query);
        requestBody.put("username", username);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    private String callFlaskAPIPost(String url) {
        String username = UserSecurityDetails.getUsername();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Changed to form URL encoded

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("username", username); // Add form data

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers); // Use the correct
                                                                                                    // entity type

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

    private String callFlaskAPIGet(String url) {
        String username = UserSecurityDetails.getUsername();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url + "/" + username, HttpMethod.GET, request,
                String.class);
        return response.getBody();
    }

    public String ask(String query) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(query);
        String question = jsonNode.get("query").asText();

        String answer = callFlaskAPIPost(RAG_URL + FlaskAPIEndpoints.ASK, query);

        jsonNode = objectMapper.readTree(answer);
        String reply = jsonNode.get("answer").asText();

        if (!reply.equalsIgnoreCase("The context is empty. Please add a file to query.")) {
            chatService.saveChatToCache(new CachedChat(question, ChatRole.USER));
            chatService.saveChatToCache(new CachedChat(reply, ChatRole.AI));
        }

        return answer;
    }

    public void saveChatsToDB(String chatName) {
        savedChatService.save(UserSecurityDetails.getUsername(), chatName);
    }

    public void emptyContext() {
        callFlaskAPIGet(RAG_URL + FlaskAPIEndpoints.EMPTY_CONTEXT);
    }

    public String answerLLM(String query) {
        return callFlaskAPIPost(RAG_URL + FlaskAPIEndpoints.ANSWER_LLM, query);
    }

    public String summarize() {
        return callFlaskAPIGet(RAG_URL + FlaskAPIEndpoints.SUMMARIZE);
    }

    public String sematicSearch(String query) {
        return callFlaskAPIPost(RAG_URL + FlaskAPIEndpoints.SEMANTIC_SEARCH, query);
    }

    public List<SavedChat> getAllSavedChats(String chatName) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(chatName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String cName = jsonNode.get("chatName").asText();
        return savedChatService.findAllSavedChats(cName);
    }

    public List<String> getAllSavedChatNames() {
        String username = UserSecurityDetails.getUsername();
        return savedChatService.findAll(username);
    }

    public String restoreContext() {
        return callFlaskAPIPost(RAG_URL + FlaskAPIEndpoints.RESTORE_CONTEXT);
    }
}
