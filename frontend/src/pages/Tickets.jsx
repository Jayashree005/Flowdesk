import {
    Search,
    SlidersHorizontal,
    Plus,
    RefreshCw,
    ChevronRight
} from "lucide-react";

import { useEffect, useState } from "react";
import { useNavigate, useOutletContext } from "react-router-dom";

import {
    getTickets
} from "../services/api";

import StatusBadge from "../components/StatusBadge";
import PriorityBadge from "../components/PriorityBadge";

export default function Tickets() {

    const navigate = useNavigate();
    const { openCreateTicket } = useOutletContext() || {};

    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [priorityFilter, setPriorityFilter] = useState("ALL");

    const loadTickets = async () => {

        try {
            setLoading(true);

            const response = await getTickets();

            setTickets(response.data || response);

        } catch (error) {

            console.error(
                "Failed to load tickets",
                error
            );

        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadTickets();
        window.addEventListener("ticket-created", loadTickets);
        return () => window.removeEventListener("ticket-created", loadTickets);
    }, []);

    const filteredTickets = tickets.filter((ticket) => {

        const matchesSearch =
            ticket.ticketNumber
                ?.toLowerCase()
                .includes(search.toLowerCase()) ||
            ticket.title
                ?.toLowerCase()
                .includes(search.toLowerCase()) ||
            ticket.assignedAgentName
                ?.toLowerCase()
                .includes(search.toLowerCase());

        const matchesStatus =
            statusFilter === "ALL" ||
            ticket.status === statusFilter;

        const matchesPriority =
            priorityFilter === "ALL" ||
            ticket.severity === priorityFilter;

        return (
            matchesSearch &&
            matchesStatus &&
            matchesPriority
        );
    });

    return (
        <div>

            <div className="page-heading">

                <div>
                    <h1>Tickets</h1>

                    <p>
                        Monitor, assign and resolve service requests.
                    </p>
                </div>

                <button
                    className="create-button"
                    onClick={openCreateTicket}
                >
                    <Plus size={16} />
                    New ticket
                </button>

            </div>

            <div className="ticket-toolbar">

                <div className="ticket-search">
                    <Search size={17} />

                    <input
                        value={search}
                        onChange={(e) =>
                            setSearch(e.target.value)
                        }
                        placeholder="Search ticket ID, title, or assignee..."
                    />
                </div>

                <select
                    value={statusFilter}
                    onChange={(e) =>
                        setStatusFilter(e.target.value)
                    }
                >
                    <option value="ALL">
                        All status
                    </option>
                    <option value="OPEN">
                        Open
                    </option>
                    <option value="ASSIGNED">
                        Assigned
                    </option>
                    <option value="IN_PROGRESS">
                        In progress
                    </option>
                    <option value="RESOLVED">
                        Resolved
                    </option>
                    <option value="CLOSED">
                        Closed
                    </option>
                    <option value="ESCALATED">
                        Escalated
                    </option>
                </select>

                <select
                    value={priorityFilter}
                    onChange={(e) =>
                        setPriorityFilter(e.target.value)
                    }
                >
                    <option value="ALL">
                        All priority
                    </option>
                    <option value="CRITICAL">
                        Critical
                    </option>
                    <option value="HIGH">
                        High
                    </option>
                    <option value="MEDIUM">
                        Medium
                    </option>
                    <option value="LOW">
                        Low
                    </option>
                </select>

                <button
                    className="filter-button"
                    title="Additional filters"
                >
                    <SlidersHorizontal size={16} />
                </button>

                <button
                    className="filter-button"
                    onClick={loadTickets}
                    title="Refresh"
                >
                    <RefreshCw
                        size={16}
                        className={
                            loading
                                ? "spin"
                                : ""
                        }
                    />
                </button>

            </div>

            <div className="ticket-count">
                <strong>
                    {filteredTickets.length}
                </strong>
                {" "}tickets
            </div>

            <div className="ticket-list-panel">

                {loading ? (

                    <div className="loading-state">
                        Loading tickets...
                    </div>

                ) : filteredTickets.length === 0 ? (

                    <div className="empty-state">
                        <h3>No tickets found</h3>
                        <p>
                            Try changing your search or filters.
                        </p>
                    </div>

                ) : (

                    <table className="ticket-table full-table">

                        <thead>

                        <tr>
                            <th>Ticket</th>
                            <th>Category</th>
                            <th>Priority</th>
                            <th>Assignee</th>
                            <th>Status</th>
                            <th>SLA</th>
                            <th></th>
                        </tr>

                        </thead>

                        <tbody>

                        {filteredTickets.map((ticket) => (

                            <tr
                                key={ticket.id}
                                className="clickable-row"
                                onClick={() =>
                                    navigate(
                                        `/tickets/${ticket.id}`
                                    )
                                }
                            >

                                <td>
                                    <div className="ticket-cell">

                                        <strong>
                                            {ticket.ticketNumber}
                                        </strong>

                                        <span>
                                            {ticket.title}
                                        </span>

                                    </div>
                                </td>

                                <td>
                                    {ticket.category}
                                </td>

                                <td>
                                    <PriorityBadge
                                        priority={ticket.severity}
                                    />
                                </td>

                                <td>
                                    {ticket.assignedAgentName ||
                                        "Unassigned"}
                                </td>

                                <td>
                                    <StatusBadge
                                        status={ticket.status}
                                    />
                                </td>

                                <td>
                                    <span
                                        className={`sla-value ${ticket.slaStatus?.toLowerCase()}`}
                                    >
                                        {ticket.slaStatus}
                                    </span>
                                </td>

                                <td>
                                    <ChevronRight
                                        size={16}
                                        className="row-arrow"
                                    />
                                </td>

                            </tr>

                        ))}

                        </tbody>

                    </table>

                )}

            </div>

        </div>
    );
}
