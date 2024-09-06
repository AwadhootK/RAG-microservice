package com.project.ragchatbot.chatbot.chat.service.impl;

import com.project.ragchatbot.chatbot.chat.jpa.entity.CachedChat;
import com.project.ragchatbot.chatbot.chat.jpa.entity.Chat;
import com.project.ragchatbot.chatbot.chat.jpa.repository.ChatRepository;

import lombok.AllArgsConstructor;
import java.util.List;

//@Service
//@AllArgsConstructor
//public class ChatService {
//    private final ChatRepository chatRepository;
//    private List<CachedChat> chatCache = new LinkedList<>(); // temporary cache instead of Redis
//    public void saveChatToCache(CachedChat cachedChat) {
//        System.out.println("Saved " + cachedChat.message + " to cache!");
//        chatCache.add(cachedChat);
//    }
//    public void saveChatsToDatabase(SavedChat savedChat) {
//        for (CachedChat cachedChat:chatCache) {
//            Chat newChat = new Chat();
//            newChat.setMessage(cachedChat.message);
//            newChat.setChatRole(cachedChat.chatRole);
//            newChat.setSavedChat(savedChat);
//            chatRepository.save(newChat);
//        }
//        System.out.println("Saved " + savedChat.getChatID() + " to database!");
//        chatCache.clear();
//    }
//
//    public List<Chat> findChatsByID(SavedChat savedChat) throws Exception {
//        return chatRepository.findChatsBySavedChatOrderByMessageID(savedChat).orElseThrow(Exception::new);
//    }
//}
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.project.ragchatbot.chatbot.chat.service.ChatService;
import com.project.ragchatbot.chatbot.savedChat.jpa.entity.SavedChat;;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveChatToCache(CachedChat cachedChat) {
        System.out.println("Saved " + cachedChat.getMessage() + " to cache!");
        redisTemplate.opsForList().rightPush("cache_key", cachedChat);
    }

    public void saveChatsToDatabase(SavedChat savedChat) {
        List<Object> cachedChats = redisTemplate.opsForList().range("cache_key", 0, -1);
        if (cachedChats != null) {
            List<CachedChat> chatsToSave = cachedChats.stream()
                    .map(obj -> (CachedChat) obj)
                    .toList();

            for (CachedChat cachedChat : chatsToSave) {
                Chat newChat = new Chat();
                newChat.setMessage(cachedChat.getMessage());
                newChat.setChatRole(cachedChat.getChatRole());
                newChat.setSavedChat(savedChat);
                chatRepository.save(newChat);
            }
            System.out.println("Saved " + savedChat.getChatID() + " to database!");
            redisTemplate.delete("cache_key");
        }
    }

    public List<Chat> findChatsByID(SavedChat savedChat) throws Exception {
        return chatRepository.findChatsBySavedChatOrderByMessageID(savedChat).orElseThrow(Exception::new);
    }
}
