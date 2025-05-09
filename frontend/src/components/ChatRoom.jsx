import React, { useState, useEffect, useRef } from 'react';
import { Input, Button, Typography, Card, Alert } from 'antd';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const { Title } = Typography;
const { TextArea } = Input;

let stompClient = null;

function ChatRoom({ currentUser }) {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState('');
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState('');
    const messagesEndRef = useRef(null);

    useEffect(() => {
        connect();

        return () => {
            disconnect();
        };
    }, []);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const connect = () => {
        const socket = new SockJS('http://localhost:8080/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    };

    const onConnected = () => {
        setConnected(true);

        // Subscribe to the Public Topic
        stompClient.subscribe('/topic/public', onMessageReceived);

        // Tell your username to the server
        sendMessage('JOIN');
    };

    const onError = (err) => {
        setError('Sunucuya bağlanırken bir hata oluştu. Lütfen sayfayı yenileyin.');
        console.log(err);
    };

    const onMessageReceived = (payload) => {
        const receivedMessage = JSON.parse(payload.body);
        setMessages(prevMessages => [...prevMessages, receivedMessage]);
    };

    const sendMessage = (type) => {
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

    const disconnect = () => {
        if (stompClient !== null) {
            sendMessage('LEAVE');
            stompClient.disconnect();
        }
        setConnected(false);
    };

    const handleSend = () => {
        if (message.trim() !== '') {
            sendMessage('CHAT');
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    const renderMessage = (msg, index) => {
        const isOwnMessage = msg.sender === currentUser.username;

        switch (msg.type) {
            case 'JOIN':
                return (
                    <div key={index} className="user-join">
                        <span>{msg.sender} sohbete katıldı</span>
                    </div>
                );
            case 'LEAVE':
                return (
                    <div key={index} className="user-leave">
                        <span>{msg.sender} sohbetten ayrıldı</span>
                    </div>
                );
            case 'CHAT':
                return (
                    <div key={index} className={`chat-message ${isOwnMessage ? 'own-message' : ''}`}>
                        {!isOwnMessage && <strong style={{ color: getAvatarColor(msg.sender) }}>{msg.sender}</strong>}
                        <div
                            className="message-content"
                            style={{
                                backgroundColor: isOwnMessage ? '#1890ff' : '#f0f2f5',
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

    return (
        <div className="chat-container">
            <div className="chat-header">
                <Title level={3}>Chat Odası</Title>
                <p>Kullanıcı: <strong>{currentUser.username}</strong></p>
            </div>

            {error && <Alert message={error} type="error" showIcon closable />}

            <div className="message-list">
                {messages.map((msg, index) => renderMessage(msg, index))}
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
    );
}

export default ChatRoom;