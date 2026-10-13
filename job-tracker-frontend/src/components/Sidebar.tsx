import { NavLink } from "react-router-dom";

const mainLinks = [
  { to: "/dashboard", icon: "▦", label: "Overview" },
  { to: "/applications", icon: "▤", label: "Applications" },
  { to: "/interviews", icon: "◷", label: "Interviews" },
  { to: "/reminders", icon: "♧", label: "Reminders" },
  { to: "/analytics", icon: "↗", label: "Analytics" },
];
const accountLinks = [
  { to: "/profile", icon: "◉", label: "My profile" },
  { to: "/payment", icon: "✦", label: "Upgrade plan" },
  { to: "/payment-history", icon: "▣", label: "Billing history" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar professional-sidebar">
      <div className="sidebar-section-label">WORKSPACE</div>
      <nav className="sidebar-nav" aria-label="Workspace navigation">
        {mainLinks.map((item) => (
          <NavLink key={item.to} to={item.to} end={item.to === "/dashboard"} className={({ isActive }) => `sidebar-link${isActive ? " active" : ""}`}>
            <span className="sidebar-icon" aria-hidden="true">{item.icon}</span>
            <span>{item.label}</span>
            {item.to === "/reminders" && <span className="sidebar-link-dot" aria-hidden="true" />}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-section-label sidebar-account-label">ACCOUNT</div>
      <nav className="sidebar-nav" aria-label="Account navigation">
        {accountLinks.map((item) => (
          <NavLink key={item.to} to={item.to} className={({ isActive }) => `sidebar-link${isActive ? " active" : ""}`}>
            <span className="sidebar-icon" aria-hidden="true">{item.icon}</span>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-help-card">
        <span className="sidebar-help-icon" aria-hidden="true">✦</span>
        <strong>Make your next move</strong>
        <p>Stay organized and keep your job search moving forward.</p>
        <NavLink to="/applications/add">Add an application <span aria-hidden="true">→</span></NavLink>
      </div>
      <div className="sidebar-footer"><span className="sidebar-footer-dot" /> Your workspace is ready</div>
    </aside>
  );
}
