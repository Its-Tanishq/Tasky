
import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Dashboard/Sidebar';
import Header from '../components/Dashboard/Header';

const Dashboard = () => {
    return (
        <div className="flex bg-[var(--bg-color)] min-h-screen text-[var(--text-color)] font-sans">
            <Sidebar />

            <div className="flex-1 flex flex-col ml-64 min-h-screen transition-all duration-300">
                <Header />

                <main className="flex-1 overflow-x-hidden overflow-y-auto bg-[var(--bg-color)]/50">
                    <div className="container mx-auto">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    );
};

export default Dashboard;
