
import React, { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, X, Check, Shield, ChevronDown, ChevronUp, Loader2 } from 'lucide-react';
import api from '../../utils/api';
import toast from 'react-hot-toast';
import permissionsData from '../../utils/permissions.json';

const Role = () => {
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingRole, setEditingRole] = useState(null);
    const [formData, setFormData] = useState({
        title: '',
        permissions: []
    });
    const [expandedModules, setExpandedModules] = useState({});

    const permissionsByModule = permissionsData.reduce((acc, perm) => {
        if (!acc[perm.module]) {
            acc[perm.module] = [];
        }
        acc[perm.module].push(perm);
        return acc;
    }, {});

    useEffect(() => {
        fetchRoles();
    }, []);

    const fetchRoles = async () => {
        try {
            const response = await api.get('/role/get-all-roles');
            setRoles(response.data);
        } catch (error) {
            toast.error('Failed to fetch roles');
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (role = null) => {
        if (role) {
            setEditingRole(role);
            setFormData({
                title: role.name || role.title,
                permissions: (role.permissions || []).map(permName => {
                    const found = permissionsData.find(p => p.name === permName);
                    return found ? found.id : null;
                }).filter(id => id !== null)
            });
        } else {
            setEditingRole(null);
            setFormData({
                title: '',
                permissions: []
            });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingRole(null);
        setFormData({ title: '', permissions: [] });
    };

    const togglePermission = (permissionId) => {
        setFormData(prev => {
            const newPermissions = prev.permissions.includes(permissionId)
                ? prev.permissions.filter(id => id !== permissionId)
                : [...prev.permissions, permissionId];
            return { ...prev, permissions: newPermissions };
        });
    };

    const toggleModule = (moduleName) => {
        setExpandedModules(prev => ({
            ...prev,
            [moduleName]: !prev[moduleName]
        }));
    };

    const selectAllInModule = (moduleName) => {
        const modulePermissionIds = permissionsByModule[moduleName].map(p => p.id);
        const allSelected = modulePermissionIds.every(id => formData.permissions.includes(id));

        setFormData(prev => {
            if (allSelected) {
                return {
                    ...prev,
                    permissions: prev.permissions.filter(id => !modulePermissionIds.includes(id))
                };
            } else {
                return {
                    ...prev,
                    permissions: [...new Set([...prev.permissions, ...modulePermissionIds])]
                };
            }
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.title.trim()) {
            toast.error("Role name is required");
            return;
        }

        if (formData.permissions.length === 0) {
            toast.error("Select at least one permission");
            return;
        }

        try {
            if (editingRole) {
                await api.put(`/role/update-role/${editingRole.id}`, {
                    name: formData.title,
                    permissionIds: formData.permissions
                });
                toast.success('Role updated successfully');
            } else {
                await api.post('/role/create-role', {
                    name: formData.title,
                    permissionIds: formData.permissions
                });
                toast.success('Role created successfully');
            }
            fetchRoles();
            handleCloseModal();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Operation failed');
        }
    };

    const handleDelete = async (roleId) => {
        if (!window.confirm('Are you sure you want to delete this role?')) return;

        try {
            await api.delete(`/role/delete-role/${roleId}`);
            toast.success('Role deleted successfully');
            fetchRoles();
        } catch (error) {
            toast.error(error.response?.data?.message || 'Failed to delete role');
        }
    };

    return (
        <div className="p-8 max-w-7xl mx-auto">
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent">
                        Roles & Permissions
                    </h1>
                    <p className="text-[var(--text-muted)] mt-2">Manage user roles and access rights</p>
                </div>
                <button
                    onClick={() => handleOpenModal()}
                    className="flex items-center gap-2 px-5 py-2.5 bg-[var(--primary-color)] hover:bg-[var(--primary-color)]/90 text-white rounded-xl transition-all shadow-lg shadow-[var(--primary-color)]/20"
                >
                    <Plus size={20} />
                    <span>Add New Role</span>
                </button>
            </div>

            {loading ? (
                <div className="flex justify-center items-center h-64">
                    <Loader2 className="animate-spin text-[var(--primary-color)]" size={40} />
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {roles.map((role) => (
                        <div key={role.id} className="bg-[var(--card-bg)] border border-[var(--glass-border)] rounded-2xl p-6 shadow-xl backdrop-blur-md hover:-translate-y-1 transition-all duration-300 group">
                            <div className="flex justify-between items-start mb-4">
                                <div className="p-3 bg-[var(--primary-color)]/10 rounded-xl text-[var(--primary-color)]">
                                    <Shield size={24} />
                                </div>
                                {(role.name || role.title) !== 'OWNER' && (
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => handleOpenModal(role)}
                                            className="p-2 text-[var(--text-muted)] hover:text-[var(--primary-color)] hover:bg-[var(--primary-color)]/10 rounded-lg transition-colors"
                                        >
                                            <Edit2 size={18} />
                                        </button>
                                        <button
                                            onClick={() => handleDelete(role.id)}
                                            className="p-2 text-[var(--text-muted)] hover:text-red-500 hover:bg-red-500/10 rounded-lg transition-colors"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                )}
                            </div>

                            <h3 className="text-xl font-bold text-[var(--text-color)] mb-2">{role.name || role.title}</h3>
                            <div className="flex items-center gap-2 text-sm text-[var(--text-muted)] mb-4">
                                <Shield size={14} />
                                <span>{role.permissions?.length || 0} Permissions</span>
                            </div>

                            <div className="mt-4 pt-4 border-t border-[var(--glass-border)]">
                                <div className="flex flex-wrap gap-2">
                                    {(role.permissions || []).slice(0, 3).map((perm, idx) => (
                                        <span key={idx} className="text-xs px-2 py-1 bg-[var(--bg-color)] rounded-md text-[var(--text-secondary)] border border-[var(--glass-border)]">
                                            {typeof perm === 'string' ? perm : (perm.permissionName || perm.name || "Permission")}
                                        </span>
                                    ))}
                                    {(role.permissions?.length || 0) > 3 && (
                                        <span className="text-xs px-2 py-1 bg-[var(--bg-color)] rounded-md text-[var(--text-secondary)] border border-[var(--glass-border)]">
                                            +{(role.permissions?.length || 0) - 3} more
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-2xl rounded-2xl shadow-2xl max-h-[90vh] flex flex-col animate-[fadeIn_0.3s_ease]">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-[var(--text-color)]">
                                {editingRole ? 'Edit Role' : 'Create New Role'}
                            </h2>
                            <button onClick={handleCloseModal} className="text-[var(--text-muted)] hover:text-[var(--text-color)] transition-colors">
                                <X size={24} />
                            </button>
                        </div>

                        <div className="p-6 overflow-y-auto flex-1 custom-scrollbar">
                            <div className="mb-6">
                                <label className="block text-sm font-medium text-[var(--text-color)] mb-2">Role Title</label>
                                <input
                                    type="text"
                                    value={formData.title}
                                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl bg-[var(--bg-color)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:outline-none transition-all"
                                    placeholder="e.g. HR Manager"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-[var(--text-color)] mb-4">Permissions</label>
                                <div className="space-y-4">
                                    {Object.entries(permissionsByModule).map(([module, perms]) => (
                                        <div key={module} className="border border-[var(--glass-border)] rounded-xl overflow-hidden bg-[var(--bg-color)]/30">
                                            <div
                                                className="flex items-center justify-between p-4 bg-[var(--glass-border)]/30 cursor-pointer hover:bg-[var(--glass-border)]/50 transition-colors"
                                            >
                                                <div className="flex items-center gap-3" onClick={() => toggleModule(module)}>
                                                    {expandedModules[module] ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                                                    <span className="font-semibold">{module}</span>
                                                    <span className="text-xs px-2 py-0.5 bg-[var(--primary-color)]/20 text-[var(--primary-color)] rounded-full">
                                                        {perms.filter(p => formData.permissions.includes(p.id)).length}/{perms.length}
                                                    </span>
                                                </div>
                                                <button
                                                    onClick={(e) => { e.stopPropagation(); selectAllInModule(module); }}
                                                    className="text-xs text-[var(--primary-color)] hover:underline"
                                                >
                                                    {perms.every(p => formData.permissions.includes(p.id)) ? 'Deselect All' : 'Select All'}
                                                </button>
                                            </div>

                                            {expandedModules[module] && (
                                                <div className="p-4 grid grid-cols-1 sm:grid-cols-2 gap-3 animate-[fadeIn_0.2s_ease]">
                                                    {perms.map((perm) => (
                                                        <div
                                                            key={perm.id}
                                                            onClick={() => togglePermission(perm.id)}
                                                            className={`flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-all ${formData.permissions.includes(perm.id)
                                                                ? 'bg-[var(--primary-color)]/10 border-[var(--primary-color)]'
                                                                : 'border-[var(--glass-border)] hover:border-[var(--text-secondary)]'
                                                                }`}
                                                        >
                                                            <div className={`mt-0.5 w-5 h-5 rounded border flex items-center justify-center transition-colors ${formData.permissions.includes(perm.id)
                                                                ? 'bg-[var(--primary-color)] border-[var(--primary-color)]'
                                                                : 'border-[var(--text-secondary)]'
                                                                }`}>
                                                                {formData.permissions.includes(perm.id) && <Check size={14} className="text-white" />}
                                                            </div>
                                                            <div>
                                                                <p className="text-sm font-medium text-[var(--text-color)]">{perm.name}</p>
                                                                <p className="text-xs text-[var(--text-muted)] mt-0.5">{perm.description}</p>
                                                            </div>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>

                        <div className="p-6 border-t border-[var(--glass-border)] flex justify-end gap-3">
                            <button
                                onClick={handleCloseModal}
                                className="px-6 py-2.5 rounded-xl border border-[var(--glass-border)] text-[var(--text-color)] hover:bg-[var(--glass-border)] transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleSubmit}
                                className="px-6 py-2.5 rounded-xl bg-[var(--primary-color)] text-white hover:bg-[var(--primary-color)]/90 shadow-lg shadow-[var(--primary-color)]/25 transition-all"
                            >
                                {editingRole ? 'Update Role' : 'Create Role'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Role;
