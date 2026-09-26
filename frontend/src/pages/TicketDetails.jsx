import {
    ArrowLeft,
    Clock3,
    UserRound,
    Building2,
    MessageSquare,
    Send,
    MoreHorizontal,
    ShieldCheck,
    AlertTriangle,
    CheckCircle2,
    UserCheck,
    Play,
    Lock,
    RotateCcw,
    X
} from "lucide-react";

import {
    useEffect,
    useState
} from "react";

import {
    useNavigate,
    useParams
} from "react-router-dom";

import {
    getTicket,
    getTicketHistory,
    getTicketComments,
    changeTicketStatus,
    assignTicket,
    addComment,
    checkSla,
    getUsers
} from "../services/api";

import StatusBadge from "../components/StatusBadge";
import PriorityBadge from "../components/PriorityBadge";

export default function TicketDetails() {

    const { id } = useParams();
    const navigate = useNavigate();

    const [ticket, setTicket] = useState(null);
    const [history, setHistory] = useState([]);
    const [comments, setComments] = useState([]);
    const [agents, setAgents] = useState([]);

    const [comment, setComment] = useState("");

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [errorBanner, setErrorBanner] = useState(null);
    const [successBanner, setSuccessBanner] = useState(null);

    // Modal state for Assign Agent
    const [assignModalOpen, setAssignModalOpen] = useState(false);
    const [selectedAgentId, setSelectedAgentId] = useState("");

    const currentUserId = 2; // Default active agent Arun Kumar

    const loadData = async () => {

        try {

            setLoading(true);

            const [
                ticketRes,
                historyRes,
                commentRes,
                userRes
            ] = await Promise.all([
                getTicket(id),
                getTicketHistory(id),
                getTicketComments(id),
                getUsers("AGENT").catch(() => ({ data: [] }))
            ]);

            const tData = ticketRes.data || ticketRes;
            const hData = historyRes.data || historyRes;
            const cData = commentRes.data || commentRes;
            const uData = userRes.data || userRes;

            setTicket(tData);
            setHistory(Array.isArray(hData) ? hData : []);
            setComments(Array.isArray(cData) ? cData : []);
            setAgents(Array.isArray(uData) ? uData : []);
            if (Array.isArray(uData) && uData.length > 0) {
                setSelectedAgentId(uData[0].id);
            }

        } catch (error) {

            console.error(
                "Failed to load ticket",
                error
            );

        } finally {

            setLoading(false);

        }
    };

    useEffect(() => {
        loadData();
    }, [id]);

    const updateStatus = async (newStatus) => {

        try {

            setSaving(true);
            setErrorBanner(null);
            setSuccessBanner(null);

            await changeTicketStatus(
                id,
                newStatus,
                currentUserId
            );

            setSuccessBanner(`Successfully moved to ${newStatus.replace("_", " ")}`);
            await loadData();

        } catch (error) {

            const message =
                error.response?.data?.message ||
                "Unable to change ticket status.";

            setErrorBanner(message);

        } finally {

            setSaving(false);

        }
    };

    const handleAssignAgent = async () => {
        if (!selectedAgentId) return;

        try {
            setSaving(true);
            setErrorBanner(null);

            await assignTicket(id, Number(selectedAgentId), currentUserId);
            setAssignModalOpen(false);
            setSuccessBanner("Agent assigned and ticket moved to ASSIGNED");
            await loadData();
        } catch (error) {
            const message =
                error.response?.data?.message ||
                "Unable to assign agent.";
            setErrorBanner(message);
        } finally {
            setSaving(false);
        }
    };

    const handleCheckSla = async () => {
        try {
            setSaving(true);
            const res = await checkSla(id);
            if (res.status === "ESCALATED") {
                setErrorBanner("SLA deadline has been breached! Ticket escalated automatically.");
            } else {
                setSuccessBanner("SLA health checked: ticket is currently on track.");
            }
            await loadData();
        } catch (error) {
            console.error(error);
        } finally {
            setSaving(false);
        }
    };

    const submitComment = async () => {

        if (!comment.trim()) {
            return;
        }

        try {

            await addComment(
                id,
                comment,
                currentUserId
            );

            setComment("");

            const updatedComments =
                await getTicketComments(id);

            setComments(updatedComments.data || updatedComments);

        } catch (error) {

            console.error(
                "Failed to add comment",
                error
            );
        }
    };

    if (loading) {
        return (
            <div className="loading-state">
                Loading ticket...
            </div>
        );
    }

    if (!ticket) {
        return (
            <div className="empty-state">
                <h3>Ticket not found</h3>
            </div>
        );
    }

    return (
        <div>

            <button
                className="back-button"
                onClick={() => navigate("/tickets")}
            >
                <ArrowLeft size={15} />
                Back to tickets
            </button>

            {errorBanner && (
                <div style={{
                    background: "#FEF2F2",
                    color: "#DC2626",
                    border: "1px solid #FEE2E2",
                    padding: "12px 16px",
                    borderRadius: "8px",
                    marginBottom: "16px",
                    fontSize: "13px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between"
                }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                        <AlertTriangle size={17} />
                        <strong>Workflow Rejection:</strong> {errorBanner}
                    </div>
                    <button
                        onClick={() => setErrorBanner(null)}
                        style={{ border: "none", background: "transparent", color: "#DC2626" }}
                    >
                        <X size={15} />
                    </button>
                </div>
            )}

            {successBanner && (
                <div style={{
                    background: "#F0FDF4",
                    color: "#16A34A",
                    border: "1px solid #DCFCE7",
                    padding: "12px 16px",
                    borderRadius: "8px",
                    marginBottom: "16px",
                    fontSize: "13px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between"
                }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                        <CheckCircle2 size={17} />
                        {successBanner}
                    </div>
                    <button
                        onClick={() => setSuccessBanner(null)}
                        style={{ border: "none", background: "transparent", color: "#16A34A" }}
                    >
                        <X size={15} />
                    </button>
                </div>
            )}

            <div className="ticket-detail-header">

                <div>

                    <div className="ticket-id-label">
                        {ticket.ticketNumber}
                    </div>

                    <h1>
                        {ticket.title}
                    </h1>

                    <div className="detail-badges">

                        <PriorityBadge
                            priority={ticket.severity}
                        />

                        <span className="category-badge">
                            {ticket.category}
                        </span>

                        <StatusBadge
                            status={ticket.status}
                        />

                    </div>

                </div>

                <button className="icon-button" onClick={() => loadData()} title="Refresh">
                    <MoreHorizontal size={18} />
                </button>

            </div>

            <div className="detail-layout">

                <main>

                    <section className="panel workflow-panel">

                        <div className="panel-header">
                            <div>
                                <h2>Workflow</h2>
                                <p>
                                    Current ticket lifecycle
                                </p>
                            </div>
                        </div>

                        <WorkflowTimeline
                            status={ticket.status}
                        />

                    </section>

                    <section className="panel description-panel">

                        <div className="panel-header">
                            <div>
                                <h2>Description</h2>
                            </div>
                        </div>

                        <div className="description-content">
                            {ticket.description}
                        </div>

                    </section>

                    <section className="panel activity-panel">

                        <div className="panel-header">

                            <div>
                                <h2>Activity</h2>
                                <p>
                                    Complete ticket history
                                </p>
                            </div>

                            <ShieldCheck
                                size={18}
                                color="#16A34A"
                            />

                        </div>

                        <div className="timeline">

                            {history.map((item) => (

                                <HistoryItem
                                    key={item.id}
                                    item={item}
                                />

                            ))}

                        </div>

                    </section>

                    <section className="panel comments-panel">

                        <div className="panel-header">

                            <div>
                                <h2>
                                    Conversation
                                </h2>

                                <p>
                                    {comments.length} comments
                                </p>
                            </div>

                            <MessageSquare
                                size={18}
                                color="#6B7280"
                            />

                        </div>

                        <div className="comments-list">

                            {comments.length === 0 ? (

                                <div className="no-comments">
                                    No comments yet.
                                </div>

                            ) : (

                                comments.map((item) => (

                                    <div
                                        className="comment-item"
                                        key={item.id}
                                    >

                                        <div className="comment-avatar">
                                            {item.userName
                                                ?.charAt(0)
                                                .toUpperCase()}
                                        </div>

                                        <div>

                                            <div className="comment-meta">
                                                <strong>
                                                    {item.userName}
                                                </strong>

                                                <span>
                                                    {formatDate(
                                                        item.createdAt
                                                    )}
                                                </span>
                                            </div>

                                            <p>
                                                {item.content}
                                            </p>

                                        </div>

                                    </div>

                                ))

                            )}

                        </div>

                        <div className="comment-input">

                            <textarea
                                value={comment}
                                onChange={(e) =>
                                    setComment(e.target.value)
                                }
                                placeholder="Write an internal comment..."
                            />

                            <button
                                className="send-button"
                                onClick={submitComment}
                            >
                                <Send size={15} />
                                Send
                            </button>

                        </div>

                    </section>

                </main>

                <aside className="detail-sidebar">

                    <section className="panel">

                        <div className="side-section">

                            <div className="side-label">
                                SLA STATUS
                            </div>

                            <div
                                className={`sla-card ${ticket.slaStatus?.toLowerCase()}`}
                            >

                                <Clock3 size={20} />

                                <div>
                                    <strong>
                                        {ticket.slaStatus}
                                    </strong>

                                    <span>
                                        Deadline
                                    </span>

                                    <small>
                                        {formatDate(
                                            ticket.slaDeadline
                                        )}
                                    </small>
                                </div>

                            </div>

                            <button
                                className="btn-secondary"
                                onClick={handleCheckSla}
                                style={{ width: "100%", marginTop: "10px", fontSize: "10px" }}
                            >
                                Trigger SLA Escalation Check
                            </button>

                        </div>

                        <div className="side-section">

                            <div className="side-label">
                                ASSIGNEE
                            </div>

                            <div className="person-row">

                                <div className="person-avatar">
                                    {ticket.assignedAgentName
                                        ?.charAt(0) || "?"}
                                </div>

                                <div>
                                    <strong>
                                        {ticket.assignedAgentName ||
                                            "Unassigned"}
                                    </strong>

                                    <span>
                                        Service Agent
                                    </span>
                                </div>

                            </div>

                            <button
                                className="btn-secondary"
                                onClick={() => setAssignModalOpen(true)}
                                style={{ width: "100%", marginTop: "10px", fontSize: "11px" }}
                            >
                                {ticket.assignedAgentName ? "Reassign Agent" : "Assign Agent"}
                            </button>

                        </div>

                        <div className="side-section">

                            <div className="side-label">
                                DEPARTMENT
                            </div>

                            <div className="info-row">
                                <Building2 size={16} />
                                <span>
                                    {ticket.departmentName}
                                </span>
                            </div>

                        </div>

                        <div className="side-section">

                            <div className="side-label">
                                REQUESTER
                            </div>

                            <div className="info-row">
                                <UserRound size={16} />
                                <span>
                                    {ticket.requesterName}
                                </span>
                            </div>

                        </div>

                        <div className="side-section">

                            <div className="side-label">
                                CREATED
                            </div>

                            <div className="created-value">
                                {formatDate(
                                    ticket.createdAt
                                )}
                            </div>

                        </div>

                    </section>

                    <section className="panel action-panel">

                        <div className="side-label">
                            WORKFLOW ACTION
                        </div>

                        {ticket.status === "OPEN" && (
                            <button
                                disabled={saving}
                                className="workflow-action primary"
                                onClick={() => setAssignModalOpen(true)}
                            >
                                <UserCheck size={14} style={{ display: "inline", marginRight: "6px" }} />
                                Assign Agent
                            </button>
                        )}

                        {getNextActions(ticket.status).map((action) => (
                            <button
                                key={action.status}
                                disabled={saving}
                                className={`workflow-action ${action.primary ? "primary" : ""}`}
                                onClick={() =>
                                    updateStatus(
                                        action.status
                                    )
                                }
                            >
                                {action.label}
                            </button>
                        ))}

                        {/* Hackathon Demo button: Test Invalid Transition from IN_PROGRESS -> CLOSED */}
                        {ticket.status === "IN_PROGRESS" && (
                            <button
                                disabled={saving}
                                className="workflow-action"
                                style={{ color: "#DC2626", borderColor: "#FEE2E2", background: "#FEF2F2" }}
                                onClick={() => updateStatus("CLOSED")}
                                title="Attempts IN_PROGRESS -> CLOSED to verify server-side WorkflowEngine rejection"
                            >
                                Test Invalid Transition (Close)
                            </button>
                        )}

                        {ticket.status === "RESOLVED" && (
                            <button
                                disabled={saving}
                                className="workflow-action"
                                onClick={() => updateStatus("IN_PROGRESS")}
                            >
                                <RotateCcw size={14} style={{ display: "inline", marginRight: "6px" }} />
                                Reopen Ticket
                            </button>
                        )}

                    </section>

                </aside>

            </div>

            {/* Assign Agent Modal */}
            {assignModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content" style={{ maxWidth: "400px" }}>
                        <div className="modal-header">
                            <h2>Assign Service Agent</h2>
                            <button className="icon-button" onClick={() => setAssignModalOpen(false)}>
                                <X size={16} />
                            </button>
                        </div>
                        <div className="modal-body">
                            <p style={{ fontSize: "12px", color: "#6B7280", margin: "0 0 14px" }}>
                                Assigning an agent transitions the ticket from <strong>OPEN</strong> to <strong>ASSIGNED</strong> under the service workflow.
                            </p>
                            <div className="form-group">
                                <label>Select Available Agent</label>
                                <select
                                    value={selectedAgentId}
                                    onChange={(e) => setSelectedAgentId(e.target.value)}
                                >
                                    {agents.map((ag) => (
                                        <option key={ag.id} value={ag.id}>
                                            {ag.name} ({ag.departmentName || "Operations"})
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button className="btn-secondary" onClick={() => setAssignModalOpen(false)}>
                                Cancel
                            </button>
                            <button className="btn-primary" onClick={handleAssignAgent} disabled={saving}>
                                Confirm Assignment
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}

function WorkflowTimeline({ status }) {

    const stages = [
        "OPEN",
        "ASSIGNED",
        "IN_PROGRESS",
        "RESOLVED",
        "CLOSED"
    ];

    const currentIndex =
        stages.indexOf(status);

    return (
        <div className="workflow-timeline">

            {stages.map((stage, index) => {

                const completed =
                    index <= currentIndex;

                const active =
                    stage === status;

                return (
                    <div
                        className="workflow-stage"
                        key={stage}
                    >

                        <div
                            className={`workflow-node
                                ${completed ? "completed" : ""}
                                ${active ? "active" : ""}
                            `}
                        >
                            {completed
                                ? <CheckCircle2 size={14} />
                                : index + 1}
                        </div>

                        <span>
                            {stage.replace(
                                "_",
                                " "
                            )}
                        </span>

                        {index < stages.length - 1 && (
                            <div
                                className={`workflow-line ${
                                    index < currentIndex
                                        ? "completed"
                                        : ""
                                }`}
                            />
                        )}

                    </div>
                );
            })}

        </div>
    );
}

function HistoryItem({ item }) {

    const isSystem =
        item.changedBy === "SYSTEM";

    return (
        <div className="timeline-item">

            <div
                className={`timeline-icon ${
                    isSystem
                        ? "system"
                        : ""
                }`}
            >
                {isSystem
                    ? <AlertTriangle size={14} />
                    : <CheckCircle2 size={14} />}
            </div>

            <div className="timeline-content">

                <div className="timeline-title">
                    <strong>
                        {item.action
                            ?.replaceAll(
                                "_",
                                " "
                            )}
                    </strong>

                    <span>
                        {formatDate(
                            item.changedAt
                        )}
                    </span>
                </div>

                <p>
                    {item.oldValue &&
                        `${item.oldValue} → `}
                    {item.newValue}
                </p>

                <small>
                    by {item.changedBy || "System"}
                </small>

            </div>

        </div>
    );
}

function getNextActions(status) {

    switch (status) {

        case "OPEN":
            return [];

        case "ASSIGNED":
            return [
                {
                    status: "IN_PROGRESS",
                    label: "Start Work",
                    primary: true
                }
            ];

        case "IN_PROGRESS":
            return [
                {
                    status: "RESOLVED",
                    label: "Mark Resolved",
                    primary: true
                }
            ];

        case "RESOLVED":
            return [
                {
                    status: "CLOSED",
                    label: "Close Ticket",
                    primary: true
                }
            ];

        case "ESCALATED":
            return [
                {
                    status: "IN_PROGRESS",
                    label: "Resume In Progress",
                    primary: true
                }
            ];

        default:
            return [];
    }
}

function formatDate(value) {

    if (!value) {
        return "-";
    }

    return new Date(value).toLocaleString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        }
    );
}
