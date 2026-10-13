import { useEffect, useState, type FormEvent } from "react";
import { createOpening, getOpenings, updateOpening } from "../services/recruitingService";
import type { JobOpening, JobRequest, OpeningStatus } from "../types/Recruiting";
import { getErrorMessage } from "../utils/apiError";

const emptyForm: JobRequest = {
  title: "", department: "", location: "", employmentType: "Full-time", description: "", status: "OPEN",
};

const statusLabel: Record<OpeningStatus, string> = { OPEN: "Open", ON_HOLD: "On hold", CLOSED: "Closed" };

export default function Jobs() {
  const [jobs, setJobs] = useState<JobOpening[]>([]);
  const [form, setForm] = useState<JobRequest>(emptyForm);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [query, setQuery] = useState("");
  const [filter, setFilter] = useState<OpeningStatus | "ALL">("ALL");

  const refresh = async () => {
    setLoading(true);
    try { setJobs(await getOpenings()); setError(""); }
    catch (e) { setError(getErrorMessage(e, "Unable to load job openings.")); }
    finally { setLoading(false); }
  };
  useEffect(() => { void refresh(); }, []);

  const save = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    try {
      await createOpening(form);
      setForm(emptyForm);
      setShowForm(false);
      await refresh();
    } catch (e) {
      setError(getErrorMessage(e, "Could not create this opening."));
    } finally { setSaving(false); }
  };

  const changeStatus = async (job: JobOpening, status: OpeningStatus) => {
    try {
      await updateOpening(job.id, { ...job, status });
      setJobs((current) => current.map((item) => item.id === job.id ? { ...item, status } : item));
      setError("");
    } catch (e) { setError(getErrorMessage(e, "Could not update this job.")); }
  };

  const visibleJobs = jobs.filter((job) =>
    (filter === "ALL" || job.status === filter) &&
    `${job.title} ${job.department} ${job.location}`.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="ats-page">
      <div className="ats-page-topline"><span className="workspace-crumb"><span className="crumb-mark">t</span> Workspace <span> / </span> Jobs</span><span className="workspace-status"><i /> Private to your team</span></div>
      <header className="ats-page-header"><div><span className="eyebrow">YOUR TEAM'S HIRING PLAN</span><h1>Jobs</h1><p>Manage open roles and keep your hiring plan in view.</p></div><button className="button" type="button" onClick={() => { setShowForm((value) => !value); setError(""); }}>＋ Create a job</button></header>
      {error && <div className="ats-alert" role="alert">{error}<button type="button" onClick={() => void refresh()}>Refresh</button></div>}
      {showForm && <form className="ats-form-panel" onSubmit={save}>
        <div className="ats-form-heading"><div><h2>New job opening</h2><p>Set up a role for your team to hire against.</p></div><button className="icon-close" aria-label="Close form" type="button" onClick={() => setShowForm(false)}>×</button></div>
        <div className="ats-form-grid">
          <label>Job title<input required maxLength={120} placeholder="e.g. Senior Product Designer" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></label>
          <label>Department<input required maxLength={80} placeholder="e.g. Product & Design" value={form.department} onChange={(e) => setForm({ ...form, department: e.target.value })} /></label>
          <label>Location<input required maxLength={120} placeholder="e.g. New York · Hybrid" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} /></label>
          <label>Employment type<select value={form.employmentType} onChange={(e) => setForm({ ...form, employmentType: e.target.value })}><option>Full-time</option><option>Part-time</option><option>Contract</option><option>Internship</option></select></label>
          <label className="ats-form-wide">Role overview <span className="muted">(optional)</span><textarea rows={3} maxLength={10000} placeholder="What will this person do? What does great look like?" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>
        </div>
        <div className="ats-form-actions"><button className="button secondary" type="button" onClick={() => setShowForm(false)}>Cancel</button><button className="button" disabled={saving}>{saving ? "Creating…" : "Create job opening"}</button></div>
      </form>}

      <section className="ats-list-panel">
        <div className="ats-list-toolbar"><div><h2>All openings <span className="count-badge">{jobs.length}</span></h2><p>Roles your recruiting team is working on.</p></div><div className="ats-list-controls"><label className="search-box"><span aria-hidden="true">⌕</span><input aria-label="Search job openings" placeholder="Search roles..." value={query} onChange={(e) => setQuery(e.target.value)} /></label><select aria-label="Filter job openings" value={filter} onChange={(e) => setFilter(e.target.value as OpeningStatus | "ALL")}><option value="ALL">All statuses</option><option value="OPEN">Open</option><option value="ON_HOLD">On hold</option><option value="CLOSED">Closed</option></select></div></div>
        {loading ? <div className="ats-empty">Loading your openings…</div> : visibleJobs.length ? <div className="ats-table-wrap"><table className="ats-table jobs-table"><thead><tr><th>Role</th><th>Type</th><th>Location</th><th>Created</th><th>Status</th></tr></thead><tbody>{visibleJobs.map((job) => <tr key={job.id}><td><span className="job-cell"><i className="job-avatar">{job.title.slice(0, 1).toUpperCase()}</i><span><b>{job.title}</b><small>{job.department}</small></span></span></td><td>{job.employmentType}</td><td>{job.location}</td><td>{new Date(job.createdAt).toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" })}</td><td><select aria-label={`Status for ${job.title}`} className={`status-select status-${job.status.toLowerCase()}`} value={job.status} onChange={(e) => void changeStatus(job, e.target.value as OpeningStatus)}>{Object.entries(statusLabel).map(([value, label]) => <option value={value} key={value}>{label}</option>)}</select></td></tr>)}</tbody></table></div> : <div className="ats-empty"><span className="empty-symbol">◫</span><strong>{jobs.length ? "No roles match those filters" : "Make your next hire intentional"}</strong><span>{jobs.length ? "Try a different search or status." : "Create your first opening and bring the team around it."}</span>{!jobs.length && <button className="button small" type="button" onClick={() => setShowForm(true)}>Create a job</button>}</div>}
        <div className="ats-list-footer">Showing {visibleJobs.length} of {jobs.length} openings <span>Visible to your workspace team</span></div>
      </section>
    </div>
  );
}
