import { useState, useEffect } from "react";
import { Settings as SettingsIcon, Shield, Clock, Users, Building } from "lucide-react";
import { getDepartments, getUsers } from "../services/api";

export default function Settings() {
    const [departments, setDepartments] = useState([]);
    const [users, setUsers] = useState([]);

    useEffect(() => {
        getDepartments().then(res => setDepartments(res.data)).catch(console.error);
        getUsers().then(res => setUsers(res.data)).catch(console.error);
    }, []);

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h1>Settings & Operations Configuration</h1>
                    <p>Global service desk policies, SLA targets, and user roles</p>
                </div>
            </div>

            <div className="dashboard-grid">
                <section className="panel" style={{ padding: "20px" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "16px" }}>
                        <Clock size={18} color="#2563EB" />
                        <h2 style={{ margin: 0, fontSize: "14px" }}>SLA Target Policies</h2>
                    </div>

                    <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
                        <div style={{ padding: "12px", background: "#FEF2F2", borderRadius: "6px", border: "1px solid #FEE2E2" }}>
                            <strong style={{ color: "#DC2626", fontSize: "12px" }}>CRITICAL Severity</strong>
                            <div style={{ fontSize: "11px", color: "#374151", marginTop: "2px" }}>
                                Resolution Window: <strong>2 Hours</strong> (Auto-escalation upon breach)
                            </div>
                        </div>

                        <div style={{ padding: "12px", background: "#FFF7ED", borderRadius: "6px", border: "1px solid #FFEDD5" }}>
                            <strong style={{ color: "#EA580C", fontSize: "12px" }}>HIGH Severity</strong>
                            <div style={{ fontSize: "11px", color: "#374151", marginTop: "2px" }}>
                                Resolution Window: <strong>4 Hours</strong>
                            </div>
                        </div>

                        <div style={{ padding: "12px", background: "#FFFBEB", borderRadius: "6px", border: "1px solid #FEF3C7" }}>
                            <strong style={{ color: "#D97706", fontSize: "12px" }}>MEDIUM Severity</strong>
                            <div style={{ fontSize: "11px", color: "#374151", marginTop: "2px" }}>
                                Resolution Window: <strong>8 Hours</strong>
                            </div>
                        </div>

                        <div style={{ padding: "12px", background: "#EFF6FF", borderRadius: "6px", border: "1px solid #DBEAFE" }}>
                            <strong style={{ color: "#2563EB", fontSize: "12px" }}>LOW Severity</strong>
                            <div style={{ fontSize: "11px", color: "#374151", marginTop: "2px" }}>
                                Resolution Window: <strong>24 Hours</strong>
                            </div>
                        </div>
                    </div>
                </section>

                <section className="panel" style={{ padding: "20px" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "16px" }}>
                        <Building size={18} color="#7C3AED" />
                        <h2 style={{ margin: 0, fontSize: "14px" }}>Active Departments ({departments.length})</h2>
                    </div>

                    <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
                        {departments.map(d => (
                            <div key={d.id} style={{ display: "flex", justifyContent: "space-between", padding: "10px 14px", background: "#F9FAFB", borderRadius: "6px", fontSize: "12px" }}>
                                <strong>{d.name}</strong>
                                <span style={{ color: "#6B7280" }}>ID: #{d.id}</span>
                            </div>
                        ))}
                    </div>

                    <div style={{ display: "flex", alignItems: "center", gap: "8px", marginTop: "24px", marginBottom: "16px" }}>
                        <Users size={18} color="#16A34A" />
                        <h2 style={{ margin: 0, fontSize: "14px" }}>Registered Users ({users.length})</h2>
                    </div>

                    <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
                        {users.slice(0, 4).map(u => (
                            <div key={u.id} style={{ display: "flex", justifyContent: "space-between", padding: "8px 12px", background: "#F9FAFB", borderRadius: "6px", fontSize: "12px" }}>
                                <div>
                                    <strong>{u.name}</strong>
                                    <div style={{ fontSize: "10px", color: "#6B7280" }}>{u.email}</div>
                                </div>
                                <span className="status-badge" style={{ background: "#E5E7EB", color: "#111827" }}>
                                    {u.role}
                                </span>
                            </div>
                        ))}
                    </div>
                </section>
            </div>
        </div>
    );
}
