
import React from 'react';

const Settings = () => {
    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent mb-6">
                Settings
            </h1>
            <div className="bg-[var(--card-bg)] rounded-xl border border-[var(--glass-border)] p-6 shadow-xl backdrop-blur-md">
                <p className="text-[var(--text-muted)]">Configure application settings here.</p>
            </div>
        </div>
    );
};

export default Settings;
