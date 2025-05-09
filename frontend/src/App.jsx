import React, { useState } from 'react';
import { Layout } from 'antd';
import LoginForm from './components/LoginForm';
import ChatRoom from './components/ChatRoom';
import './App.css';

const { Content } = Layout;

function App() {
    const [currentUser, setCurrentUser] = useState(null);

    const onLogin = (username) => {
        setCurrentUser({
            username: username,
            color: getRandomColor(),
        });
    };

    const getRandomColor = () => {
        const colors = [
            '#2196F3', '#32c787', '#00BCD4', '#ff5652',
            '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
        ];
        return colors[Math.floor(Math.random() * colors.length)];
    };

    return (
        <Layout>
            <Content>
                {currentUser ? (
                    <ChatRoom currentUser={currentUser} />
                ) : (
                    <LoginForm onLogin={onLogin} />
                )}
            </Content>
        </Layout>
    );
}

export default App;