import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import AppLayout from "./layouts/AppLayout";
import Dashboard from "./pages/Dashboard";
import Tickets from "./pages/Tickets";
import TicketDetails from "./pages/TicketDetails";
import MyWork from "./pages/MyWork";
import Workflows from "./pages/Workflows";
import Analytics from "./pages/Analytics";
import AuditLog from "./pages/AuditLog";
import Settings from "./pages/Settings";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<AppLayout />}>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/tickets" element={<Tickets />} />
                    <Route path="/tickets/:id" element={<TicketDetails />} />
                    <Route path="/my-work" element={<MyWork />} />
                    <Route path="/workflows" element={<Workflows />} />
                    <Route path="/analytics" element={<Analytics />} />
                    <Route path="/audit" element={<AuditLog />} />
                    <Route path="/settings" element={<Settings />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
