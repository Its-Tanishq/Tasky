
import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../utils/api';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchUser = useCallback(async () => {
        setLoading(true);
        try {
            const response = await api.get('/auth/me');
            console.log(response.data);
            setUser(response.data);
            setError(null);
        } catch (err) {
            console.error("Failed to fetch user user:", err);
            if (err.response && err.response.status === 401) {
                setUser(null);
            } else {
                setError(err.response?.data?.message || err.message);
                setUser(null);
            }
        } finally {
            setLoading(false);
        }
    }, []);

    const logout = () => {
        localStorage.removeItem("accessToken");
        setUser(null);
        window.location.href = '/';
    };

    const value = {
        user,
        loading,
        setLoading,
        error,
        fetchUser,
        setUser,
        logout
    };

    return (
        <UserContext.Provider value={value}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error("useUser must be used within a UserProvider");
    }
    return context;
};

export default UserContext;
