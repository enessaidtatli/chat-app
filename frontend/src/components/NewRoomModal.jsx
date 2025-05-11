import React, { useState } from 'react';
import { Modal, Form, Input } from 'antd';
import { useChat } from '../context/ChatContext';

function NewRoomModal({ visible, onCancel }) {
    const [newRoomName, setNewRoomName] = useState('');
    const { createRoom } = useChat();

    const handleCreateRoom = () => {
        if (newRoomName.trim() !== '') {
            createRoom(newRoomName);
            setNewRoomName('');
            onCancel();
        }
    };

    return (
        <Modal
            title="Yeni Oda Oluştur"
            open={visible}
            onOk={handleCreateRoom}
            onCancel={onCancel}
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
    );
}

export default NewRoomModal;