import {
    Search,
    Bell,
    Plus
} from "lucide-react";
import { useLocation } from "react-router-dom";

export default function Topbar({ onNewTicket }) {
    const location = useLocation();

    const getPageTitle = (pathname) => {
        if (pathname === "/") return "Overview";
        if (pathname.startsWith("/tickets/")) return "Ticket Details";
        if (pathname === "/tickets") return "Tickets";
        if (pathname === "/my-work") return "My Work";
        if (pathname === "/workflows") return "Workflows";
        if (pathname === "/analytics") return "Analytics";
        if (pathname === "/audit") return "Audit Log";
        if (pathname === "/settings") return "Settings";
        return "Overview";
    };

    return (
        <header className="topbar">

            <div className="breadcrumb">
                Operations
                <span>/</span>
                <strong>{getPageTitle(location.pathname)}</strong>
            </div>

            <div className="topbar-actions">

                <div className="search-box">
                    <Search size={17} />
                    <input
                        placeholder="Search tickets..."
                    />
                    <kbd>⌘ K</kbd>
                </div>

                <button className="icon-button" title="Notifications">
                    <Bell size={18} />
                    <span className="notification-dot" />
                </button>

                <button
                    className="create-button"
                    onClick={onNewTicket}
                >
                    <Plus size={17} />
                    New ticket
                </button>

            </div>

        </header>
    );
}
