import React, { useState, useEffect } from 'react';
import api from '../../utils/api';
import { X, Loader2, Users } from 'lucide-react';
import { useUser } from '../../Context/UserContext';

const Employee = () => {
    const { user } = useUser();
    const domain = user?.orgName ? `@${user.orgName.toLowerCase().replace(/\s+/g, '')}.com` : '@example.com';

    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [roles, setRoles] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [editingEmployee, setEditingEmployee] = useState(null);
    const [teams, setTeams] = useState([]);
    const [isTeamModalOpen, setIsTeamModalOpen] = useState(false);
    const [teamManageEmp, setTeamManageEmp] = useState(null);
    const [selectedTeamId, setSelectedTeamId] = useState('');
    const [formData, setFormData] = useState({
        name: '',
        emailPrefix: '',
        password: '',
        role: ''
    });

    const fetchEmployees = async () => {
        try {
            const response = await api.get('/employee/allEmployees');
            setEmployees(response.data);
        } catch (error) {
            console.error("Failed to fetch employees:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEmployees();
        
        const fetchRoles = async () => {
            try {
                const response = await api.get('/role/get-all-roles');
                const filteredRoles = response.data.filter(r => (r.name || r.title || r) !== 'OWNER');
                setRoles(filteredRoles);
                if (filteredRoles.length > 0) {
                    const firstRole = filteredRoles[0].name || filteredRoles[0].title || filteredRoles[0];
                    setFormData(prev => ({ ...prev, role: typeof firstRole === 'string' ? firstRole : '' }));
                }
            } catch (error) {
                console.error("Failed to fetch roles:", error);
            }
        };
        
        const fetchTeams = async () => {
            try {
                const response = await api.get('/team/all');
                setTeams(response.data);
            } catch (error) {
                console.error("Failed to fetch teams:", error);
            }
        };

        fetchRoles();
        fetchTeams();
    }, []);

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this employee? This action cannot be undone.")) {
            try {
                await api.delete(`/employee/delete/${id}`);
                fetchEmployees();
            } catch (error) {
                console.error("Failed to delete employee:", error);
                alert("Failed to delete employee. Please try again.");
            }
        }
    };

    const getInitials = (name) => {
        if (!name) return '?';
        return name.charAt(0).toUpperCase();
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleManageTeamOpen = (emp) => {
        setTeamManageEmp(emp);
        setSelectedTeamId('');
        setIsTeamModalOpen(true);
    };

    const handleTeamAction = async (action) => {
        if (action === 'add' && !selectedTeamId) {
            alert("Please select a team");
            return;
        }
        setIsSubmitting(true);
        try {
            if (action === 'add') {
                await api.put(`/team/add?teamId=${selectedTeamId}&empId=${teamManageEmp.id}`);
            } else if (action === 'remove') {
                await api.put(`/team/remove?empId=${teamManageEmp.id}`);
            }
            fetchEmployees();
            setIsTeamModalOpen(false);
        } catch (error) {
            console.error(`Failed to ${action} team:`, error);
            alert(error.response?.data?.message || `Failed to ${action} team. Please try again.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEditOpen = (emp) => {
        const [prefix] = emp.email ? emp.email.split('@') : [''];
        
        setFormData({
            name: emp.name || '',
            emailPrefix: prefix || '',
            password: '',
            role: emp.role || (roles.length > 0 ? (roles[0].name || roles[0].title || roles[0]) : '')
        });
        setEditingEmployee(emp);
        setIsCreateModalOpen(true);
    };

    const openCreateModal = () => {
        setEditingEmployee(null);
        setFormData({ 
            name: '', emailPrefix: '', password: '', 
            role: roles.length > 0 ? (roles[0].name || roles[0].title || roles[0]) : '' 
        });
        setIsCreateModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.name || !formData.emailPrefix || (!editingEmployee && !formData.password) || !formData.role) {
            alert("Please fill in all fields.");
            return;
        }

        const fullEmail = formData.emailPrefix + domain;
        const submitData = {
            ...formData,
            email: fullEmail
        };

        setIsSubmitting(true);
        try {
            if (editingEmployee) {
                await api.put(`/employee/update/${editingEmployee.id}`, submitData);
            } else {
                await api.post('/employee/create', submitData);
            }
            fetchEmployees();
            setIsCreateModalOpen(false);
            setEditingEmployee(null);
            setFormData({ 
                name: '', emailPrefix: '', password: '', 
                role: roles.length > 0 ? (roles[0].name || roles[0].title || roles[0]) : '' 
            });
        } catch (error) {
            console.error(`Failed to ${editingEmployee ? 'update' : 'create'} employee:`, error);
            alert(error.response?.data?.message || `Failed to ${editingEmployee ? 'update' : 'create'} employee. Please try again.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="p-4 md:p-8 max-w-7xl mx-auto w-full animate-in fade-in duration-500">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-3xl md:text-4xl font-extrabold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent transform transition-all hover:scale-105 origin-left duration-300">
                        Team Members
                    </h1>
                    <p className="text-[var(--text-secondary)] mt-2 text-sm md:text-base opacity-90">
                        Manage your organization's employees and their roles.
                    </p>
                </div>
                
                {/* Add Employee Button */}
                <button 
                    onClick={openCreateModal}
                    className="flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white font-semibold rounded-xl shadow-lg hover:shadow-[0_8px_20px_rgba(108,92,231,0.3)] transform hover:-translate-y-0.5 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-transparent active:scale-95 border border-transparent"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2.5" d="M12 4v16m8-8H4"></path>
                    </svg>
                    New Member
                </button>
            </div>
            
            {/* Main Content Area */}
            <div className="bg-[var(--card-bg)] rounded-3xl border border-[var(--glass-border)] shadow-2xl backdrop-blur-xl overflow-hidden transition-all duration-300 relative">
                
                {/* Glowing subtle background effects */}
                <div className="absolute top-0 right-0 w-64 h-64 bg-[var(--primary-color)] opacity-5 rounded-full blur-3xl pointer-events-none -translate-y-1/2 translate-x-1/2"></div>
                <div className="absolute bottom-0 left-0 w-64 h-64 bg-[var(--secondary-color)] opacity-5 rounded-full blur-3xl pointer-events-none translate-y-1/2 -translate-x-1/2"></div>
                
                <div className="relative z-10">
                    {loading ? (
                        <div className="flex flex-col justify-center items-center py-24 px-4">
                            <div className="relative w-16 h-16">
                                <div className="absolute inset-0 border-4 border-[var(--ui-element-border)] rounded-full"></div>
                                <div className="absolute inset-0 border-4 border-[var(--primary-color)] rounded-full border-t-transparent animate-spin"></div>
                            </div>
                            <p className="mt-6 text-[var(--text-secondary)] font-medium tracking-wide animate-pulse">
                                Fetching team roster...
                            </p>
                        </div>
                    ) : employees.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-28 px-4 text-center">
                            <div className="w-24 h-24 mb-6 rounded-3xl bg-gradient-to-br from-[var(--ui-element-bg)] to-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] flex items-center justify-center transform rotate-3 hover:-rotate-3 transition-all duration-500 shadow-inner group">
                                <svg className="w-12 h-12 text-[var(--text-secondary)] group-hover:text-[var(--primary-color)] transition-colors duration-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                                </svg>
                            </div>
                            <h3 className="text-2xl font-bold text-[var(--text-color)] mb-3">No employees found</h3>
                            <p className="text-[var(--text-secondary)] max-w-md mx-auto text-base">
                                Your organization currently has no staff members. Add your first employee to get started and build your team.
                            </p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto custom-scrollbar">
                            <table className="w-full text-left whitespace-nowrap">
                                <thead>
                                    <tr className="bg-[var(--section-bg)] bg-opacity-50 border-b border-[var(--glass-border)]">
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Member details</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Role</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Status</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-[var(--glass-border)]">
                                    {employees.map((emp, index) => (
                                        <tr 
                                            key={index} 
                                            className="hover:bg-[var(--ui-element-bg)] transition-all duration-200 group relative"
                                        >
                                            <td className="px-6 py-5">
                                                <div className="flex items-center gap-4">
                                                    <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[var(--primary-color)] to-[var(--secondary-color)] flex items-center justify-center text-white text-lg font-bold shadow-lg shadow-indigo-500/20 transform group-hover:scale-105 group-hover:rotate-3 transition-all duration-300">
                                                        {getInitials(emp.name)}
                                                    </div>
                                                    <div>
                                                        <div className="text-base font-bold text-[var(--text-color)] group-hover:text-[var(--primary-color)] transition-colors duration-200">
                                                            {emp.name || "Unknown"}
                                                        </div>
                                                        <div className="text-sm text-[var(--text-secondary)] mt-0.5 group-hover:text-[var(--text-color)] transition-colors">
                                                            {emp.email}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="inline-flex items-center px-4 py-1.5 rounded-full text-xs font-bold bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] tracking-widest shadow-sm group-hover:border-[var(--primary-color)] group-hover:border-opacity-30 transition-all duration-300">
                                                    {emp.role || "UNASSIGNED"}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="flex items-center gap-2.5">
                                                    <span className="relative flex h-3 w-3">
                                                      {emp.isActive && <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>}
                                                      <span className={`relative inline-flex rounded-full h-3 w-3 ${emp.isActive ? 'bg-emerald-500' : 'bg-rose-500'}`}></span>
                                                    </span>
                                                    <span className="text-sm text-[var(--text-secondary)] font-semibold group-hover:text-[var(--text-color)] transition-colors">
                                                        {emp.isActive ? 'Active' : 'Inactive'}
                                                    </span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                {emp.role === 'OWNER' ? (
                                                    <div className="flex items-center justify-end w-full pr-3 text-[var(--text-secondary)] opacity-50" title="Owner cannot be modified">
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                                                        </svg>
                                                    </div>
                                                ) : (
                                                    <div className="flex items-center justify-end gap-2 opacity-50 group-hover:opacity-100 transition-opacity duration-300">
                                                        <button 
                                                            onClick={() => handleManageTeamOpen(emp)}
                                                            className="p-2 rounded-xl text-[var(--primary-color)] hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)]/50"
                                                            title="Manage Team"
                                                        >
                                                            <Users className="w-5 h-5" />
                                                        </button>
                                                        <button 
                                                            onClick={() => handleEditOpen(emp)}
                                                            className="p-2 rounded-xl text-[var(--secondary-color)] hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[var(--secondary-color)]/50"
                                                            title="Edit Member"
                                                        >
                                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                                                            </svg>
                                                        </button>
                                                        <button 
                                                            onClick={() => handleDelete(emp.id)}
                                                            className="p-2 rounded-xl text-rose-500 hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-rose-500/50"
                                                            title="Delete Member"
                                                        >
                                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                                                            </svg>
                                                        </button>
                                                    </div>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* Manage Team Modal */}
            {isTeamModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4 animate-in fade-in duration-200">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-sm rounded-2xl shadow-2xl flex flex-col transform transition-all scale-100 opacity-100">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <h2 className="text-xl font-bold text-[var(--text-color)]">
                                Manage Team 
                            </h2>
                            <button 
                                onClick={() => setIsTeamModalOpen(false)} 
                                className="text-[var(--text-secondary)] hover:text-[var(--text-color)] hover:bg-[var(--ui-element-bg)] p-2 rounded-xl transition-all focus:outline-none"
                            >
                                <X size={20} />
                            </button>
                        </div>

                        <div className="p-6 space-y-5">
                            <div className="text-sm font-medium text-[var(--text-color)]">
                                Assign <span className="font-bold text-[var(--primary-color)]">{teamManageEmp?.name}</span> to a team.
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Select Team</label>
                                <div className="relative">
                                    <select
                                        value={selectedTeamId}
                                        onChange={(e) => setSelectedTeamId(e.target.value)}
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                    >
                                        <option value="" disabled className="bg-[var(--card-bg)]">Select a team</option>
                                        {teams.map((t) => (
                                            <option key={t.id} value={t.id} className="bg-[var(--card-bg)]">
                                                {t.name}
                                            </option>
                                        ))}
                                    </select>
                                    <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div className="p-6 border-t border-[var(--glass-border)] flex flex-col gap-3 rounded-b-2xl bg-[var(--section-bg)] bg-opacity-30">
                            <button
                                type="button"
                                onClick={() => handleTeamAction('add')}
                                disabled={isSubmitting || !selectedTeamId}
                                className="w-full flex justify-center items-center gap-2 px-6 py-2.5 rounded-xl font-medium bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white hover:opacity-90 shadow-lg transition-all focus:outline-none disabled:opacity-70 disabled:cursor-not-allowed"
                            >
                                {isSubmitting && selectedTeamId ? <Loader2 size={18} className="animate-spin" /> : 'Assign to Team'}
                            </button>
                            <button
                                type="button"
                                onClick={() => handleTeamAction('remove')}
                                disabled={isSubmitting}
                                className="w-full flex justify-center items-center gap-2 px-6 py-2.5 rounded-xl font-medium border border-rose-500/30 text-rose-500 hover:bg-rose-500/10 transition-colors focus:outline-none disabled:opacity-70 disabled:cursor-not-allowed"
                            >
                                {isSubmitting && !selectedTeamId ? <Loader2 size={18} className="animate-spin" /> : 'Remove from Team'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Employee Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4 animate-in fade-in duration-200">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-lg rounded-2xl shadow-2xl flex flex-col transform transition-all scale-100 opacity-100">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-[var(--text-color)]">
                                {editingEmployee ? 'Edit Member' : 'Create New Member'}
                            </h2>
                            <button 
                                onClick={() => setIsCreateModalOpen(false)} 
                                className="text-[var(--text-secondary)] hover:text-[var(--text-color)] hover:bg-[var(--ui-element-bg)] p-2 rounded-xl transition-all focus:outline-none"
                            >
                                <X size={20} />
                            </button>
                        </div>

                        <form onSubmit={handleSubmit} className="flex flex-col flex-1">
                            <div className="p-6 space-y-5 overflow-y-auto max-h-[60vh] custom-scrollbar">
                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Full Name</label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleInputChange}
                                        required
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                        placeholder="e.g. John Doe"
                                    />
                                </div>
                                
                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Email Address</label>
                                    <div className="flex">
                                        <input
                                            type="text"
                                            name="emailPrefix"
                                            value={formData.emailPrefix}
                                            onChange={handleInputChange}
                                            required
                                            className="w-full px-4 py-3 rounded-l-xl bg-[var(--ui-element-bg)] border border-r-0 border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                            placeholder="john.doe"
                                        />
                                        <div className="flex items-center px-4 py-3 bg-[var(--section-bg)] bg-opacity-50 border border-[var(--glass-border)] rounded-r-xl text-[var(--text-secondary)] font-medium">
                                            {domain}
                                        </div>
                                    </div>
                                </div>

                                {!editingEmployee && (
                                    <div>
                                        <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Password</label>
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleInputChange}
                                            required={!editingEmployee}
                                            className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                            placeholder="Enter a strong password"
                                        />
                                    </div>
                                )}
                                
                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Role</label>
                                    <div className="relative">
                                        <select
                                            name="role"
                                            value={formData.role}
                                            onChange={handleInputChange}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                        >
                                            <option value="" disabled className="bg-[var(--card-bg)]">Select a role</option>
                                            {roles.map((r, i) => (
                                                <option key={i} value={r.name || r.title || r} className="bg-[var(--card-bg)]">
                                                    {r.name || r.title || r}
                                                </option>
                                            ))}
                                        </select>
                                        <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div className="p-6 border-t border-[var(--glass-border)] flex justify-end gap-3 rounded-b-2xl bg-[var(--section-bg)] bg-opacity-30">
                                <button
                                    type="button"
                                    onClick={() => setIsCreateModalOpen(false)}
                                    className="px-6 py-2.5 rounded-xl font-medium border border-[var(--glass-border)] text-[var(--text-color)] hover:bg-[var(--ui-element-bg)] transition-colors focus:outline-none"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className="flex items-center gap-2 px-6 py-2.5 rounded-xl font-medium bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white hover:opacity-90 shadow-lg transition-all focus:outline-none disabled:opacity-70 disabled:cursor-not-allowed"
                                >
                                    {isSubmitting ? (
                                        <><Loader2 size={18} className="animate-spin" /> {editingEmployee ? 'Updating...' : 'Creating...'}</>
                                    ) : (
                                        editingEmployee ? "Update Member" : "Create Member"
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Employee;
