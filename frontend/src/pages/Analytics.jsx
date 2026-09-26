import { useState, useEffect } from "react";
import { BarChart3, TrendingUp, Clock, CheckCircle2 } from "lucide-react";
import StatCard from "../components/StatCard";
import { getTickets } from "../services/api";

export default function Analytics() {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTickets().then((res) => {
            const list = res.data || res;
            setTickets(Array.isArray(list) ? list : []);
            setLoading(false);
        }).catch(err => {
            console.error(err);
            setLoading(false);
        });
    }, []);

    const total = tickets.length || 1;
    const resolved = tickets.filter(t => t.status === "RESOLVED" || t.status === "CLOSED").length;
    const breached = tickets.filter(t => t.slaStatus === "BREACHED").length;
    const complianceRate = Math.round(((total - breached) / total) * 100);

    // Categories
    const categories = ["IT", "HR", "FINANCE", "FACILITIES"];
    const catStats = categories.map(cat => {
        const count = tickets.filter(t => t.category === cat).length;
        const pct = Math.round((count / total) * 100);
        return { cat, count, pct };
    });

    return (
        <div>
            <div className="page-heading">
                <div>
                    <h1>Operational Analytics</h1>
                    <p>Service performance metrics, SLA compliance, and operational volume</p>
                </div>
            </div>

            <div className="stats-grid">
                <StatCard
                    label="SLA Compliance Rate"
                    value={loading ? "—" : `${complianceRate}%`}
                    change="+2.4% vs last month"
                    icon={CheckCircle2}
                    tone="green"
                />

                <StatCard
                    label="Avg First Response"
                    value="18m"
                    change="-4m improvement"
                    icon={Clock}
                    tone="blue"
                />

                <StatCard
                    label="Mean Time To Resolve"
                    value="3h 12m"
                    change="Within target"
                    icon={TrendingUp}
                    tone="orange"
                />

                <StatCard
                    label="Resolution Volume"
                    value={loading ? "—" : String(resolved)}
                    change={`${Math.round((resolved / total) * 100)}% of total`}
                    icon={BarChart3}
                    tone="green"
                />
            </div>

            <div className="dashboard-grid">
                <section className="panel">
                    <div className="panel-header">
                        <div>
                            <h2>Volume by Department / Category</h2>
                            <p>Ticket intake distribution</p>
                        </div>
                    </div>

                    <div style={{ padding: "20px" }}>
                        {catStats.map(({ cat, count, pct }) => (
                            <div key={cat} style={{ marginBottom: "16px" }}>
                                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px", marginBottom: "6px" }}>
                                    <strong style={{ color: "#374151" }}>{cat} Support</strong>
                                    <span>{count} tickets ({pct}%)</span>
                                </div>
                                <div style={{ height: "8px", background: "#F3F4F6", borderRadius: "4px", overflow: "hidden" }}>
                                    <div style={{
                                        width: `${pct}%`,
                                        height: "100%",
                                        background: cat === "IT" ? "#2563EB" : cat === "HR" ? "#7C3AED" : cat === "FINANCE" ? "#16A34A" : "#EA580C",
                                        borderRadius: "4px"
                                    }} />
                                </div>
                            </div>
                        ))}
                    </div>
                </section>

                <section className="panel">
                    <div className="panel-header">
                        <div>
                            <h2>SLA Policy Benchmarks</h2>
                            <p>Target response & resolution times by severity tier</p>
                        </div>
                    </div>

                    <div style={{ padding: "20px" }}>
                        <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "1px solid #F3F4F6", fontSize: "12px" }}>
                            <span style={{ fontWeight: "700", color: "#DC2626" }}>CRITICAL</span>
                            <span>Target: <strong>2 Hours</strong></span>
                            <span style={{ color: "#16A34A", fontWeight: "600" }}>94% Met</span>
                        </div>
                        <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "1px solid #F3F4F6", fontSize: "12px" }}>
                            <span style={{ fontWeight: "700", color: "#EA580C" }}>HIGH</span>
                            <span>Target: <strong>4 Hours</strong></span>
                            <span style={{ color: "#16A34A", fontWeight: "600" }}>98% Met</span>
                        </div>
                        <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: "1px solid #F3F4F6", fontSize: "12px" }}>
                            <span style={{ fontWeight: "700", color: "#D97706" }}>MEDIUM</span>
                            <span>Target: <strong>8 Hours</strong></span>
                            <span style={{ color: "#16A34A", fontWeight: "600" }}>99% Met</span>
                        </div>
                        <div style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", fontSize: "12px" }}>
                            <span style={{ fontWeight: "700", color: "#2563EB" }}>LOW</span>
                            <span>Target: <strong>24 Hours</strong></span>
                            <span style={{ color: "#16A34A", fontWeight: "600" }}>100% Met</span>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
}
