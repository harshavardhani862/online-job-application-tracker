import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const features = [
  { icon: "▤", tone: "violet", title: "Application tracking", description: "Keep company details, role, status, and important notes organized." },
  { icon: "◷", tone: "blue", title: "Interview planning", description: "See upcoming interviews and feel prepared for every conversation." },
  { icon: "♧", tone: "mint", title: "Smart reminders", description: "Plan follow-ups and keep important dates from slipping by." },
  { icon: "↗", tone: "amber", title: "Progress insights", description: "Understand your job search progress from one clear workspace." },
];

export default function Home() {
  const { user } = useAuth();
  return (
    <div className="marketing-page">
      <section className="marketing-hero">
        <div className="marketing-copy">
          <div className="marketing-pill"><span className="live-dot" /> YOUR NEXT OPPORTUNITY STARTS HERE</div>
          <h1>Your job search,<br />but <span>more organized.</span></h1>
          <p className="marketing-lead">A calmer way to manage applications, prepare for interviews, remember follow-ups, and see how far you have come.</p>
          <div className="marketing-actions">
            <Link className="button marketing-primary" to={user ? "/dashboard" : "/signup"}>{user ? "Open your dashboard" : "Get started for free"} <span aria-hidden="true">→</span></Link>
            {!user && <Link className="button secondary marketing-secondary" to="/signin">I already have an account</Link>}
          </div>
          <div className="marketing-proof">
            <div className="proof-avatars" aria-hidden="true"><span>H</span><span>J</span><span>✓</span></div>
            <p><strong>Everything in one workspace</strong><br /><span>Applications · Interviews · Reminders · Insights</span></p>
          </div>
        </div>

        <div className="product-preview" aria-label="Illustration of the JobTrack dashboard">
          <div className="preview-glow" />
          <div className="preview-window">
            <div className="preview-topbar"><div className="preview-brand"><span className="preview-brand-mark">J</span> JobTrack</div><div className="preview-dots"><i /><i /><i /></div></div>
            <div className="preview-content">
              <div className="preview-greeting"><div><small>YOUR WORKSPACE</small><h3>Job search overview</h3></div><span className="preview-date">This week ▾</span></div>
              <div className="preview-stats">
                <div><span className="preview-stat-icon violet-bg">▤</span><small>Applications</small><strong>24</strong><em>Stay organized</em></div>
                <div><span className="preview-stat-icon blue-bg">◷</span><small>Interviews</small><strong>03</strong><em>Get prepared</em></div>
                <div><span className="preview-stat-icon green-bg">✓</span><small>Follow-ups</small><strong>05</strong><em>Never forget</em></div>
              </div>
              <div className="preview-lower">
                <div className="preview-chart"><div className="preview-panel-heading"><strong>Application activity</strong><small>Overview</small></div><div className="chart-bars"><span style={{height:"30%"}}/><span style={{height:"48%"}}/><span style={{height:"38%"}}/><span style={{height:"68%"}}/><span style={{height:"53%"}}/><span style={{height:"84%"}}/><span style={{height:"70%"}}/></div><div className="chart-days"><span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span><span>Fri</span><span>Sat</span><span>Sun</span></div></div>
                <div className="preview-tasks"><div className="preview-panel-heading"><strong>Up next</strong><small>Today</small></div><div className="preview-task"><span className="task-marker task-purple">A</span><div><strong>Review application</strong><small>Follow-up reminder</small></div></div><div className="preview-task"><span className="task-marker task-blue">I</span><div><strong>Prepare for interview</strong><small>Interview prep</small></div></div></div>
              </div>
            </div>
          </div>
          <div className="floating-note"><span>✓</span><div><strong>You are on track</strong><small>One step closer to your goal</small></div></div>
        </div>
      </section>

      <section className="marketing-trust"><span>DESIGNED TO KEEP YOUR SEARCH MOVING</span><div><b>Stay organized</b><i /><b>Follow up on time</b><i /><b>See your progress</b><i /><b>Move with confidence</b></div></section>

      <section className="marketing-features">
        <div className="marketing-section-heading"><span className="eyebrow">YOUR CAREER, IN FOCUS</span><h2>Less juggling.<br /><span>More forward momentum.</span></h2><p>Bring the important parts of your job search together in one simple, thoughtfully designed place.</p></div>
        <div className="feature-grid">{features.map((feature) => <article className="feature-card" key={feature.title}><div className={`feature-icon ${feature.tone}`}>{feature.icon}</div><h3>{feature.title}</h3><p>{feature.description}</p><span className="feature-arrow" aria-hidden="true">↗</span></article>)}</div>
      </section>

      <section className="marketing-bottom-cta"><div><span className="eyebrow">READY WHEN YOU ARE</span><h2>Make your next move count.</h2><p>Start building a more organized job search today.</p></div><Link className="button marketing-primary" to={user ? "/dashboard" : "/signup"}>{user ? "Go to dashboard" : "Create your free account"} <span aria-hidden="true">→</span></Link></section>
      <footer className="marketing-footer"><Link className="brand" to="/"> <span className="brand-mark">J</span> JobTrack<span className="brand-period">.</span></Link><span>Made to help you move forward in your career.</span><span>© {new Date().getFullYear()} JobTrack</span></footer>
    </div>
  );
}
