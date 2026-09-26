import {
    LayoutDashboard,
    Ticket,
    BriefcaseBusiness,
    GitBranch,
    BarChart3,
    History,
    Settings,
    ChevronDown
} from "lucide-react";

import { NavLink } from "react-router-dom";

const navigation = [
    {
        label: "Overview",
        path: "/",
        icon: LayoutDashboard
    },
    {
        label: "Tickets",
        path: "/tickets",
        icon: Ticket
    },
    {
        label: "My Work",
        path: "/my-work",
        icon: BriefcaseBusiness
    },
    {
        label: "Workflows",
        path: "/workflows",
        icon: GitBranch
    },
    {
        label: "Analytics",
        path: "/analytics",
        icon: BarChart3
    },
    {
        label: "Audit Log",
        path: "/audit",
        icon: History
    }
];

export default function Sidebar() {
    return (
        <aside className="sidebar">

            <div className="brand">
                <div className="brand-mark">
                    F
                </div>

                <div>
                    <div className="brand-name">
                        FlowDesk
                    </div>

                    <div className="brand-subtitle">
                        Service Operations
                    </div>
                </div>
            </div>

            <div className="nav-section">
                <div className="nav-label">
                    WORKSPACE
                </div>

                {navigation.map((item) => {
                    const Icon = item.icon;

                    return (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            className={({ isActive }) =>
                                `nav-item ${isActive ? "active" : ""}`
                            }
                        >
                            <Icon size={18} strokeWidth={1.8} />
                            <span>{item.label}</span>
                        </NavLink>
                    );
                })}
            </div>

            <div className="sidebar-bottom">

                <NavLink
                    to="/settings"
                    className={({ isActive }) =>
                        `nav-item ${isActive ? "active" : ""}`
                    }
                >
                    <Settings size={18} />
                    <span>Settings</span>
                </NavLink>

                <div className="sidebar-divider" />

                <div className="user-card">

                    <div className="avatar">
                        A
                    </div>

                    <div className="user-info">
                        <strong>Admin User</strong>
                        <span>Administrator</span>
                    </div>

                    <ChevronDown size={16} />
                </div>

            </div>

        </aside>
    );
}
