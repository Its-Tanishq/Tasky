
import React from 'react';
import { NavLink } from 'react-router-dom';
import {
    Megaphone,
    Users,
    UserCog,
    CheckSquare,
    Briefcase,
    Settings,
    LogOut
} from 'lucide-react';

const Sidebar = () => {
    const menuItems = [
        { path: 'announcement', icon: <Megaphone size={20} />, label: 'Announcement' },
        { path: 'employee', icon: <Users size={20} />, label: 'Employee' },
        { path: 'role', icon: <UserCog size={20} />, label: 'Role' },
        { path: 'task', icon: <CheckSquare size={20} />, label: 'Task' },
        { path: 'team', icon: <Briefcase size={20} />, label: 'Team' },
        { path: 'settings', icon: <Settings size={20} />, label: 'Settings' },
    ];

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/';
    };

    return (
        <div className="h-screen w-64 bg-[var(--card-bg)] border-r border-[var(--glass-border)] flex flex-col backdrop-blur-md fixed left-0 top-0 z-50 transition-all duration-300">
            <div className="p-6 flex items-center justify-center border-b border-[var(--glass-border)]">
                <h1 className="text-2xl font-bold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent">
                    Tasky
                </h1>
            </div>

            <nav className="flex-1 px-4 py-6 space-y-2 overflow-y-auto">
                {menuItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        className={({ isActive }) => `
              flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group
              ${isActive
                                ? 'bg-[var(--primary-color)] text-white shadow-lg shadow-[var(--primary-color)]/20'
                                : 'text-[var(--text-color)] hover:bg-[var(--glass-border)] hover:text-[var(--primary-color)]'
                            }
            `}
                    >
                        {item.icon}
                        <span className="font-medium">{item.label}</span>
                    </NavLink>
                ))}
            </nav>

            <div className="p-4 border-t border-[var(--glass-border)]">
                <button
                    onClick={handleLogout}
                    className="w-full flex items-center justify-center gap-2 px-4 py-3 text-red-500 hover:bg-red-500/10 rounded-xl transition-all duration-200 font-medium"
                >
                    <LogOut size={20} />
                    <span>Logout</span>
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
