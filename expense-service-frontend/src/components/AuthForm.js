// src/AuthForm.js

import React, { useState } from 'react';
import { styles } from './styles'; // Import styles

// NOTE: Replace this with the actual base URL of your Spring Boot backend
const API_BASE_URL = 'http://localhost:9898/auth/v1';

const AuthForm = () => {
    // --- Form State ---
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [message, setMessage] = useState('');
    const [authData, setAuthData] = useState(null);

    // Consolidated state for all form fields
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        first_name: '',
        last_name: '',
        email: '',
        phone_number: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // --- Submission Logic ---
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('Processing...');
        setAuthData(null);

        const endpoint = isLoginMode ? 'login' : 'signup';
        const url = `${API_BASE_URL}/${endpoint}`;

        // Prepare the payload (only send necessary fields)
        const payload = isLoginMode
            ? { username: formData.username, password: formData.password }
            : { ...formData }; // Send all fields for signup

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            });

            if (response.ok) {
                const data = await response.json();
                setAuthData(data);

                const successMessage = isLoginMode 
                    ? 'Login successful! Tokens stored.' 
                    : 'Sign Up successful! Account created.';
                setMessage(successMessage);

                // Store tokens securely (using localStorage here for demo)
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.token); 
                console.log("Tokens stored in localStorage.");
                
            } else {
                const errorText = await response.text();
                setMessage(`${endpoint} failed. Status: ${response.status}. Message: ${errorText || 'Server Error'}`);
            }

        } catch (error) {
            setMessage(`Network error: Could not connect to the API. Details: ${error.message}`);
        }
    };

    // --- Render Component ---
    return (
        <div style={styles.container}>
            <h2>{isLoginMode ? 'User Login' : 'User Sign Up'}</h2>
            
            <form onSubmit={handleSubmit} style={styles.form}>
                {/* Always required fields */}
                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    style={styles.input}
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    style={styles.input}
                />
                
                {/* Extra fields only for Sign Up */}
                {!isLoginMode && (
                    <>
                        <input type="text" name="first_name" placeholder="First Name" value={formData.firstname} onChange={handleChange} required style={styles.input} />
                        <input type="text" name="last_name" placeholder="Last Name" value={formData.lastname} onChange={handleChange} required style={styles.input} />
                        <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} required style={styles.input} />
                        <input type="tel" name="phone_number" placeholder="Mobile Number" value={formData.mobileNumber} onChange={handleChange} required style={styles.input} />
                    </>
                )}
                
                <button type="submit" style={styles.button}>
                    {isLoginMode ? 'Login' : 'Sign Up'}
                </button>
            </form>

            <p 
                onClick={() => {
                    setIsLoginMode(!isLoginMode);
                    setMessage(''); // Clear message on mode switch
                    setAuthData(null); // Clear tokens on mode switch
                }} 
                style={styles.switchText}
            >
                {isLoginMode 
                    ? 'Need an account? Sign Up' 
                    : 'Already have an account? Login'}
            </p>

            {/* Display Messages */}
            {message && (
                <p style={{ color: message.includes('successful') ? '#28a745' : '#dc3545', fontWeight: 'bold' }}>
                    {message}
                </p>
            )}

            {/* Display Auth Data */}
            {authData && (
                <div style={styles.tokenBox}>
                    <h4>Authentication Successful! 🎉</h4>
                    <p>Access Token (JWT): **{authData.accessToken.substring(0, 30)}...**</p>
                    <p>Refresh Token: **{authData.token.substring(0, 30)}...**</p>
                </div>
            )}
        </div>
    );
};

export default AuthForm;