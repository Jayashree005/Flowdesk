import { Inbox } from "lucide-react";

export default function EmptyState({
    title = "No tickets found",
    message = "There are no tickets matching your current criteria.",
    icon: Icon = Inbox
}) {
    return (
        <div className="empty-state">
            <Icon size={40} strokeWidth={1.5} />
            <h3>{title}</h3>
            <p>{message}</p>
        </div>
    );
}
