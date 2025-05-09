package io.github.enessaidtatli.controller;

/**
 * @author etatli on 9.05.2025 15:23
 */
import io.github.enessaidtatli.model.ChatMessage;
import io.github.enessaidtatli.model.Room;
import io.github.enessaidtatli.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessageSendingOperations messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    // Kullanıcı genel kanala katılıyor
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Username'i WebSocket oturumuna ekliyoruz
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatService.addUser(chatMessage.getSender());
        return chatMessage;
    }

    // Belirli bir odaya mesaj gönderme
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendRoomMessage(@DestinationVariable String roomId,
                                       @Payload ChatMessage chatMessage) {
        chatMessage.setRoomId(roomId);
        return chatMessage;
    }

    // Bir odaya katılma
    @MessageMapping("/chat.joinRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage joinRoom(@DestinationVariable String roomId,
                                @Payload ChatMessage chatMessage,
                                SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("room_" + roomId, roomId);

        chatService.addUserToRoom(username, roomId);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setRoomId(roomId);
        chatMessage.setContent(username + " odaya katıldı!");

        return chatMessage;
    }

    // Odadan ayrılma
    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage leaveRoom(@DestinationVariable String roomId,
                                 @Payload ChatMessage chatMessage) {
        String username = chatMessage.getSender();
        chatService.removeUserFromRoom(username, roomId);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setRoomId(roomId);
        chatMessage.setContent(username + " odadan ayrıldı!");

        return chatMessage;
    }

    // Özel (birebir) mesaj gönderme
    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.PRIVATE);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(), "/queue/private", chatMessage);

        // Mesajı gönderene de echo yaparak gönderiyoruz (chat geçmişini görebilmesi için)
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(), "/queue/private", chatMessage);
    }

    // Tüm odaları getirme
    @MessageMapping("/chat.getRooms")
    public void getRooms(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        List<Room> rooms = chatService.getAllRooms();

        messagingTemplate.convertAndSendToUser(
                username, "/queue/rooms", rooms);
    }

    // Tüm aktif kullanıcıları getirme
    @MessageMapping("/chat.getUsers")
    public void getUsers(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        List<String> users = chatService.getAllUsers();

        messagingTemplate.convertAndSendToUser(
                username, "/queue/users", users);
    }

    // Yeni oda oluşturma
    @MessageMapping("/chat.createRoom")
    public void createRoom(@Payload Room room, SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Room newRoom = chatService.createRoom(room.getName(), username);

        // Tüm kullanıcılara yeni odayı bildirme
        messagingTemplate.convertAndSend("/topic/rooms", newRoom);
    }
}