import { useNavigate } from "react-router-dom";
import StatusBadge from "./StatusBadge";
import PriorityBadge from "./PriorityBadge";

export default function TicketRow({ ticket }) {
    const navigate = useNavigate();

    const formatSla = (deadline, status, slaStatus) => {
        if (status === "RESOLVED" || status === "CLOSED") {
            return "Completed";
        }
        if (slaStatus === "BREACHED") {
            return <strong className="breached">BREACHED</strong>;
        }
        if (!deadline) return "—";

        try {
            const diffMs = new Date(deadline) - new Date();
            if (diffMs <= 0) return <strong className="breached">BREACHED</strong>;
            const diffMins = Math.floor(diffMs / 60000);
            const hours = Math.floor(diffMins / 60);
            const mins = diffMins % 60;
            if (hours > 0) return `${hours}h ${mins}m`;
            return `${mins}m left`;
        } catch {
            return "On Track";
        }
    };

    return (
        <tr
            style={{ cursor: "pointer" }}
            onClick={() => navigate(`/tickets/${ticket.id}`)}
        >
            <td>
                <div className="ticket-cell">
                    <strong>{ticket.ticketNumber}</strong>
                    <span>{ticket.title}</span>
                </div>
            </td>

            <td>{ticket.category}</td>

            <td>
                <PriorityBadge priority={ticket.severity} />
            </td>

            <td>{ticket.assignedAgentName || "Unassigned"}</td>

            <td>
                <StatusBadge status={ticket.status} />
            </td>

            <td>
                <span className="sla-value">
                    {formatSla(ticket.slaDeadline, ticket.status, ticket.slaStatus)}
                </span>
            </td>
        </tr>
    );
}
