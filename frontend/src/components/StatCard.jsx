export default function StatCard({
    label,
    value,
    change,
    icon: Icon,
    tone = "default"
}) {
    return (
        <div className="stat-card">

            <div className="stat-top">
                <div className={`stat-icon ${tone}`}>
                    {Icon && <Icon size={19} />}
                </div>

                {change && (
                    <span className="stat-change">
                        {change}
                    </span>
                )}
            </div>

            <div className="stat-value">
                {value}
            </div>

            <div className="stat-label">
                {label}
            </div>

        </div>
    );
}
