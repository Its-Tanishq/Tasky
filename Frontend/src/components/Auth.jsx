import React, { useState, useEffect } from 'react';
import { X, Building2, User, UserCheck, LogIn, ChevronLeft, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../utils/api';

const Auth = ({ isOpen, onClose, initialView = 'initial' }) => {
    const [view, setView] = useState('initial');
    const [loginExpanded, setLoginExpanded] = useState(false);
    const [isVisible, setIsVisible] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleOrgCreate = async () => {
        setIsLoading(true);
        try {
            const response = await api.post('/organization/create-organization', {
                name,
                email,
                password
            });
            toast.success(response.data.message);
            setView('login-owner');
        } catch (error) {
            toast.error(error.response?.data?.message || error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const handleOrgLogin = async () => {
        setIsLoading(true);
        try {
            const response = await api.post('/organization/login-organization', {
                email,
                password
            });
            toast.success(response.data.message);
            localStorage.setItem("accessToken", response.data.accessToken);
        } catch (error) {
            toast.error(error.response?.data?.message || error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const handleEmployeeLogin = async () => {
        setIsLoading(true);
        try {
            const response = await api.post('/employee/login', {
                email,
                password
            });
            toast.success(response.data.message);
            localStorage.setItem("accessToken", response.data.accessToken);
        } catch (error) {
            toast.error(error.response?.data?.message || error.message);
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        if (isOpen) {
            setIsVisible(true);
            setView(initialView);
        } else {
            setTimeout(() => {
                setIsVisible(false);
                setLoginExpanded(false);
                setView('initial');
            }, 300);
        }
    }, [isOpen, initialView]);

    useEffect(() => {
        setName('');
        setEmail('');
        setPassword('');
    }, [view]);

    if (!isVisible && !isOpen) return null;

    const handleBack = () => {
        setView('initial');
    };

    const renderCreateOrgForm = () => (
        <form className="flex flex-col gap-5 animate-[fadeIn_0.3s_ease]" onSubmit={(e) => { e.preventDefault(); handleOrgCreate(); }}>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Organization Name</label>
                <div className="relative">
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                        placeholder="e.g. Acme Corp"
                    />
                </div>
            </div>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Email Address</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="john@company.com"
                />
            </div>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="••••••••"
                />
            </div>
            <button
                className={`mt-4 w-full p-4 rounded-xl bg-gradient-to-br from-[var(--primary-color)] to-[var(--accent-color)] text-white border-none text-base font-semibold transition-all duration-300 shadow-[0_4px_15px_rgba(108,92,231,0.3)] ${isLoading ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer hover:-translate-y-[2px] hover:shadow-[0_8px_25px_rgba(108,92,231,0.5)] active:translate-y-0'}`}
                type="submit"
                disabled={isLoading}
            >
                {isLoading ? (
                    <div className="flex items-center justify-center gap-2">
                        <Loader2 className="animate-spin" size={20} />
                        <span>Creating...</span>
                    </div>
                ) : (
                    'Create Organization'
                )}
            </button>
        </form>
    );

    const renderLoginOwnerForm = () => (
        <form className="flex flex-col gap-5 animate-[fadeIn_0.3s_ease]" onSubmit={(e) => { e.preventDefault(); handleOrgLogin(); }}>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Email Address</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="owner@company.com"
                />
            </div>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="••••••••"
                />
            </div>
            <button
                className={`mt-4 w-full p-4 rounded-xl bg-gradient-to-br from-[var(--primary-color)] to-[var(--accent-color)] text-white border-none text-base font-semibold transition-all duration-300 shadow-[0_4px_15px_rgba(108,92,231,0.3)] ${isLoading ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer hover:-translate-y-[2px] hover:shadow-[0_8px_25px_rgba(108,92,231,0.5)] active:translate-y-0'}`}
                type="submit"
                disabled={isLoading}
            >
                {isLoading ? (
                    <div className="flex items-center justify-center gap-2">
                        <Loader2 className="animate-spin" size={20} />
                        <span>Logging in...</span>
                    </div>
                ) : (
                    'Login as Owner'
                )}
            </button>
        </form>
    );

    const renderLoginEmployeeForm = () => (
        <form className="flex flex-col gap-5 animate-[fadeIn_0.3s_ease]" onSubmit={(e) => { e.preventDefault(); handleEmployeeLogin(); }}>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Email Address</label>
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="employee@company.com"
                />
            </div>
            <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-[var(--text-color)] ml-1">Password</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] text-base transition-all duration-200 outline-none focus:border-[var(--primary-color)] focus:bg-[var(--ui-element-bg)] focus:shadow-[0_0_0_4px_rgba(108,92,231,0.1)] placeholder:text-[var(--text-secondary)] placeholder:opacity-70"
                    placeholder="••••••••"
                />
            </div>
            <button
                className={`mt-4 w-full p-4 rounded-xl bg-gradient-to-br from-[var(--primary-color)] to-[var(--accent-color)] text-white border-none text-base font-semibold transition-all duration-300 shadow-[0_4px_15px_rgba(108,92,231,0.3)] ${isLoading ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer hover:-translate-y-[2px] hover:shadow-[0_8px_25px_rgba(108,92,231,0.5)] active:translate-y-0'}`}
                type="submit"
                disabled={isLoading}
            >
                {isLoading ? (
                    <div className="flex items-center justify-center gap-2">
                        <Loader2 className="animate-spin" size={20} />
                        <span>Logging in...</span>
                    </div>
                ) : (
                    'Login as Employee'
                )}
            </button>
        </form>
    );

    const getTitleAndSubtitle = () => {
        switch (view) {
            case 'create-org':
                return { title: 'Create Organization', subtitle: 'Start your journey with Tasky today' };
            case 'login-owner':
                return { title: 'Welcome Back, Owner', subtitle: 'Manage your organization and team' };
            case 'login-employee':
                return { title: 'Employee Login', subtitle: 'Access your tasks and collaborate' };
            default:
                return { title: 'Welcome to Tasky', subtitle: 'Choose how you want to get started' };
        }
    };

    const { title, subtitle } = getTitleAndSubtitle();

    return (
        <div
            className={`fixed top-0 left-0 w-full h-full bg-black/60 backdrop-blur-md z-[1000] flex justify-center items-center transition-opacity duration-300 ${isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'}`}
            onClick={onClose}
        >
            <div
                className={`w-[90%] max-w-[500px] bg-[var(--card-bg)] border border-[var(--glass-border)] rounded-[20px] p-8 shadow-[0_10px_40px_rgba(0,0,0,0.2)] relative overflow-hidden max-h-[90vh] overflow-y-auto transition-all duration-400 ease-[cubic-bezier(0.16,1,0.3,1)] ${isOpen ? 'scale-100 opacity-100' : 'scale-90 opacity-0'}`}
                onClick={(e) => e.stopPropagation()}
            >
                <button
                    className="absolute top-4 right-4 bg-transparent border-none text-[var(--text-secondary)] cursor-pointer w-8 h-8 rounded-full flex justify-center items-center transition-all duration-200 z-10 hover:bg-[var(--ui-element-bg)] hover:text-[var(--text-color)]"
                    onClick={onClose}
                    aria-label="Close"
                >
                    <X size={20} />
                </button>

                {view !== 'initial' && (
                    <button
                        className="text-sm text-[var(--text-secondary)] bg-transparent border-none cursor-pointer p-0 mb-6 flex items-center gap-2 transition-colors duration-200 font-medium hover:text-[var(--primary-color)]"
                        onClick={handleBack}
                    >
                        <ChevronLeft size={16} /> Back
                    </button>
                )}

                <div className="mb-8 text-center">
                    <h2 className="text-[1.8rem] font-bold bg-gradient-to-br from-[var(--primary-color)] to-[var(--accent-color)] bg-clip-text text-transparent mb-2">{title}</h2>
                    <p className="text-[var(--text-secondary)] text-[0.95rem]">{subtitle}</p>
                </div>

                {view === 'initial' ? (
                    <div className="flex flex-col gap-4">
                        {/* Create Org Option */}
                        <div
                            className="bg-[var(--ui-element-bg)] border border-[var(--glass-border)] p-6 rounded-2xl cursor-pointer transition-all duration-300 flex items-center gap-4 relative overflow-hidden hover:bg-[var(--ui-element-bg-secondary)] hover:-translate-y-[2px] hover:border-[var(--primary-color)] hover:shadow-[0_4px_12px_rgba(108,92,231,0.2)] group"
                            onClick={() => setView('create-org')}
                        >
                            <div className="w-12 h-12 rounded-xl bg-[rgba(108,92,231,0.1)] text-[var(--primary-color)] flex justify-center items-center transition-all duration-300 group-hover:bg-[var(--primary-color)] group-hover:text-white">
                                <Building2 size={24} />
                            </div>
                            <div className="flex-1">
                                <div className="font-semibold text-lg mb-1 text-[var(--text-color)]">Create Organization</div>
                                <div className="text-sm text-[var(--text-secondary)]">Set up a new workspace for your team</div>
                            </div>
                        </div>

                        {/* Login Option */}
                        <div className="flex flex-col">
                            <div
                                className={`bg-[var(--ui-element-bg)] border border-[var(--glass-border)] p-6 rounded-2xl cursor-pointer transition-all duration-300 flex items-center gap-4 relative overflow-hidden hover:bg-[var(--ui-element-bg-secondary)] hover:-translate-y-[2px] hover:border-[var(--primary-color)] hover:shadow-[0_4px_12px_rgba(108,92,231,0.2)] group ${loginExpanded ? 'bg-[var(--ui-element-bg-secondary)] border-[var(--primary-color)] shadow-[0_4px_12px_rgba(108,92,231,0.2)]' : ''}`}
                                onClick={() => setLoginExpanded(!loginExpanded)}
                            >
                                <div className={`w-12 h-12 rounded-xl bg-[rgba(108,92,231,0.1)] text-[var(--primary-color)] flex justify-center items-center transition-all duration-300 ${loginExpanded ? 'bg-[var(--primary-color)] text-white' : 'group-hover:bg-[var(--primary-color)] group-hover:text-white'}`}>
                                    <LogIn size={24} />
                                </div>
                                <div className="flex-1">
                                    <div className="font-semibold text-lg mb-1 text-[var(--text-color)]">Login</div>
                                    <div className="text-sm text-[var(--text-secondary)]">Access your existing account</div>
                                </div>
                            </div>

                            {/* Slide Down Options */}
                            <div
                                className={`overflow-hidden transition-all duration-500 ease-[cubic-bezier(0.4,0,0.2,1)] flex flex-col gap-3 ${loginExpanded ? 'max-h-[300px] opacity-100 mt-4' : 'max-h-0 opacity-0 mt-0'}`}
                            >
                                <div
                                    className={`bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] p-4 rounded-xl cursor-pointer flex items-center gap-4 transition-all duration-200 translate-x-[20px] opacity-0 hover:bg-[var(--ui-element-bg)] hover:border-[var(--secondary-color)] group ${loginExpanded ? '!translate-x-0 !opacity-100 delay-100' : ''}`}
                                    onClick={() => setView('login-owner')}
                                >
                                    <div className="w-9 h-9 rounded-lg flex justify-center items-center bg-[rgba(0,206,201,0.1)] text-[var(--secondary-color)] group-hover:bg-[var(--secondary-color)] group-hover:text-black transition-colors">
                                        <UserCheck size={20} />
                                    </div>
                                    <div className="flex-1">
                                        <div className="font-semibold text-[0.95rem] text-[var(--text-color)]">Login as Owner</div>
                                    </div>
                                </div>
                                <div
                                    className={`bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] p-4 rounded-xl cursor-pointer flex items-center gap-4 transition-all duration-200 translate-x-[20px] opacity-0 hover:bg-[var(--ui-element-bg)] hover:border-[var(--secondary-color)] group ${loginExpanded ? '!translate-x-0 !opacity-100 delay-200' : ''}`}
                                    onClick={() => setView('login-employee')}
                                >
                                    <div className="w-9 h-9 rounded-lg flex justify-center items-center bg-[rgba(0,206,201,0.1)] text-[var(--secondary-color)] group-hover:bg-[var(--secondary-color)] group-hover:text-black transition-colors">
                                        <User size={20} />
                                    </div>
                                    <div className="flex-1">
                                        <div className="font-semibold text-[0.95rem] text-[var(--text-color)]">Login as Employee</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div>
                        {view === 'create-org' && renderCreateOrgForm()}
                        {view === 'login-owner' && renderLoginOwnerForm()}
                        {view === 'login-employee' && renderLoginEmployeeForm()}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Auth;
