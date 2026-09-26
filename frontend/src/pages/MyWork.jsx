import { useState, useEffect } from "react";
import { BriefcaseBusiness } from "lucide-react";
import TicketRow from "../components/TicketRow";
import EmptyState from "../components/EmptyState";
import { getTickets } from "../services/api";

export default function MyWork() {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTickets().then((res) => {
            const list = res.data || res;
            const myTickets = Array.isArray(list) ? list.filter(t =>
                t.status === "ASSIGNED" || t.status === "IN_PROGRESS" || t.status === "ESCALATED"
            ) : [];
            setTickets(myTickets);
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
                    <h1>My Work Queue</h1>
                    <p>Active tickets assigned to you or requiring direct operations intervention</p>
                </div>
            </div>

            <section className="panel">
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
                        {loading ? (
                            <tr>
                                <td colSpan="6" style={{ textAlign: "center", padding: "40px", color: "#6B7280" }}>
                                    Loading your queue...
                                </td>
                            </tr>
                        ) : tickets.length > 0 ? (
                            tickets.map(t => <TicketRow key={t.id} ticket={t} />)
                        ) : (
                            <tr>
                                <td colSpan="6" style={{ padding: "0" }}>
                                    <EmptyState
                                        title="Your queue is clear!"
                                        message="No pending tickets currently require your attention."
                                        icon={BriefcaseBusiness}
                                    />
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </section>
        </div>
    );
}
