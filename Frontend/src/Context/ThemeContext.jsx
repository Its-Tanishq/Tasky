import React, { createContext, useState, useEffect } from 'react'

export const ThemeContext = createContext();

const ThemeContextProvider = ({ children }) => {
    const [theme, setTheme] = useState(() => {
        return localStorage.getItem('theme') || 'light';
    });

    useEffect(() => {
        document.documentElement.setAttribute("data-theme", theme);
        localStorage.setItem('theme', theme);
    }, [theme]);

    const toggletheme = () => {
        setTheme(prev => prev === 'light' ? 'dark' : 'light');
    }

    return (
        <ThemeContext.Provider value={{ theme, toggleTheme: toggletheme }}>
            {children}
        </ThemeContext.Provider>
    )
}

export default ThemeContextProvider;