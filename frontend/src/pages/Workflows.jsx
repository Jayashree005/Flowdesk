import { useState } from "react";
import { GitBranch, ShieldCheck, ArrowRight, Check, X } from "lucide-react";

const WORKFLOW_RULES = [
    { from: "OPEN", to: "ASSIGNED", category: "ALL", severity: "ALL", allowed: true, description: "Automatic or manual agent assignment" },
    { from: "ASSIGNED", to: "IN_PROGRESS", category: "ALL", severity: "ALL", allowed: true, description: "Agent commences investigation" },
    { from: "IN_PROGRESS", to: "RESOLVED", category: "ALL", severity: "ALL", allowed: true, description: "Fix deployed or resolution provided" },
    { from: "RESOLVED", to: "CLOSED", category: "ALL", severity: "ALL", allowed: true, description: "Requester or system confirms resolution" },
    { from: "RESOLVED", to: "IN_PROGRESS", category: "ALL", severity: "ALL", allowed: true, description: "Reopening ticket upon dissatisfaction" },
    { from: "ESCALATED", to: "IN_PROGRESS", category: "ALL", severity: "ALL", allowed: true, description: "Resuming triage after SLA breach" },
    { from: "ESCALATED", to: "RESOLVED", category: "ALL", severity: "ALL", allowed: true, description: "Expedited fix for breached ticket" },
    { from: "IN_PROGRESS", to: "CLOSED", category: "IT", severity: "CRITICAL", allowed: false, description: "Strict prohibition: Critical tickets cannot skip resolution stage" },
    { from: "OPEN", to: "CLOSED", category: "ALL", severity: "ALL", allowed: false, description: "Tickets cannot be closed without being worked on" },
    { from: "CLOSED", to: "OPEN", category: "ALL", severity: "ALL", allowed: false, description: "Closed tickets are immutable and archived" },
];

export default function Workflows() {
    const [selectedCategory, setSelectedCategory] = useState("ALL");

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h1>Workflow State Engine</h1>
                    <p>Configurable state transition policies governed by category, severity, and department rules</p>
                </div>
            </div>

            <div className="panel" style={{ padding: "24px", marginBottom: "20px" }}>
                <div style={{ display: "flex", alignItems: "center", gap: "10px", marginBottom: "16px" }}>
                    <div className="stat-icon blue">
                        <GitBranch size={20} />
                    </div>
                    <div>
                        <h3 style={{ margin: 0, fontSize: "15px" }}>The FlowDesk State Machine Differentiator</h3>
                        <p style={{ margin: "2px 0 0", fontSize: "12px", color: "#6B7280" }}>
                            Unlike standard CRUD ticket desks, every state movement is validated server-side by the <code>WorkflowEngine</code>.
                        </p>
                    </div>
                </div>

                <div style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(5, 1fr)",
                    gap: "10px",
                    background: "#F9FAFB",
                    padding: "16px",
                    borderRadius: "8px",
                    border: "1px solid #E5E7EB",
                    textAlign: "center"
                }}>
                    <div>
                        <strong style={{ fontSize: "12px", color: "#2563EB" }}>1. OPEN</strong>
                        <div style={{ fontSize: "11px", color: "#6B7280" }}>Ticket Logged</div>
                    </div>
                    <div>
                        <strong style={{ fontSize: "12px", color: "#7C3AED" }}>2. ASSIGNED</strong>
                        <div style={{ fontSize: "11px", color: "#6B7280" }}>Agent Claimed</div>
                    </div>
                    <div>
                        <strong style={{ fontSize: "12px", color: "#EA580C" }}>3. IN PROGRESS</strong>
                        <div style={{ fontSize: "11px", color: "#6B7280" }}>Triage & Fix</div>
                    </div>
                    <div>
                        <strong style={{ fontSize: "12px", color: "#16A34A" }}>4. RESOLVED</strong>
                        <div style={{ fontSize: "11px", color: "#6B7280" }}>Fix Verified</div>
                    </div>
                    <div>
                        <strong style={{ fontSize: "12px", color: "#6B7280" }}>5. CLOSED</strong>
                        <div style={{ fontSize: "11px", color: "#6B7280" }}>Archived / Audited</div>
                    </div>
                </div>
            </div>

            <section className="panel">
                <div className="panel-header">
                    <div>
                        <h2>Configured Transition Rules Matrix</h2>
                        <p>Enforced by Spring Boot WorkflowEngine</p>
                    </div>
                </div>

                <table className="ticket-table">
                    <thead>
                        <tr>
                            <th>From State</th>
                            <th>Transition</th>
                            <th>To State</th>
                            <th>Category & Severity</th>
                            <th>Policy Status</th>
                            <th>Rule Rationale</th>
                        </tr>
                    </thead>
                    <tbody>
                        {WORKFLOW_RULES.map((rule, idx) => (
                            <tr key={idx}>
                                <td>
                                    <span className="status-badge" style={{ background: "#F3F4F6", color: "#111827" }}>
                                        {rule.from}
                                    </span>
                                </td>
                                <td>
                                    <ArrowRight size={14} color="#9CA3AF" />
                                </td>
                                <td>
                                    <span className="status-badge" style={{ background: "#F3F4F6", color: "#111827" }}>
                                        {rule.to}
                                    </span>
                                </td>
                                <td>
                                    <span style={{ fontSize: "11px", fontWeight: "600" }}>
                                        {rule.category} / {rule.severity}
                                    </span>
                                </td>
                                <td>
                                    {rule.allowed ? (
                                        <span style={{ display: "inline-flex", alignItems: "center", gap: "4px", color: "#16A34A", fontSize: "11px", fontWeight: "700" }}>
                                            <Check size={14} /> ALLOWED
                                        </span>
                                    ) : (
                                        <span style={{ display: "inline-flex", alignItems: "center", gap: "4px", color: "#DC2626", fontSize: "11px", fontWeight: "700" }}>
                                            <X size={14} /> FORBIDDEN (400)
                                        </span>
                                    )}
                                </td>
                                <td style={{ fontSize: "11px", color: "#4B5563" }}>
                                    {rule.description}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </section>
        </div>
    );
}
