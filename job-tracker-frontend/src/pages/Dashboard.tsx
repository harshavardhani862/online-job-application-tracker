import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getStatistics } from "../services/dashboardService";
import type { DashboardStatistics } from "../types/Dashboard";
import PremiumBadge from "../components/PremiumBadge";
import { useAuth } from "../hooks/useAuth";

export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStatistics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const { user } = useAuth();

  useEffect(() => {
    let active = true;
    const refresh = async () => {
      try {
        const latest = await getStatistics();
        if (active) { setStats(latest); setError(""); }
      } catch (e) {
        console.error("Could not refresh dashboard statistics:", e);
        if (active) setError("We couldn't refresh your statistics. Please check your connection and try again.");
      } finally {
        if (active) setLoading(false);
      }
    };
    void refresh();
    const timer = window.setInterval(() => void refresh(), 30000);
    const onFocus = () => void refresh();
    window.addEventListener("focus", onFocus);
    return () => { active = false; window.clearInterval(timer); window.removeEventListener("focus", onFocus); };
  }, []);

  const cards = [
    { label: "Total applications", value: stats?.totalApplications, icon: "▤", tone: "violet", note: "Every opportunity in one place" },
    { label: "Applied", value: stats?.applied, icon: "↗", tone: "blue", note: "Applications submitted" },
    { label: "Shortlisted", value: stats?.shortlisted, icon: "✦", tone: "amber", note: "Moving to the next stage" },
    { label: "Interviews", value: stats?.interviews, icon: "◷", tone: "purple", note: "Conversations to prepare for" },
    { label: "Selected", value: stats?.selected, icon: "✓", tone: "green", note: "Offers and successful outcomes" },
    { label: "Not selected", value: stats?.rejected, icon: "↗", tone: "rose", note: "Learn and keep progressing" },
  ];

  return (
    <div className="dashboard-page">
      <section className="dashboard-welcome">
        <div className="dashboard-welcome-copy">
          <span className="eyebrow">YOUR CAREER WORKSPACE</span>
          <h1>Welcome back{user?.name ? `, ${user.name.split(" ")[0]}` : ""} <span aria-hidden="true">✦</span></h1>
          <p>One step at a time. Keep your applications organized, prepare for interviews, and make your next career move with confidence.</p>
          <div className="dashboard-welcome-actions">
            <Link className="button" to="/applications/add"><span aria-hidden="true">＋</span> Add application</Link>
            <Link className="dashboard-light-link" to="/applications">View applications <span aria-hidden="true">→</span></Link>
          </div>
        </div>
        <div className="welcome-art" aria-hidden="true">
          <div className="welcome-orbit orbit-a" />
          <div className="welcome-orbit orbit-b" />
          <div className="welcome-doc"><span>JOB SEARCH</span><b>✓</b><i /><i /><i /></div>
          <div className="welcome-check">✓</div>
          <div className="welcome-star">✦</div>
        </div>
      </section>

      <div className="dashboard-section-heading">
        <div><h2>Your progress at a glance</h2><p>Stay on top of every opportunity.</p></div>
        <PremiumBadge />
      </div>

      {loading && <div className="dashboard-loading" role="status"><span className="loading-dot" /> Loading your latest statistics…</div>}
      {error && <div className="dashboard-error" role="status">{error}</div>}

      <section className="stats-grid professional-stats-grid" aria-label="Application statistics">
        {cards.map((card) => (
          <article className="stat professional-stat" key={card.label}>
            <div className={`professional-stat-icon ${card.tone}`} aria-hidden="true">{card.icon}</div>
            <div className="professional-stat-main">
              <span>{card.label}</span>
              <b>{card.value ?? (loading ? "—" : 0)}</b>
              <small>{card.note}</small>
            </div>
          </article>
        ))}
      </section>

      <section className="dashboard-section-heading quick-heading">
        <div><h2>Quick actions</h2><p>Pick up where you left off.</p></div>
      </section>
      <section className="dashboard-quick-grid">
        <Link className="dashboard-quick-link" to="/applications/add"><div className="dashboard-quick-icon violet">＋</div><div><strong>Add an application</strong><span>Track a new job opportunity</span></div><span className="quick-arrow">↗</span></Link>
        <Link className="dashboard-quick-link" to="/interviews"><div className="dashboard-quick-icon blue">◷</div><div><strong>Prepare for interviews</strong><span>Review your interview schedule</span></div><span className="quick-arrow">↗</span></Link>
        <Link className="dashboard-quick-link" to="/reminders"><div className="dashboard-quick-icon green">♧</div><div><strong>Check reminders</strong><span>Keep follow-ups on time</span></div><span className="quick-arrow">↗</span></Link>
        <Link className="dashboard-quick-link" to="/analytics"><div className="dashboard-quick-icon amber">▥</div><div><strong>View your insights</strong><span>Understand your progress</span></div><span className="quick-arrow">↗</span></Link>
      </section>
    </div>
  );
}
