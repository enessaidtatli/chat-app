package io.github.enessaidtatli.service;

import io.github.enessaidtatli.model.Room;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    // Aktif kullanıcılar listesi
    private Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    // Sohbet odaları
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    public ChatService() {
        // Başlangıçta bir genel sohbet odası oluştur
        Room generalRoom = new Room("Genel Sohbet", "system");
        rooms.put(generalRoom.getId(), generalRoom);
    }

    // Kullanıcı ekleme
    public void addUser(String username) {
        activeUsers.add(username);
    }

    // Kullanıcı silme
    public void removeUser(String username) {
        activeUsers.remove(username);

        // Kullanıcıyı tüm odalardan çıkar
        for (Room room : rooms.values()) {
            room.getUsers().remove(username);
        }
    }

    // Tüm kullanıcıları getir
    public List<String> getAllUsers() {
        return new ArrayList<>(activeUsers);
    }

    // Yeni oda oluştur
    public Room createRoom(String roomName, String creator) {
        Room newRoom = new Room(roomName, creator);
        rooms.put(newRoom.getId(), newRoom);
        return newRoom;
    }

    // Kullanıcıyı odaya ekle
    public void addUserToRoom(String username, String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.getUsers().add(username);
        }
    }

    // Kullanıcıyı odadan çıkar
    public void removeUserFromRoom(String username, String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.getUsers().remove(username);
        }
    }

    // Tüm odaları getir
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    // Belirli bir odadaki kullanıcıları getir
    public Set<String> getUsersInRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            return new HashSet<>(room.getUsers());
        }
        return new HashSet<>();
    }
}