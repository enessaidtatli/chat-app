import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class ChatService {
    constructor() {
        this.stompClient = null;
        this.callbacks = {
            onConnected: () => {},
            onError: () => {},
            onPublicMessageReceived: () => {},
            onPrivateMessageReceived: () => {},
            onRoomMessageReceived: () => {},
            onRoomsReceived: () => {},
            onUsersReceived: () => {},
            onRoomCreated: () => {},
            onUsersUpdate: () => {},
        };
        this.roomSubscriptions = {};
    }

    setCallbacks(callbacks) {
        this.callbacks = { ...this.callbacks, ...callbacks };
    }

    connect(username) {
        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, () => {
            // Subscribe to the Public Topic
            this.stompClient.subscribe('/topic/public', (payload) => {
                this.callbacks.onPublicMessageReceived(JSON.parse(payload.body));
            });

            // Subscribe to user-specific topics
            this.stompClient.subscribe(`/user/${username}/queue/private`, (payload) => {
                this.callbacks.onPrivateMessageReceived(JSON.parse(payload.body));
            });

            this.stompClient.subscribe(`/user/${username}/queue/rooms`, (payload) => {
                this.callbacks.onRoomsReceived(JSON.parse(payload.body));
            });

            this.stompClient.subscribe(`/user/${username}/queue/users`, (payload) => {
                this.callbacks.onUsersReceived(JSON.parse(payload.body));
            });

            this.stompClient.subscribe('/topic/rooms', (payload) => {
                this.callbacks.onRoomCreated(JSON.parse(payload.body));
            });

            this.stompClient.subscribe('/topic/users', (payload) => {
                this.callbacks.onUsersUpdate(JSON.parse(payload.body));
            });

            this.callbacks.onConnected();

            this.sendPublicMessage(username, '', 'JOIN');

            this.requestRooms();
            this.requestUsers();
        }, this.callbacks.onError);
    }

    disconnect(username) {
        if (this.stompClient !== null) {
            this.sendPublicMessage(username, '', 'LEAVE');

            // Unsubscribe from all rooms
            Object.values(this.roomSubscriptions).forEach(subscription => {
                subscription.unsubscribe();
            });

            this.stompClient.disconnect();
            this.stompClient = null;
        }
    }

    // Public message methods
    sendPublicMessage(sender, content, type) {
        if (this.stompClient) {
            const chatMessage = {
                sender,
                content,
                type,
                time: this.getCurrentTime()
            };
            this.stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        }
    }

    // Room methods
    subscribeToRoom(roomId, username) {
        if (this.stompClient && !this.roomSubscriptions[roomId]) {
            const subscription = this.stompClient.subscribe(`/topic/room/${roomId}`, (payload) => {
                this.callbacks.onRoomMessageReceived(roomId, JSON.parse(payload.body));
            });

            this.roomSubscriptions[roomId] = subscription;

            // Send join room message
            this.sendRoomMessage(username, '', 'JOIN', roomId);
            return true;
        }
        return false;
    }

    unsubscribeFromRoom(roomId) {
        if (this.roomSubscriptions[roomId]) {
            this.roomSubscriptions[roomId].unsubscribe();
            delete this.roomSubscriptions[roomId];
            return true;
        }
        return false;
    }

    sendRoomMessage(sender, content, type, roomId) {
        if (this.stompClient) {
            const chatMessage = {
                sender,
                content,
                type,
                roomId,
                time: this.getCurrentTime()
            };
            this.stompClient.send(`/app/chat.sendMessage/${roomId}`, {}, JSON.stringify(chatMessage));
        }
    }

    joinRoom(username, roomId) {
        return this.subscribeToRoom(roomId, username);
    }

    leaveRoom(username, roomId) {
        const chatMessage = {
            sender: username,
            type: 'LEAVE',
            roomId
        };
        this.stompClient.send(`/app/chat.leaveRoom/${roomId}`, {}, JSON.stringify(chatMessage));
        return this.unsubscribeFromRoom(roomId);
    }

    createRoom(username, roomName) {
        if (this.stompClient) {
            const room = {
                name: roomName,
                creator: username
            };
            this.stompClient.send("/app/chat.createRoom", {}, JSON.stringify(room));
            return true;
        }
        return false;
    }

    // Private message methods
    sendPrivateMessage(sender, receiver, content) {
        if (this.stompClient) {
            const chatMessage = {
                sender,
                receiver,
                content,
                type: 'PRIVATE',
                time: this.getCurrentTime()
            };
            this.stompClient.send("/app/chat.private", {}, JSON.stringify(chatMessage));
        }
    }

    // Utility methods
    requestRooms() {
        if (this.stompClient) {
            this.stompClient.send("/app/chat.getRooms", {});
        }
    }

    requestUsers() {
        if (this.stompClient) {
            this.stompClient.send("/app/chat.getUsers", {});
        }
    }

    getCurrentTime() {
        return new Date().toLocaleTimeString('tr-TR', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    isConnected() {
        return this.stompClient !== null && this.stompClient.connected;
    }
}

// Singleton instance
const chatService = new ChatService();
export default chatService;