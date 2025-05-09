package io.github.enessaidtatli.controller;

/**
 * @author etatli on 9.05.2025 15:25
 */

import io.github.enessaidtatli.model.ChatMessage;
import io.github.enessaidtatli.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Yeni bir web socket bağlantısı alındı!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {
            String username = (String) sessionAttributes.get("username");
            if(username != null) {
                logger.info("Kullanıcı bağlantısı kesildi: " + username);

                // Kullanıcıyı listeden kaldır
                chatService.removeUser(username);

                // Genel kanala ayrılma mesajı gönder
                ChatMessage publicMessage = new ChatMessage();
                publicMessage.setType(ChatMessage.MessageType.LEAVE);
                publicMessage.setSender(username);
                publicMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                messagingTemplate.convertAndSend("/topic/public", publicMessage);

                // Kullanıcının bulunduğu tüm odalardan çıkış mesajı gönder
                for (String key : sessionAttributes.keySet()) {
                    if (key.startsWith("room_")) {
                        String roomId = (String) sessionAttributes.get(key);
                        ChatMessage roomMessage = new ChatMessage();
                        roomMessage.setType(ChatMessage.MessageType.LEAVE);
                        roomMessage.setSender(username);
                        roomMessage.setRoomId(roomId);
                        roomMessage.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                        roomMessage.setContent(username + " odadan ayrıldı!");

                        messagingTemplate.convertAndSend("/topic/room/" + roomId, roomMessage);

                        // Odadan kullanıcıyı kaldır
                        chatService.removeUserFromRoom(username, roomId);
                    }
                }

                // Kullanıcı listesini güncelle ve diğer kullanıcılara bildir
                messagingTemplate.convertAndSend("/topic/users", chatService.getAllUsers());
            }
        }
    }
}
