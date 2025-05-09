import React, { useState } from 'react';
import { Form, Input, Button, Typography } from 'antd';

const { Title } = Typography;

function LoginForm({ onLogin }) {
    const [username, setUsername] = useState('');

    const handleSubmit = (e) => {
        if (username.trim()) {
            onLogin(username.trim());
        }
    };

    return (
        <div className="login-page">
            <Form className="login-form" onFinish={handleSubmit}>
                <Title level={2} className="login-title">Chat Uygulamasına Hoş Geldiniz</Title>
                <Form.Item
                    name="username"
                    rules={[{ required: true, message: 'Lütfen kullanıcı adınızı girin!' }]}
                >
                    <Input
                        size="large"
                        placeholder="Kullanıcı adınızı girin"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </Form.Item>
                <Form.Item>
                    <Button
                        type="primary"
                        htmlType="submit"
                        size="large"
                        style={{ width: '100%' }}
                    >
                        Giriş Yap
                    </Button>
                </Form.Item>
            </Form>
        </div>
    );
}

export default LoginForm;