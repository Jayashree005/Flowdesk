export default function StatusBadge({ status }) {
    if (!status) return null;

    const formattedStatus = status.replace(/_/g, " ");
    const statusClass = `status-${status.toLowerCase().replace(/_/g, "-")}`;

    return (
        <span className={`status-badge ${statusClass}`}>
            <span className="status-dot" />
            {formattedStatus}
        </span>
    );
}
