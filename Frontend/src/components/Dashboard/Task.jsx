
import React, { useState, useEffect } from 'react';
import api from '../../utils/api';
import { Loader2, KanbanSquare, X } from 'lucide-react';

const Task = () => {
    const [tasks, setTasks] = useState([]);
    const [employees, setEmployees] = useState([]);
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [editingTask, setEditingTask] = useState(null);
    const [filter, setFilter] = useState('ALL');
    
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        priority: 'MEDIUM',
        status: 'PENDING',
        assignType: 'EMPLOYEE',
        assignedEmployeeId: '',
        assignedTeamId: '',
        dueDate: ''
    });

    const fetchTasks = async (currentFilter = filter) => {
        setLoading(true);
        try {
            let endpoint = '/task/get';
            switch (currentFilter) {
                case 'LOW_PRIORITY': endpoint = '/task/getLowPriorityTask'; break;
                case 'MEDIUM_PRIORITY': endpoint = '/task/getMediumPriorityTask'; break;
                case 'HIGH_PRIORITY': endpoint = '/task/getHighPriorityTask'; break;
                case 'PENDING': endpoint = '/task/getPendingTasks'; break;
                case 'IN_PROGRESS': endpoint = '/task/getInProgressTasks'; break;
                case 'COMPLETED': endpoint = '/task/getCompletedTasks'; break;
                case 'CANCELLED': endpoint = '/task/getCancelledTasks'; break;
                case 'TEAM': endpoint = '/task/getAssignedTypeTeam'; break;
                case 'EMPLOYEE': endpoint = '/task/getAssignedTypeEmployee'; break;
                default: endpoint = '/task/get'; break;
            }
            const response = await api.get(endpoint);
            setTasks(response.data || []);
        } catch (error) {
            console.error(`Failed to fetch tasks for filter ${currentFilter}:`, error);
        } finally {
            setLoading(false);
        }
    };

    const fetchDependencies = async () => {
        try {
            const [empRes, teamRes] = await Promise.all([
                api.get('/employee/allEmployees'),
                api.get('/team/all')
            ]);
            setEmployees(empRes.data || []);
            setTeams(teamRes.data || []);
        } catch (error) {
            console.error("Failed to fetch dependencies:", error);
        }
    };

    useEffect(() => {
        fetchDependencies();
    }, []);

    useEffect(() => {
        if (filter) {
            fetchTasks(filter);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [filter]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const openCreateModal = () => {
        setEditingTask(null);
        setFormData({
            title: '',
            description: '',
            priority: 'MEDIUM',
            status: 'PENDING',
            assignType: 'EMPLOYEE',
            assignedEmployeeId: '',
            assignedTeamId: '',
            dueDate: ''
        });
        setIsCreateModalOpen(true);
    };

    const openEditModal = (task) => {
        setEditingTask(task);
        
        let formattedDate = '';
        if (task.dueDate) {
            try {
                formattedDate = task.dueDate.includes('T') ? task.dueDate.substring(0, 16) : new Date(task.dueDate).toISOString().substring(0, 16);
            } catch (e) {
                formattedDate = '';
            }
        }

        setFormData({
            title: task.title || '',
            description: task.description || '',
            priority: task.priority || 'MEDIUM',
            status: task.status || 'PENDING',
            assignType: task.assignType || 'EMPLOYEE',
            assignedEmployeeId: task.assignedEmployeeId || '',
            assignedTeamId: task.assignedTeamId || '',
            dueDate: formattedDate
        });
        setIsCreateModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.title || !formData.description || !formData.dueDate) {
            alert("Please fill in essential fields: title, description, and due date.");
            return;
        }

        const payload = {
            title: formData.title,
            description: formData.description,
            priority: formData.priority,
            status: formData.status,
            assignType: formData.assignType,
            dueDate: formData.dueDate
        };

        if (formData.assignType === 'EMPLOYEE') {
            if (!formData.assignedEmployeeId) {
                alert("Please select an employee.");
                return;
            }
            payload.assignedEmployeeId = parseInt(formData.assignedEmployeeId);
        } else {
            if (!formData.assignedTeamId) {
                alert("Please select a team.");
                return;
            }
            payload.assignedTeamId = parseInt(formData.assignedTeamId);
        }

        setIsSubmitting(true);
        try {
            if (editingTask) {
                await api.put(`/task/update/${editingTask.id}`, payload);
            } else {
                await api.post('/task/create', payload);
            }
            fetchTasks(filter);
            setIsCreateModalOpen(false);
            setEditingTask(null);
        } catch (error) {
            console.error(`Failed to ${editingTask ? 'update' : 'create'} task:`, error);
            alert(error.response?.data?.message || `Failed to ${editingTask ? 'update' : 'create'} task. Please try again.`);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this task?")) {
            try {
                await api.delete(`/task/delete/${id}`);
                fetchTasks(filter);
            } catch (error) {
                console.error("Failed to delete task:", error);
                alert("Failed to delete task. Please try again.");
            }
        }
    };

    const getPriorityColor = (priority) => {
        switch (priority) {
            case 'URGENT': return 'text-red-500 bg-red-500/10 border-red-500/20';
            case 'HIGH': return 'text-orange-500 bg-orange-500/10 border-orange-500/20';
            case 'MEDIUM': return 'text-blue-500 bg-blue-500/10 border-blue-500/20';
            case 'LOW': return 'text-emerald-500 bg-emerald-500/10 border-emerald-500/20';
            default: return 'text-gray-500 bg-gray-500/10 border-gray-500/20';
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'DONE': return 'text-emerald-500';
            case 'IN_PROGRESS': return 'text-blue-500';
            case 'CANCELLED': return 'text-rose-500';
            default: return 'text-amber-500'; 
        }
    };

    return (
        <div className="p-4 md:p-8 max-w-7xl mx-auto w-full animate-in fade-in duration-500">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
                <div>
                    <h1 className="text-3xl md:text-4xl font-extrabold bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] bg-clip-text text-transparent transform transition-all hover:scale-105 origin-left duration-300">
                        Tasks
                    </h1>
                    <p className="text-[var(--text-secondary)] mt-2 text-sm md:text-base opacity-90">
                        Manage tasks and assignments.
                    </p>
                </div>
                
                <div className="flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
                    {/* Filter Dropdown */}
                    <div className="relative w-full sm:w-auto min-w-[200px]">
                        <select
                            value={filter}
                            onChange={(e) => setFilter(e.target.value)}
                            className="w-full px-4 py-2.5 rounded-xl bg-[var(--card-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none shadow-sm font-medium"
                        >
                            <option value="ALL">All Tasks</option>
                            <option value="LOW_PRIORITY">Low Priority</option>
                            <option value="MEDIUM_PRIORITY">Medium Priority</option>
                            <option value="HIGH_PRIORITY">High Priority</option>
                            <option value="PENDING">Pending Status</option>
                            <option value="IN_PROGRESS">In Progress Status</option>
                            <option value="COMPLETED">Completed Status</option>
                            <option value="CANCELLED">Cancelled Status</option>
                            <option value="TEAM">Assigned to Team</option>
                            <option value="EMPLOYEE">Assigned to Employee</option>
                        </select>
                        <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                        </div>
                    </div>
                
                    {/* Create Task Button */}
                    <button 
                        onClick={openCreateModal}
                        className="flex items-center justify-center gap-2 w-full sm:w-auto px-5 py-2.5 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white font-semibold rounded-xl shadow-lg hover:shadow-[0_8px_20px_rgba(108,92,231,0.3)] transform hover:-translate-y-0.5 transition-all duration-300 focus:outline-none whitespace-nowrap"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2.5" d="M12 4v16m8-8H4"></path>
                        </svg>
                        New Task
                    </button>
                </div>
            </div>
            
            {/* Main Content Area */}
            <div className="bg-[var(--card-bg)] rounded-3xl border border-[var(--glass-border)] shadow-2xl backdrop-blur-xl overflow-hidden transition-all duration-300 relative">
                <div className="absolute top-0 right-0 w-64 h-64 bg-[var(--primary-color)] opacity-5 rounded-full blur-3xl pointer-events-none -translate-y-1/2 translate-x-1/2"></div>
                <div className="absolute bottom-0 left-0 w-64 h-64 bg-[var(--secondary-color)] opacity-5 rounded-full blur-3xl pointer-events-none translate-y-1/2 -translate-x-1/2"></div>
                
                <div className="relative z-10">
                    {loading ? (
                        <div className="flex flex-col justify-center items-center py-24 px-4">
                            <div className="relative w-16 h-16">
                                <div className="absolute inset-0 border-4 border-[var(--ui-element-border)] rounded-full"></div>
                                <div className="absolute inset-0 border-4 border-[var(--primary-color)] rounded-full border-t-transparent animate-spin"></div>
                            </div>
                            <p className="mt-6 text-[var(--text-secondary)] font-medium md:text-lg animate-pulse">
                                Loading tasks...
                            </p>
                        </div>
                    ) : tasks.length === 0 ? (
                        <div className="flex flex-col items-center justify-center py-28 px-4 text-center">
                            <div className="w-24 h-24 mb-6 rounded-3xl bg-gradient-to-br from-[var(--ui-element-bg)] to-[var(--ui-element-bg-secondary)] border border-[var(--glass-border)] flex items-center justify-center transform rotate-3 hover:-rotate-3 transition-all duration-500 shadow-inner group">
                                <KanbanSquare className="w-12 h-12 text-[var(--text-secondary)] group-hover:text-[var(--primary-color)] transition-colors duration-300" />
                            </div>
                            <h3 className="text-2xl font-bold text-[var(--text-color)] mb-3">No tasks found</h3>
                            <p className="text-[var(--text-secondary)] max-w-md mx-auto text-base">
                                Your organization currently has no tasks. Create your first task to get started.
                            </p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto custom-scrollbar">
                            <table className="w-full text-left whitespace-nowrap">
                                <thead>
                                    <tr className="bg-[var(--section-bg)] bg-opacity-50 border-b border-[var(--glass-border)]">
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Task Details</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Priority</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Status</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase">Due Date</th>
                                        <th className="px-6 py-5 text-sm font-bold tracking-wider text-[var(--text-secondary)] uppercase text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-[var(--glass-border)]">
                                    {tasks.map((task, index) => (
                                        <tr key={task.id || index} className="hover:bg-[var(--ui-element-bg)] transition-all duration-200 group">
                                            <td className="px-6 py-5">
                                                <div>
                                                    <div className="text-base font-bold text-[var(--text-color)] group-hover:text-[var(--primary-color)] transition-colors duration-200">
                                                        {task.title}
                                                    </div>
                                                    <div className="text-sm text-[var(--text-secondary)] mt-0.5 truncate max-w-xs" title={task.description}>
                                                        {task.description}
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold border tracking-wide ${getPriorityColor(task.priority)}`}>
                                                    {task.priority || 'MEDIUM'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className={`text-sm font-bold ${getStatusColor(task.status)}`}>
                                                    {task.status || 'PENDING'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="text-sm text-[var(--text-color)]">
                                                    {task.dueDate ? new Date(task.dueDate).toLocaleString() : 'N/A'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-5">
                                                <div className="flex items-center justify-end gap-2 opacity-50 group-hover:opacity-100 transition-opacity duration-300">
                                                    <button 
                                                        onClick={() => openEditModal(task)}
                                                        className="p-2 rounded-xl text-[var(--secondary-color)] hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[var(--secondary-color)]/50"
                                                        title="Edit Task"
                                                    >
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                                                        </svg>
                                                    </button>
                                                    <button 
                                                        onClick={() => handleDelete(task.id)}
                                                        className="p-2 rounded-xl text-rose-500 hover:bg-[var(--ui-element-bg-secondary)] transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-rose-500/50"
                                                        title="Delete Task"
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

            {/* Create Task Modal */}
            {isCreateModalOpen && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex justify-center items-center p-4 animate-in fade-in duration-200">
                    <div className="bg-[var(--card-bg)] border border-[var(--glass-border)] w-full max-w-2xl rounded-2xl shadow-2xl flex flex-col transform transition-all scale-100 opacity-100">
                        <div className="p-6 border-b border-[var(--glass-border)] flex justify-between items-center">
                            <h2 className="text-2xl font-bold text-[var(--text-color)]">
                                {editingTask ? 'Edit Task' : 'Create New Task'}
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
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Task Title</label>
                                    <input
                                        type="text"
                                        name="title"
                                        value={formData.title}
                                        onChange={handleInputChange}
                                        required
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all"
                                        placeholder="Enter task title"
                                    />
                                </div>

                                <div>
                                    <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Description</label>
                                    <textarea
                                        name="description"
                                        value={formData.description}
                                        onChange={handleInputChange}
                                        required
                                        rows="3"
                                        className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all resize-none"
                                        placeholder="Describe the task details..."
                                    ></textarea>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                                    <div>
                                        <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Priority</label>
                                        <div className="relative">
                                            <select
                                                name="priority"
                                                value={formData.priority}
                                                onChange={handleInputChange}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                            >
                                                <option value="LOW" className="bg-[var(--card-bg)]">Low</option>
                                                <option value="MEDIUM" className="bg-[var(--card-bg)]">Medium</option>
                                                <option value="HIGH" className="bg-[var(--card-bg)]">High</option>
                                                <option value="URGENT" className="bg-[var(--card-bg)]">Urgent</option>
                                            </select>
                                            <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Status</label>
                                        <div className="relative">
                                            <select
                                                name="status"
                                                value={formData.status}
                                                onChange={handleInputChange}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                            >
                                                <option value="PENDING" className="bg-[var(--card-bg)]">Pending</option>
                                                <option value="IN_PROGRESS" className="bg-[var(--card-bg)]">In Progress</option>
                                                <option value="DONE" className="bg-[var(--card-bg)]">Done</option>
                                                <option value="CANCELLED" className="bg-[var(--card-bg)]">Cancelled</option>
                                            </select>
                                            <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Due Date</label>
                                        <input
                                            type="datetime-local"
                                            name="dueDate"
                                            value={formData.dueDate}
                                            onChange={handleInputChange}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all [color-scheme:dark]"
                                        />
                                    </div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                                    <div>
                                        <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Assign Type</label>
                                        <div className="relative">
                                            <select
                                                name="assignType"
                                                value={formData.assignType}
                                                onChange={handleInputChange}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                            >
                                                <option value="EMPLOYEE" className="bg-[var(--card-bg)]">Employee</option>
                                                <option value="TEAM" className="bg-[var(--card-bg)]">Team</option>
                                            </select>
                                            <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                            </div>
                                        </div>
                                    </div>

                                    {formData.assignType === 'EMPLOYEE' ? (
                                        <div>
                                            <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Select Employee</label>
                                            <div className="relative">
                                                <select
                                                    name="assignedEmployeeId"
                                                    value={formData.assignedEmployeeId}
                                                    onChange={handleInputChange}
                                                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                                >
                                                    <option value="" disabled className="bg-[var(--card-bg)]">Select an employee</option>
                                                    {employees.map(emp => (
                                                        <option key={emp.id} value={emp.id} className="bg-[var(--card-bg)]">{emp.name} ({emp.email})</option>
                                                    ))}
                                                </select>
                                                <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                                </div>
                                            </div>
                                        </div>
                                    ) : (
                                        <div>
                                            <label className="block text-sm font-semibold text-[var(--text-secondary)] mb-1.5 ml-1 uppercase tracking-wide">Select Team</label>
                                            <div className="relative">
                                                <select
                                                    name="assignedTeamId"
                                                    value={formData.assignedTeamId}
                                                    onChange={handleInputChange}
                                                    className="w-full px-4 py-3 rounded-xl bg-[var(--ui-element-bg)] border border-[var(--glass-border)] text-[var(--text-color)] focus:border-[var(--primary-color)] focus:ring-1 focus:ring-[var(--primary-color)] focus:outline-none transition-all appearance-none"
                                                >
                                                    <option value="" disabled className="bg-[var(--card-bg)]">Select a team</option>
                                                    {teams.map(team => (
                                                        <option key={team.id} value={team.id} className="bg-[var(--card-bg)]">{team.name}</option>
                                                    ))}
                                                </select>
                                                <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-[var(--text-secondary)]">
                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                                                </div>
                                            </div>
                                        </div>
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
                                        <><Loader2 size={18} className="animate-spin" /> {editingTask ? 'Updating...' : 'Creating...'}</>
                                    ) : (
                                        editingTask ? 'Update Task' : 'Create Task'
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

export default Task;
