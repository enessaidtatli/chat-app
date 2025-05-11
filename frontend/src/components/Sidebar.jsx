import React from 'react';
import { Menu, Avatar } from 'antd';
import { MessageOutlined, TeamOutlined, UserOutlined, PlusOutlined } from '@ant-design/icons';
import { useChat } from '../context/ChatContext';

function Sidebar({ collapsed, showModal }) {
    const {
        activeTab,
        rooms,
        users,
        currentUser,
        getAvatarColor,
        setActiveTab,
        joinRoom,
        startPrivateChat,
    } = useChat();

    return (
        <>
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
                <Menu.Item
                    key="public"
                    icon={<MessageOutlined />}
                    onClick={() => {
                        setActiveTab('public');
                    }}
                >
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
        </>
    );
}

export default Sidebar;