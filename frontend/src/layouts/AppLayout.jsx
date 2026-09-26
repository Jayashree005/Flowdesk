import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Topbar from "../components/Topbar";
import CreateTicketModal from "../components/CreateTicketModal";

export default function AppLayout() {
    const [isCreateOpen, setIsCreateOpen] = useState(false);

    return (
        <div className="app-shell">
            <Sidebar />

            <main className="main-area">
                <Topbar onNewTicket={() => setIsCreateOpen(true)} />

                <div className="page-content">
                    <Outlet context={{ openCreateTicket: () => setIsCreateOpen(true) }} />
                </div>
            </main>

            <CreateTicketModal
                isOpen={isCreateOpen}
                onClose={() => setIsCreateOpen(false)}
                onCreated={() => {
                    // Trigger custom event so any active page (Dashboard, Tickets) can reload tickets
                    window.dispatchEvent(new CustomEvent("ticket-created"));
                }}
            />
        </div>
    );
}
