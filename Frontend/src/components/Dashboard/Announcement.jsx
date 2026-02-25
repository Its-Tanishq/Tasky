import React, { useState, useEffect } from 'react';
import api from '../../utils/api';
import { Loader2, Megaphone, X, Pencil, Trash2, Plus } from 'lucide-react';

const Announcement = () => {
    const [announcements, setAnnouncements] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [editingAnnouncement, setEditingAnnouncement] = useState(null);
    
    const [formData, setFormData] = useState({
        title: '',
        message: ''
    });

    const fetchAnnouncements = async () => {
        setLoading(true);
        try {
            const response = await api.get('/announcement/organization');
            setAnnouncements(response.data || []);
        } catch (error) {
            console.error("Failed to fetch announcements:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAnnouncements();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const openCreateModal = () => {
        setEditingAnnouncement(null);
        setFormData({ title: '', message: '' });
        setIsModalOpen(true);
    };

    const openEditModal = (announcement) => {
        setEditingAnnouncement(announcement);
        setFormData({
            title: announcement.title || '',
            message: announcement.message || ''
        });
        setIsModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!formData.title || !formData.message) {
            alert("Please fill in both title and message.");
            return;
        }

        setIsSubmitting(true);
        try {
            if (editingAnnouncement) {
                await api.put(`/announcement/update/${editingAnnouncement.id}`, formData);
            } else {
                await api.post('/announcement/create', formData);
            }
            fetchAnnouncements();
            setIsModalOpen(false);
            setEditingAnnouncement(null);
        } catch (error) {
            console.error(`Failed to ${editingAnnouncement ? 'update' : 'create'} announcement:`, error);
            alert(error.response?.data?.message || `Failed to ${editingAnnouncement ? 'update' : 'create'} announcement. Please try again.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this announcement?")) {
            try {
                await api.delete(`/announcement/delete/${id}`);
                fetchAnnouncements();
            } catch (error) {
                console.error("Failed to delete announcement:", error);
                alert("Failed to delete announcement. Please try again.");
            }
        }
    };

    return (
        <div className="p-4 md:p-8 max-w-7xl mx-auto w-full animate-in fade-in duration-500">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-3xl md:text-4xl font-extrabold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent transform transition-all hover:scale-105 origin-left duration-300">
                        Announcements
                    </h1>
                    <p className="text-[var(--text-secondary)] mt-2 text-sm md:text-base opacity-90">
                        Broadcast important updates to your organization.
                    </p>
                </div>
                
                <button 
                    onClick={openCreateModal}
                    className="flex items-center justify-center gap-2 w-full sm:w-auto px-5 py-2.5 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white font-semibold rounded-xl shadow-lg hover:shadow-[0_8px_20px_rgba(108,92,231,0.3)] transform hover:-translate-y-0.5 transition-all duration-300 focus:outline-none whitespace-nowrap"
                >
                    <Plus className="w-5 h-5" />
                    New Announcement
                </button>
            </div>
            
            {/* Main Content Area */}
            <div className="bg-[var(--card-bg)] rounded-3xl border border-[var(--glass-border)] shadow-2xl backdrop-blur-xl overflow-hidden transition-all duration-300 relative">
                <div className="absolute top-0 left-0 w-64 h-64 bg-[var(--primary-color)] opacity-5 rounded-full blur-3xl pointer-events-none -translate-x-1/2 -translate-y-1/2"></div>
                <div className="absolute bottom-0 right-0 w-64 h-64 bg-[var(--secondary-color)] opacity-5 rounded-full blur-3xl pointer-events-none translate-x-1/2 translate-y-1/2"></div>
                
                <div className="relative z-10 min-h-[400px]">
                    {loading ? (
                        <div className="flex flex-col justify-center items-center py-24 px-4 h-full">
                            <div className="relative w-16 h-16">
                                <div className="absolute inset-0 border-4 border-[var(--ui-element-border)] rounded-full"></div>
                                <div className="absolute inset-0 border-4 border-[var(--primary-color)] rounded-full border-t-transparent animate-spin"></div>
                            </div>
                            <p className="mt-6 text-[var(--text-secondary)] font-medium md:text-lg animate-pulse">
                                Loading announcements...
                            </p>
                        </div>
                    ) : announcements.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-28 px-4 text-center h-full">
                            <div className="w-24 h-24 mb-6 rounded-3xl bg-gradient-to-br from-[var(--ui-element-bg)] to-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] flex items-center justify-center transform hover:scale-105 transition-all duration-500 shadow-inner group">
                                <Megaphone className="w-12 h-12 text-[var(--text-secondary)] group-hover:text-[var(--primary-color)] transition-colors duration-300" />
                            </div>
                            <h3 className="text-2xl font-bold text-[var(--text-color)] mb-3">No announcements yet</h3>
                            <p className="text-[var(--text-secondary)] max-w-md mx-auto text-base">
                                Create your first announcement to share updates with your organization.
                            </p>
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                            {announcements.map((announcement, index) => (
                                <div key={announcement.id || index} className="group flex flex-col bg-[var(--ui-element-bg)] hover:bg-[var(--ui-element-bg-hover)] border border-[var(--glass-border)] rounded-2xl p-6 transition-all duration-300 hover:shadow-lg hover:-translate-y-1 relative overflow-hidden h-full">
                                     <div className="absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex gap-2 justify-end z-10 bg-[var(--ui-element-bg)] p-1 rounded-xl shadow-md border border-[var(--glass-border)]">
                                        <button 
                                            onClick={() => openEditModal(announcement)}
                                            className="p-1.5 rounded-lg text-[var(--secondary-color)] hover:bg-[var(--card-bg)] transition-colors focus:outline-none"
                                            title="Edit Announcement"
                                        >
                                            <Pencil size={16} />
                                        </button>
                                        <button 
                                            onClick={() => handleDelete(announcement.id)}
                                            className="p-1.5 rounded-lg text-rose-500 hover:bg-[var(--card-bg)] transition-colors focus:outline-none"
                                            title="Delete Announcement"
                                        >
                                            <Trash2 size={16} />
                                        </button>
                                    </div>
                                    <div className="mb-4 pr-16 flex-grow">
                                        <h3 className="text-lg font-bold text-[var(--text-color)] mb-2 line-clamp-2">
                                            {announcement.title}
                                        </h3>
                                        <div className="w-12 h-1 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] rounded-full mb-3"></div>
                                        <p className="text-[var(--text-secondary)] text-sm line-clamp-4 leading-relaxed whitespace-pre-wrap">
                                            {announcement.message}
                                        </p>
                                    </div>
                                    <div className="mt-auto pt-4 flex items-center justify-between text-xs text-[var(--text-secondary)] border-t border-[var(--glass-border)]">
                                        <div className="flex items-center gap-1.5 opacity-70">
                                           <Megaphone size={14} />
                                           <span>Announcement</span>
                                        </div>
                                        {announcement.createdAt && (
                                            <span className="opacity-70">
                                                {new Date(announcement.createdAt).toLocaleDateString()}
                                            </span>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Create/Edit Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4 animate-in fade-in duration-200">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-xl rounded-2xl shadow-2xl flex flex-col transform transition-all scale-100 opacity-100">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <div className="flex items-center gap-3">
                                <div className="p-2 bg-gradient-to-br from-[var(--primary-color)] to-[var(--secondary-color)] rounded-lg text-white">
                                    <Megaphone size={20} />
                                </div>
                                <h2 className="text-2xl font-bold text-[var(--text-color)]">
                                    {editingAnnouncement ? 'Edit Announcement' : 'New Announcement'}
                                </h2>
                            </div>
                            <button 
                                onClick={() => setIsModalOpen(false)} 
                                className="text-[var(--text-secondary)] hover:text-[var(--text-color)] hover:bg-[var(--ui-element-bg)] p-2 rounded-xl transition-all focus:outline-none"
                            >
                                <X size={20} />
                            </button>
                        </div>

                        <form onSubmit={handleSubmit} className="flex flex-col">
                            <div className="p-6 space-y-5">
                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Title</label>
                                    <input
                                        type="text"
                                        name="title"
                                        value={formData.title}
                                        onChange={handleInputChange}
                                        required
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                        placeholder="Enter announcement title"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Message</label>
                                    <textarea
                                        name="message"
                                        value={formData.message}
                                        onChange={handleInputChange}
                                        required
                                        rows="5"
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all resize-none"
                                        placeholder="Type the announcement details here..."
                                    ></textarea>
                                </div>
                            </div>
                            
                            <div className="p-6 border-t border-[var(--glass-border)] flex justify-end gap-3 rounded-b-2xl bg-[var(--section-bg)] bg-opacity-30">
                                <button
                                    type="button"
                                    onClick={() => setIsModalOpen(false)}
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
                                        <><Loader2 size={18} className="animate-spin" /> {editingAnnouncement ? 'Updating...' : 'Publishing...'}</>
                                    ) : (
                                        editingAnnouncement ? 'Update' : 'Publish'
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

export default Announcement;
