import React, { useState, useEffect } from 'react';
import api from '../../utils/api';
import { Loader2, Users, X } from 'lucide-react';

const Team = () => {
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [editingTeam, setEditingTeam] = useState(null);
    const [formData, setFormData] = useState({ name: '' });

    const fetchTeams = async () => {
        try {
            const response = await api.get('/team/all');
            setTeams(response.data);
        } catch (error) {
            console.error("Failed to fetch teams:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTeams();
    }, []);

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this team? This action cannot be undone.")) {
            try {
                await api.delete(`/team/delete/${id}`);
                fetchTeams();
            } catch (error) {
                console.error("Failed to delete team:", error);
                alert("Failed to delete team. Please try again.");
            }
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleEditOpen = (team) => {
        setFormData({ name: team.name || '' });
        setEditingTeam(team);
        setIsCreateModalOpen(true);
    };

    const openCreateModal = () => {
        setEditingTeam(null);
        setFormData({ name: '' });
        setIsCreateModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.name) {
            alert("Please provide a team name.");
            return;
        }

        setIsSubmitting(true);
        try {
            if (editingTeam) {
                await api.put(`/team/update/${editingTeam.id}`, formData);
            } else {
                await api.post('/team/create', formData);
            }
            fetchTeams();
            setIsCreateModalOpen(false);
            setEditingTeam(null);
            setFormData({ name: '' });
        } catch (error) {
            console.error(`Failed to ${editingTeam ? 'update' : 'create'} team:`, error);
            alert(error.response?.data?.message || `Failed to ${editingTeam ? 'update' : 'create'} team. Please try again.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    const getInitials = (name) => {
        if (!name) return '?';
        return name.charAt(0).toUpperCase();
    };

    return (
        <div className="p-4 md:p-8 max-w-7xl mx-auto w-full animate-in fade-in duration-500">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-3xl md:text-4xl font-extrabold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent transform transition-all hover:scale-105 origin-left duration-300">
                        Teams
                    </h1>
                    <p className="text-[var(--text-secondary)] mt-2 text-sm md:text-base opacity-90">
                        Manage your organization's teams and their members.
                    </p>
                </div>
                
                {/* Create Team Button */}
                <button 
                    onClick={openCreateModal}
                    className="flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white font-semibold rounded-xl shadow-lg hover:shadow-[0_8px_20px_rgba(108,92,231,0.3)] transform hover:-translate-y-0.5 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-transparent active:scale-95 border border-transparent"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2.5" d="M12 4v16m8-8H4"></path>
                    </svg>
                    New Team
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
                                Fetching teams...
                            </p>
                        </div>
                    ) : teams.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-28 px-4 text-center">
                            <div className="w-24 h-24 mb-6 rounded-3xl bg-gradient-to-br from-[var(--ui-element-bg)] to-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] flex items-center justify-center transform rotate-3 hover:-rotate-3 transition-all duration-500 shadow-inner group">
                                <Users className="w-12 h-12 text-[var(--text-secondary)] group-hover:text-[var(--primary-color)] transition-colors duration-300" />
                            </div>
                            <h3 className="text-2xl font-bold text-[var(--text-color)] mb-3">No teams found</h3>
                            <p className="text-[var(--text-secondary)] max-w-md mx-auto text-base">
                                Your organization currently has no teams. Create your first team to get started.
                            </p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto custom-scrollbar">
                            <table className="w-full text-left whitespace-nowrap">
                                <thead>
                                    <tr className="bg-[var(--section-bg)] bg-opacity-50 border-b border-[var(--glass-border)]">
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Team Name</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Created By</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Created At</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-[var(--glass-border)]">
                                    {teams.map((team, index) => (
                                        <tr 
                                            key={team.id || index} 
                                            className="hover:bg-[var(--ui-element-bg)] transition-all duration-200 group relative"
                                        >
                                            <td className="px-6 py-5">
                                                <div className="flex items-center gap-4">
                                                    <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[var(--primary-color)] to-[var(--secondary-color)] flex items-center justify-center text-white text-lg font-bold shadow-lg shadow-indigo-500/20 transform group-hover:scale-105 group-hover:rotate-3 transition-all duration-300">
                                                        {getInitials(team.name)}
                                                    </div>
                                                    <div>
                                                        <div className="text-base font-bold text-[var(--text-color)] group-hover:text-[var(--primary-color)] transition-colors duration-200">
                                                            {team.name || "Unknown Team"}
                                                        </div>
                                                        <div className="text-sm text-[var(--text-secondary)] mt-0.5 group-hover:text-[var(--text-color)] transition-colors">
                                                            ID: {team.id}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="inline-flex items-center px-4 py-1.5 rounded-full text-sm font-medium bg-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] text-[var(--text-color)] shadow-sm group-hover:border-[var(--primary-color)] group-hover:border-opacity-30 transition-all duration-300">
                                                    {team.createdBy || "System"}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="text-sm text-[var(--text-secondary)] font-medium">
                                                    {team.createdAt ? new Date(team.createdAt).toLocaleDateString() : 'N/A'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="flex items-center justify-end gap-2 opacity-50 group-hover:opacity-100 transition-opacity duration-300">
                                                    <button 
                                                        onClick={() => handleEditOpen(team)}
                                                        className="p-2 rounded-xl text-[var(--secondary-color)] hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[var(--secondary-color)]/50"
                                                        title="Edit Team"
                                                    >
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                                                        </svg>
                                                    </button>
                                                    <button 
                                                        onClick={() => handleDelete(team.id)}
                                                        className="p-2 rounded-xl text-rose-500 hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-rose-500/50"
                                                        title="Delete Team"
                                                    >
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                                                        </svg>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* Create / Edit Team Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4 animate-in fade-in duration-200">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-lg rounded-2xl shadow-2xl flex flex-col transform transition-all scale-100 opacity-100">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-[var(--text-color)]">
                                {editingTeam ? 'Edit Team' : 'Create New Team'}
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
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Team Name</label>
                                    <input
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleInputChange}
                                        required
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                        placeholder="e.g. Engineering Team"
                                    />
                                    {editingTeam && (
                                        <p className="mt-2 text-xs text-[var(--text-secondary)] italic ml-1">
                                            Currently only team name can be updated.
                                        </p>
                                    )}
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
                                        <><Loader2 size={18} className="animate-spin" /> {editingTeam ? 'Updating...' : 'Creating...'}</>
                                    ) : (
                                        editingTeam ? "Update Team" : "Create Team"
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

export default Team;
