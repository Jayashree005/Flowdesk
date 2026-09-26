import { useState, useEffect } from "react";
import { X, Loader2 } from "lucide-react";
import { createTicket, getUsers, getDepartments } from "../services/api";

export default function CreateTicketModal({ isOpen, onClose, onCreated }) {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [category, setCategory] = useState("IT");
    const [severity, setSeverity] = useState("MEDIUM");
    const [requesterId, setRequesterId] = useState("");
    const [departmentId, setDepartmentId] = useState("");

    const [users, setUsers] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isOpen) {
            getUsers().then(res => {
                setUsers(res.data);
                // default to first requester or user
                const req = res.data.find(u => u.role === "REQUESTER") || res.data[0];
                if (req) setRequesterId(req.id);
            }).catch(console.error);

            getDepartments().then(res => {
                setDepartments(res.data);
                if (res.data.length > 0) setDepartmentId(res.data[0].id);
            }).catch(console.error);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setSubmitting(true);

        try {
            const payload = {
                title,
                description,
                category,
                severity,
                requesterId: Number(requesterId) || 4,
                departmentId: Number(departmentId) || 1
            };
            const response = await createTicket(payload);
            setSubmitting(false);
            setTitle("");
            setDescription("");
            onCreated(response.data);
            onClose();
        } catch (err) {
            setSubmitting(false);
            setError(err.response?.data?.message || "Failed to create ticket");
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Create New Service Ticket</h2>
                    <button className="icon-button" onClick={onClose}>
                        <X size={16} />
                    </button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="modal-body">
                        {error && (
                            <div style={{ color: "#DC2626", background: "#FEF2F2", padding: "8px 12px", borderRadius: "6px", fontSize: "12px" }}>
                                {error}
                            </div>
                        )}

                        <div className="form-group">
                            <label>Title *</label>
                            <input
                                required
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                placeholder="Brief summary of the issue"
                            />
                        </div>

                        <div className="form-group">
                            <label>Description *</label>
                            <textarea
                                required
                                rows={3}
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                placeholder="Detailed description of the problem or request"
                            />
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label>Category</label>
                                <select value={category} onChange={(e) => setCategory(e.target.value)}>
                                    <option value="IT">IT Support</option>
                                    <option value="HR">HR Operations</option>
                                    <option value="FINANCE">Finance</option>
                                    <option value="FACILITIES">Facilities</option>
                                    <option value="GENERAL">General</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Severity</label>
                                <select value={severity} onChange={(e) => setSeverity(e.target.value)}>
                                    <option value="CRITICAL">Critical (2h SLA)</option>
                                    <option value="HIGH">High (4h SLA)</option>
                                    <option value="MEDIUM">Medium (8h SLA)</option>
                                    <option value="LOW">Low (24h SLA)</option>
                                </select>
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label>Department</label>
                                <select value={departmentId} onChange={(e) => setDepartmentId(e.target.value)}>
                                    {departments.map((d) => (
                                        <option key={d.id} value={d.id}>{d.name}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Requester</label>
                                <select value={requesterId} onChange={(e) => setRequesterId(e.target.value)}>
                                    {users.map((u) => (
                                        <option key={u.id} value={u.id}>{u.name} ({u.role})</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn-secondary" onClick={onClose} disabled={submitting}>
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary" disabled={submitting}>
                            {submitting ? (
                                <>
                                    <Loader2 size={14} className="spin" style={{ display: "inline", marginRight: "6px" }} />
                                    Creating...
                                </>
                            ) : "Create Ticket"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
