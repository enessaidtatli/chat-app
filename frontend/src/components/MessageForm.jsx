import React, { useState } from 'react';
import { Input, Button } from 'antd';
import { useChat } from '../context/ChatContext';

const { TextArea } = Input;

function MessageForm() {
    const [message, setMessage] = useState('');
    const {
        connected,
        activeTab,
        sendPublicMessage,
        sendRoomMessage,
        sendPrivateMessage
    } = useChat();

    const handleSend = () => {
        if (message.trim() !== '') {
            if (activeTab === 'public') {
                sendPublicMessage(message);
            } else if (activeTab.startsWith('room_')) {
                const roomId = activeTab.replace('room_', '');
                sendRoomMessage(roomId, message);
            } else if (activeTab.startsWith('private_')) {
                const username = activeTab.replace('private_', '');
                sendPrivateMessage(username, message);
            }
            setMessage('');
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    return (
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
    );
}

export default MessageForm;