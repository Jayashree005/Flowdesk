import { useState, useEffect } from "react";
import { History, ShieldAlert } from "lucide-react";
import { getTickets, getTicketHistory } from "../services/api";

export default function AuditLog() {
    const [auditLogs, setAuditLogs] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTickets().then(async (res) => {
            const tickets = res.data || res;
            if (!Array.isArray(tickets)) {
                setLoading(false);
                return;
            }
            const historyPromises = tickets.map(t =>
                getTicketHistory(t.id)
                    .then(hRes => {
                        const hList = hRes.data || hRes;
                        return (Array.isArray(hList) ? hList : []).map(entry => ({ ...entry, ticketNumber: t.ticketNumber, ticketTitle: t.title }));
                    })
                    .catch(() => [])
            );

            const allHistories = await Promise.all(historyPromises);
            const flatLogs = allHistories.flat().sort((a, b) => new Date(b.changedAt) - new Date(a.changedAt));
            setAuditLogs(flatLogs);
            setLoading(false);
        }).catch(err => {
            console.error(err);
            setLoading(false);
        });
    }, []);

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h1>Immutable Audit Trail</h1>
                    <p>Complete historical log of every ticket creation, assignment, transition, and escalation</p>
                </div>
            </div>

            <section className="panel">
                <table className="ticket-table">
                    <thead>
                        <tr>
                            <th>Ticket</th>
                            <th>Action</th>
                            <th>Actor / Changed By</th>
                            <th>Transition</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan="5" style={{ textAlign: "center", padding: "40px", color: "#6B7280" }}>
                                    Loading audit logs...
                                </td>
                            </tr>
                        ) : auditLogs.length > 0 ? (
                            auditLogs.map((log) => (
                                <tr key={log.id}>
                                    <td>
                                        <strong>{log.ticketNumber}</strong>
                                        <div style={{ fontSize: "11px", color: "#6B7280" }}>{log.ticketTitle}</div>
                                    </td>
                                    <td>
                                        <span className="status-badge" style={{
                                            background: log.action === "SLA_BREACHED" ? "#FEF2F2" : "#F3F4F6",
                                            color: log.action === "SLA_BREACHED" ? "#DC2626" : "#374151"
                                        }}>
                                            {log.action}
                                        </span>
                                    </td>
                                    <td>
                                        <strong>{log.changedBy || "System"}</strong>
                                    </td>
                                    <td>
                                        {log.oldValue || log.newValue ? (
                                            <span style={{ fontSize: "11px" }}>
                                                <code>{log.oldValue || "—"}</code> → <code>{log.newValue}</code>
                                            </span>
                                        ) : "—"}
                                    </td>
                                    <td>
                                        <span style={{ fontSize: "11px", color: "#6B7280" }}>
                                            {new Date(log.changedAt).toLocaleString()}
                                        </span>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="5" style={{ textAlign: "center", padding: "30px", color: "#6B7280" }}>
                                    No audit history recorded yet.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </section>
        </div>
    );
}
