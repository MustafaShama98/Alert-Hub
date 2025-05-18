import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';
import jwt_decode from 'jwt-decode';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwt_decode(token);
                setUser({
                    email: decoded.sub,
                    permissions: decoded.permissions?.split(',') || [],
                    userId: decoded.userId
                });
            } catch (error) {
                console.error('Invalid token:', error);
                localStorage.removeItem('token');
            }
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await axios.post('http://localhost:8222/api/security/auth/login', 
                {
                    email,
                    password
                },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                }
            );
            
            const { token } = response.data;
            if (!token) {
                throw new Error('No token received from server');
            }

            localStorage.setItem('token', token);
            
            const decoded = jwt_decode(token);
            const userData = {
                email: decoded.sub,
                permissions: decoded.permissions?.split(',') || [],
                userId: decoded.userId
            };
            setUser(userData);

            return true;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    };

    const register = async (userData) => {
        try {
            const response = await axios.post('http://localhost:8222/api/security/auth/signup', 
                userData,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                }
            );
            return response.data;
        } catch (error) {
            console.error('Registration failed:', error);
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    const hasPermission = (permission) => {
        return user?.permissions?.includes(permission) || false;
    };

    const value = {
        user,
        loading,
        login,
        logout,
        register,
        hasPermission
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export default AuthContext; 