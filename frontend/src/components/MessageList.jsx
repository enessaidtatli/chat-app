import React, { useEffect, useRef } from 'react';
import { Avatar, Tooltip, Badge } from 'antd';
import { useChat } from '../context/ChatContext';

function MessageList() {
    const { currentMessages, currentUser, getAvatarColor, error } = useChat();
    const messages = currentMessages();
    const messagesEndRef = useRef(null);

    // Scroll to bottom on new messages
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

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

    return (
        <div className="message-list">
            {error && (
                <div style={{ padding: '0 0 10px 0' }}>
                    <Badge status="error" text={error} />
                </div>
            )}

            {messages.map((msg, index) => renderMessage(msg, index))}
            <div ref={messagesEndRef} />
        </div>
    );
}

export default MessageList;