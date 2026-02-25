
import React from 'react';
import { Bell, Search, User } from 'lucide-react';
import { useUser } from '../../Context/UserContext';


const Header = () => {
    const { user } = useUser();

    return (
        <div className="h-16 bg-[var(--card-bg)]/80 backdrop-blur-md border-b border-[var(--glass-border)] flex items-center justify-between px-8 sticky top-0 z-40">
            <div className="flex items-center gap-4 flex-1">
                <div className="relative w-96">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--text-muted)]" size={18} />
                    <input
                        type="text"
                        placeholder="Search..."
                        className="w-full pl-10 pr-4 py-2 rounded-xl bg-[var(--bg-color)] border border-[var(--glass-border)] focus:outline-none focus:border-[var(--primary-color)] text-[var(--text-color)] placeholder-[var(--text-muted)] transition-all"
                    />
                </div>
            </div>

            <div className="flex items-center gap-6">
                <button className="relative p-2 text-[var(--text-color)] hover:bg-[var(--glass-border)] rounded-full transition-all">
                    <Bell size={20} />
                    <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-[var(--card-bg)]"></span>
                </button>

                <div className="flex items-center gap-3 pl-6 border-l border-[var(--glass-border)]">
                    <div className="text-right hidden md:block">
                        <p className="text-sm font-semibold text-[var(--text-color)]">{user?.name || "User"}</p>
                        <p className="text-xs text-[var(--text-muted)]">{user?.role?.title || user?.role || "Member"}</p>
                    </div>
                    <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[var(--primary-color)] to-[var(--secondary-color)] flex items-center justify-center text-white font-bold shadow-lg">
                        <User size={20} />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Header;
