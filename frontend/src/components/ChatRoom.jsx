import React, { useState, useEffect, useRef } from 'react';
import { Input, Button, Typography, Tabs, List, Badge, Modal, Form, Layout, Menu, Avatar, Tooltip } from 'antd';
import { MessageOutlined, TeamOutlined, UserOutlined, PlusOutlined } from '@ant-design/icons';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const { Title } = Typography;
const { TextArea } = Input;
const { Sider, Content } = Layout;
const { TabPane } = Tabs;

let stompClient = null;

function ChatRoom({ currentUser }) {
    // State for messages, rooms, and users
    const [activeTab, setActiveTab] = useState('public');
    const [publicMessages, setPublicMessages] = useState([]);
    const [roomMessages, setRoomMessages] = useState({});
    const [privateMessages, setPrivateMessages] = useState({});
    const [message, setMessage] = useState('');
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState('');
    const [rooms, setRooms] = useState([]);
    const [users, setUsers] = useState([]);
    const [newRoomName, setNewRoomName] = useState('');
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [activeConversation, setActiveConversation] = useState(null);
    const [collapsed, setCollapsed] = useState(false);

    const messagesEndRef = useRef(null);

    // Initialize connection
    useEffect(() => {
        connect();
        return () => disconnect();
    }, []);

    // Scroll to bottom on new messages
    useEffect(() => {
        scrollToBottom();
    }, [publicMessages, roomMessages, privateMessages, activeTab, activeConversation]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    // Connect to WebSocket
    const connect = () => {
        const socket = new SockJS('http://localhost:8080/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    };

    const onConnected = () => {
        setConnected(true);

        // Subscribe to the Public Topic
        stompClient.subscribe('/topic/public', onPublicMessageReceived);

        // Subscribe to user-specific topics for private messages and notifications
        stompClient.subscribe(`/user/${currentUser.username}/queue/private`, onPrivateMessageReceived);
        stompClient.subscribe(`/user/${currentUser.username}/queue/rooms`, onRoomsReceived);
        stompClient.subscribe(`/user/${currentUser.username}/queue/users`, onUsersReceived);
        stompClient.subscribe('/topic/rooms', onRoomCreated);
        stompClient.subscribe('/topic/users', onUsersUpdate);

        // Join the public chat
        sendPublicMessage('JOIN');

        // Request rooms and users list
        requestRooms();
        requestUsers();
    };

    const onError = (err) => {
        setError('Sunucuya bağlanırken bir hata oluştu. Lütfen sayfayı yenileyin.');
        console.log(err);
    };

    // Message handlers
    const onPublicMessageReceived = (payload) => {
        const receivedMessage = JSON.parse(payload.body);
        setPublicMessages(prevMessages => [...prevMessages, receivedMessage]);
    };

    const onRoomMessageReceived = (roomId, payload) => {
        const receivedMessage = JSON.parse(payload.body);
        setRoomMessages(prevMessages => ({
            ...prevMessages,
            [roomId]: [...(prevMessages[roomId] || []), receivedMessage]
        }));
    };

    const onPrivateMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        const chatPartner = message.sender === currentUser.username ? message.receiver : message.sender;

        setPrivateMessages(prevMessages => ({
            ...prevMessages,
            [chatPartner]: [...(prevMessages[chatPartner] || []), message]
        }));
    };

    const onRoomsReceived = (payload) => {
        const receivedRooms = JSON.parse(payload.body);
        setRooms(receivedRooms);
    };

    const onUsersReceived = (payload) => {
        const receivedUsers = JSON.parse(payload.body);
        // Filter out current user
        setUsers(receivedUsers.filter(user => user !== currentUser.username));
    };

    const onRoomCreated = (payload) => {
        const newRoom = JSON.parse(payload.body);
        setRooms(prevRooms => [...prevRooms, newRoom]);
    };

    const onUsersUpdate = (payload) => {
        const updatedUsers = JSON.parse(payload.body);
        setUsers(updatedUsers.filter(user => user !== currentUser.username));
    };

    // Send messages
    const sendPublicMessage = (type) => {
        if (stompClient && (message.trim() !== '' || type === 'JOIN')) {
            const chatMessage = {
                sender: currentUser.username,
                content: message,
                type: type,
                time: new Date().toLocaleTimeString('tr-TR', {
                    hour: '2-digit',
                    minute: '2-digit'
                })
            };
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            if (type === 'CHAT') {
                setMessage('');
            }
        }
    };

    const sendRoomMessage = (roomId, type) => {
        if (stompClient && (message.trim() !== '' || type === 'JOIN')) {
            const chatMessage = {
                sender: currentUser.username,
                content: message,
                type: type,
                roomId: roomId,
                time: new Date().toLocaleTimeString('tr-TR', {
                    hour: '2-digit',
                    minute: '2-digit'
                })
            };
            stompClient.send(`/app/chat.sendMessage/${roomId}`, {}, JSON.stringify(chatMessage));
            if (type === 'CHAT') {
                setMessage('');
            }
        }
    };

    const sendPrivateMessage = (receiver) => {
        if (stompClient && message.trim() !== '') {
            const chatMessage = {
                sender: currentUser.username,
                receiver: receiver,
                content: message,
                type: 'PRIVATE',
                time: new Date().toLocaleTimeString('tr-TR', {
                    hour: '2-digit',
                    minute: '2-digit'
                })
            };
            stompClient.send("/app/chat.private", {}, JSON.stringify(chatMessage));
            setMessage('');
        }
    };

    // Room and user actions
    const joinRoom = (roomId) => {
        if (!roomMessages[roomId]) {
            // Subscribe to the room
            stompClient.subscribe(`/topic/room/${roomId}`, (payload) => onRoomMessageReceived(roomId, payload));

            // Send join room message
            const joinMessage = {
                sender: currentUser.username,
                type: 'JOIN',
                roomId: roomId
            };
            stompClient.send(`/app/chat.joinRoom/${roomId}`, {}, JSON.stringify(joinMessage));
        }

        // Set the active tab to the room
        setActiveTab(`room_${roomId}`);
        setActiveConversation(roomId);
    };

    const leaveRoom = (roomId) => {
        const leaveMessage = {
            sender: currentUser.username,
            type: 'LEAVE',
            roomId: roomId
        };
        stompClient.send(`/app/chat.leaveRoom/${roomId}`, {}, JSON.stringify(leaveMessage));

        // Unsubscribe from the room
        // Note: This is a simplified version; normally you'd keep track of subscription IDs

        // Return to public chat
        setActiveTab('public');
        setActiveConversation(null);
    };

    const startPrivateChat = (username) => {
        setActiveTab(`private_${username}`);
        setActiveConversation(username);
    };

    const createRoom = () => {
        if (newRoomName.trim() !== '') {
            const room = {
                name: newRoomName,
                creator: currentUser.username
            };
            stompClient.send("/app/chat.createRoom", {}, JSON.stringify(room));
            setNewRoomName('');
            setIsModalVisible(false);
        }
    };

    const requestRooms = () => {
        stompClient.send("/app/chat.getRooms", {});
    };

    const requestUsers = () => {
        stompClient.send("/app/chat.getUsers", {});
    };

    const disconnect = () => {
        if (stompClient !== null) {
            sendPublicMessage('LEAVE');
            stompClient.disconnect();
        }
        setConnected(false);
    };

    // UI handlers
    const handleSend = () => {
        if (message.trim() !== '') {
            if (activeTab === 'public') {
                sendPublicMessage('CHAT');
            } else if (activeTab.startsWith('room_')) {
                const roomId = activeTab.replace('room_', '');
                sendRoomMessage(roomId, 'CHAT');
            } else if (activeTab.startsWith('private_')) {
                const username = activeTab.replace('private_', '');
                sendPrivateMessage(username);
            }
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const showModal = () => {
        setIsModalVisible(true);
    };

    const handleCancel = () => {
        setIsModalVisible(false);
        setNewRoomName('');
    };

    // Rendering helpers
    const renderMessage = (msg, index) => {
        const isOwnMessage = msg.sender === currentUser.username;

        switch (msg.type) {
            case 'JOIN':
                return (
                    <div key={index} className="user-join">
                        <span>{msg.content || `${msg.sender} sohbete katıldı`}</span>
                    </div>
                );
            case 'LEAVE':
                return (
                    <div key={index} className="user-leave">
                        <span>{msg.content || `${msg.sender} sohbetten ayrıldı`}</span>
                    </div>
                );
            case 'CHAT':
            case 'PRIVATE':
                const isPrivate = msg.type === 'PRIVATE';
                return (
                    <div key={index} className={`chat-message ${isOwnMessage ? 'own-message' : ''} ${isPrivate ? 'private-message' : ''}`}>
                        {!isOwnMessage && (
                            <Tooltip title={msg.sender}>
                                <Avatar
                                    style={{
                                        backgroundColor: getAvatarColor(msg.sender),
                                        marginRight: '8px'
                                    }}
                                    size="small"
                                >
                                    {msg.sender.charAt(0).toUpperCase()}
                                </Avatar>
                            </Tooltip>
                        )}
                        <div
                            className="message-content"
                            style={{
                                backgroundColor: isOwnMessage ? '#1890ff' : (isPrivate ? '#ffd6e7' : '#f0f2f5'),
                                color: isOwnMessage ? 'white' : 'black'
                            }}
                        >
                            <p style={{ margin: 0 }}>{msg.content}</p>
                            <small style={{
                                display: 'block',
                                textAlign: 'right',
                                fontSize: '11px',
                                marginTop: '4px',
                                opacity: 0.7
                            }}>
                                {msg.time}
                            </small>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    const getAvatarColor = (username) => {
        if (username === currentUser.username) {
            return currentUser.color;
        }

        let hash = 0;
        for (let i = 0; i < username.length; i++) {
            hash = username.charCodeAt(i) + ((hash << 5) - hash);
        }

        const colors = [
            '#2196F3', '#32c787', '#00BCD4', '#ff5652',
            '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
        ];

        return colors[Math.abs(hash) % colors.length];
    };

    const renderChatContent = () => {
        if (activeTab === 'public') {
            return publicMessages.map((msg, index) => renderMessage(msg, index));
        } else if (activeTab.startsWith('room_')) {
            const roomId = activeTab.replace('room_', '');
            return (roomMessages[roomId] || []).map((msg, index) => renderMessage(msg, index));
        } else if (activeTab.startsWith('private_')) {
            const username = activeTab.replace('private_', '');
            return (privateMessages[username] || []).map((msg, index) => renderMessage(msg, index));
        }
        return null;
    };

    const getActiveTabTitle = () => {
        if (activeTab === 'public') {
            return 'Genel Sohbet';
        } else if (activeTab.startsWith('room_')) {
            const roomId = activeTab.replace('room_', '');
            const room = rooms.find(r => r.id === roomId);
            return room ? room.name : 'Oda';
        } else if (activeTab.startsWith('private_')) {
            const username = activeTab.replace('private_', '');
            return `${username} ile özel sohbet`;
        }
        return 'Sohbet';
    };

    return (
        <Layout style={{ height: '100vh' }}>
            <Sider
                width={250}
                theme="light"
                collapsible
                collapsed={collapsed}
                onCollapse={setCollapsed}
            >
                <div className="user-info" style={{ padding: '16px', borderBottom: '1px solid #f0f0f0' }}>
                    <Avatar
                        style={{ backgroundColor: currentUser.color }}
                        size="large"
                    >
                        {currentUser.username.charAt(0).toUpperCase()}
                    </Avatar>
                    {!collapsed && (
                        <span style={{ marginLeft: '8px', fontWeight: 'bold' }}>
                            {currentUser.username}
                        </span>
                    )}
                </div>

                <Menu
                    mode="inline"
                    selectedKeys={[activeTab]}
                    style={{ height: '100%', borderRight: 0 }}
                >
                    <Menu.Item key="public" icon={<MessageOutlined />} onClick={() => {
                        setActiveTab('public');
                        setActiveConversation(null);
                    }}>
                        Genel Sohbet
                    </Menu.Item>

                    <Menu.SubMenu key="rooms" icon={<TeamOutlined />} title="Odalar">
                        {rooms.map(room => (
                            <Menu.Item
                                key={`room_${room.id}`}
                                onClick={() => joinRoom(room.id)}
                            >
                                {room.name}
                            </Menu.Item>
                        ))}
                        <Menu.Item
                            key="new_room"
                            icon={<PlusOutlined />}
                            onClick={showModal}
                        >
                            Yeni Oda Oluştur
                        </Menu.Item>
                    </Menu.SubMenu>

                    <Menu.SubMenu key="users" icon={<UserOutlined />} title="Özel Mesajlar">
                        {users.map(user => (
                            <Menu.Item
                                key={`private_${user}`}
                                onClick={() => startPrivateChat(user)}
                            >
                                {user}
                            </Menu.Item>
                        ))}
                    </Menu.SubMenu>
                </Menu>
            </Sider>

            <Layout>
                <Content>
                    <div className="chat-container">
                        <div className="chat-header">
                            <Title level={4}>{getActiveTabTitle()}</Title>
                        </div>

                        {error && (
                            <div style={{ padding: '0 20px' }}>
                                <Badge status="error" text={error} />
                            </div>
                        )}

                        <div className="message-list">
                            {renderChatContent()}
                            <div ref={messagesEndRef} />
                        </div>

                        <div className="message-form">
                            <TextArea
                                placeholder="Mesajınızı yazın..."
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                onKeyPress={handleKeyPress}
                                autoSize={{ minRows: 2, maxRows: 4 }}
                                style={{ marginBottom: '10px' }}
                                disabled={!connected}
                            />
                            <Button
                                type="primary"
                                onClick={handleSend}
                                style={{ float: 'right' }}
                                disabled={!connected || message.trim() === ''}
                            >
                                Gönder
                            </Button>
                        </div>
                    </div>
                </Content>
            </Layout>

            <Modal
                title="Yeni Oda Oluştur"
                visible={isModalVisible}
                onOk={createRoom}
                onCancel={handleCancel}
                okButtonProps={{ disabled: !newRoomName.trim() }}
            >
                <Form layout="vertical">
                    <Form.Item
                        label="Oda Adı"
                        rules={[{ required: true, message: 'Lütfen bir oda adı girin!' }]}
                    >
                        <Input
                            value={newRoomName}
                            onChange={(e) => setNewRoomName(e.target.value)}
                            placeholder="Oda adı girin"
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </Layout>
    );
}

export default ChatRoom;