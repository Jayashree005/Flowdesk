import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import {
    Ticket as TicketIcon,
    Clock3,
    TriangleAlert,
    CheckCircle2,
    ArrowUpRight,
    MoreHorizontal
} from "lucide-react";

import StatCard from "../components/StatCard";
import TicketRow from "../components/TicketRow";
import { getTickets } from "../services/api";

export default function Dashboard() {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    const loadData = () => {
        getTickets()
            .then((res) => {
                const data = res.data || res;
                setTickets(Array.isArray(data) ? data : []);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Failed to load tickets", err);
                setLoading(false);
            });
    };

    useEffect(() => {
        loadData();
        window.addEventListener("ticket-created", loadData);
        return () => window.removeEventListener("ticket-created", loadData);
    }, []);

    // Compute live stats from tickets
    const openTickets = tickets.filter(t => t.status !== "RESOLVED" && t.status !== "CLOSED");
    const resolvedTickets = tickets.filter(t => t.status === "RESOLVED" || t.status === "CLOSED");
    const escalatedTickets = tickets.filter(t => t.status === "ESCALATED");
    const slaRiskTickets = tickets.filter(t => {
        if (t.status === "RESOLVED" || t.status === "CLOSED") return false;
        if (t.slaStatus === "BREACHED") return true;
        if (!t.slaDeadline) return false;
        const diffMs = new Date(t.slaDeadline) - new Date();
        return diffMs > 0 && diffMs < 2 * 3600 * 1000; // within 2 hours
    });

    const criticalCount = tickets.filter(t => t.severity === "CRITICAL").length;
    const highCount = tickets.filter(t => t.severity === "HIGH").length;
    const mediumCount = tickets.filter(t => t.severity === "MEDIUM").length;
    const lowCount = tickets.filter(t => t.severity === "LOW").length;
    const totalCount = tickets.length || 1;

    const criticalPct = Math.round((criticalCount / totalCount) * 100);
    const highPct = Math.round((highCount / totalCount) * 100);
    const mediumPct = Math.round((mediumCount / totalCount) * 100);
    const lowPct = Math.round((lowCount / totalCount) * 100);

    const recentTickets = [...tickets].reverse().slice(0, 5);

    // Format current date nicely
    const todayStr = new Intl.DateTimeFormat('en-US', {
        month: 'long',
        day: 'numeric',
        year: 'numeric'
    }).format(new Date());

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h1>Good morning, Admin</h1>
                    <p>
                        Here's what's happening across your service desk today.
                    </p>
                </div>

                <div className="heading-date">
                    {todayStr}
                </div>
            </div>

            <div className="stats-grid">
                <StatCard
                    label="Open tickets"
                    value={loading ? "—" : String(openTickets.length)}
                    change="+8.2%"
                    icon={TicketIcon}
                    tone="blue"
                />

                <StatCard
                    label="SLA at risk"
                    value={loading ? "—" : String(slaRiskTickets.length)}
                    change="+3 today"
                    icon={Clock3}
                    tone="orange"
                />

                <StatCard
                    label="Escalated"
                    value={loading ? "—" : String(escalatedTickets.length)}
                    change="Immediate Action"
                    icon={TriangleAlert}
                    tone="red"
                />

                <StatCard
                    label="Resolved today"
                    value={loading ? "—" : String(resolvedTickets.length)}
                    change="+14.6%"
                    icon={CheckCircle2}
                    tone="green"
                />
            </div>

            <div className="dashboard-grid">
                <section className="panel">
                    <div className="panel-header">
                        <div>
                            <h2>Priority workload</h2>
                            <p>Current ticket distribution</p>
                        </div>

                        <Link to="/tickets" className="ghost-button">
                            View tickets
                            <ArrowUpRight size={15} />
                        </Link>
                    </div>

                    <div className="priority-list">
                        <PriorityRow
                            label="Critical"
                            count={criticalCount}
                            percentage={`${criticalPct}%`}
                            type="critical"
                        />

                        <PriorityRow
                            label="High"
                            count={highCount}
                            percentage={`${highPct}%`}
                            type="high"
                        />

                        <PriorityRow
                            label="Medium"
                            count={mediumCount}
                            percentage={`${mediumPct}%`}
                            type="medium"
                        />

                        <PriorityRow
                            label="Low"
                            count={lowCount}
                            percentage={`${lowPct}%`}
                            type="low"
                        />
                    </div>
                </section>

                <section className="panel">
                    <div className="panel-header">
                        <div>
                            <h2>SLA risk queue</h2>
                            <p>Tickets requiring urgent attention</p>
                        </div>

                        <button className="icon-button" title="More options">
                            <MoreHorizontal size={18} />
                        </button>
                    </div>

                    <div className="risk-list">
                        {slaRiskTickets.length > 0 ? (
                            slaRiskTickets.slice(0, 4).map(ticket => {
                                const diffMs = ticket.slaDeadline ? new Date(ticket.slaDeadline) - new Date() : 0;
                                const isBreached = ticket.slaStatus === "BREACHED" || diffMs <= 0;
                                const diffMins = Math.max(0, Math.floor(diffMs / 60000));
                                const timeText = isBreached ? "BREACHED" : `${diffMins}m left`;

                                return (
                                    <RiskRow
                                        key={ticket.id}
                                        id={ticket.ticketNumber}
                                        title={ticket.title}
                                        time={timeText}
                                        severity={ticket.severity}
                                    />
                                );
                            })
                        ) : (
                            <div style={{ padding: "20px 0", textAlign: "center", color: "#6B7280", fontSize: "12px" }}>
                                All active tickets are currently within SLA deadlines.
                            </div>
                        )}
                    </div>
                </section>
            </div>

            <section className="panel recent-panel">
                <div className="panel-header">
                    <div>
                        <h2>Recent tickets</h2>
                        <p>Latest service operations activity</p>
                    </div>

                    <Link to="/tickets" className="ghost-button">
                        View all
                        <ArrowUpRight size={15} />
                    </Link>
                </div>

                <table className="ticket-table">
                    <thead>
                        <tr>
                            <th>Ticket</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Assignee</th>
                            <th>Status</th>
                            <th>SLA</th>
                        </tr>
                    </thead>

                    <tbody>
                        {recentTickets.length > 0 ? (
                            recentTickets.map((t) => (
                                <TicketRow key={t.id} ticket={t} />
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6" style={{ textAlign: "center", padding: "30px", color: "#6B7280" }}>
                                    No tickets created yet. Click "+ New ticket" in the top bar to create one.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </section>
        </div>
    );
}

function PriorityRow({
    label,
    count,
    percentage,
    type
}) {
    return (
        <div className="priority-row">
            <div className={`priority-indicator ${type}`} />

            <span className="priority-name">
                {label}
            </span>

            <strong>{count}</strong>

            <span className="priority-percent">
                {percentage}
            </span>
        </div>
    );
}

function RiskRow({
    id,
    title,
    time,
    severity
}) {
    return (
        <div className="risk-row">
            <div className="risk-main">
                <div className="risk-id">
                    {id}
                </div>

                <div className="risk-title">
                    {title}
                </div>
            </div>

            <div className="risk-right">
                <span className={`priority-text ${severity?.toLowerCase()}`}>
                    {severity}
                </span>

                <strong className={time === "BREACHED" ? "breached" : ""}>
                    {time}
                </strong>
            </div>
        </div>
    );
}
