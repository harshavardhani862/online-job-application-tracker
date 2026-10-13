import { useEffect, useState, type FormEvent } from "react";
import { createCandidate, getCandidates, getOpenings, updateCandidate } from "../services/recruitingService";
import type { Candidate, CandidateRequest, CandidateStage, JobOpening } from "../types/Recruiting";
import { getErrorMessage } from "../utils/apiError";

const stages: CandidateStage[] = ["NEW", "SCREENING", "INTERVIEW", "OFFER", "HIRED", "REJECTED"];
const emptyForm: CandidateRequest = { name: "", email: "", phone: "", location: "", source: "Direct", resumeUrl: "", notes: "", stage: "NEW" };
const label = (value: string) => value.charAt(0) + value.slice(1).toLowerCase();

function initials(name: string) {
  return name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join("").toUpperCase();
}

export default function Candidates() {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [jobs, setJobs] = useState<JobOpening[]>([]);
  const [form, setForm] = useState<CandidateRequest>(emptyForm);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [query, setQuery] = useState("");
  const [stageFilter, setStageFilter] = useState<CandidateStage | "ALL">("ALL");

  const refresh = async () => {
    setLoading(true);
    try {
      const [people, openings] = await Promise.all([getCandidates(), getOpenings()]);
      setCandidates(people);
      setJobs(openings);
      setError("");
    } catch (e) { setError(getErrorMessage(e, "Unable to load candidates.")); }
    finally { setLoading(false); }
  };
  useEffect(() => { void refresh(); }, []);

  const save = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    try {
      await createCandidate({ ...form, jobId: form.jobId ? Number(form.jobId) : undefined });
      setForm(emptyForm);
      setShowForm(false);
      await refresh();
    } catch (e) { setError(getErrorMessage(e, "Could not add this candidate.")); }
    finally { setSaving(false); }
  };

  const moveCandidate = async (candidate: Candidate, stage: CandidateStage) => {
    const request: CandidateRequest = {
      name: candidate.name, email: candidate.email, phone: candidate.phone,
      location: candidate.location, source: candidate.source, resumeUrl: candidate.resumeUrl,
      notes: candidate.notes, jobId: candidate.jobId, stage,
    };
    try {
      const saved = await updateCandidate(candidate.id, request);
      setCandidates((current) => current.map((item) => item.id === saved.id ? saved : item));
      setError("");
    } catch (e) { setError(getErrorMessage(e, "Could not update the candidate stage.")); }
  };

  const visible = candidates.filter((candidate) =>
    (stageFilter === "ALL" || candidate.stage === stageFilter) &&
    `${candidate.name} ${candidate.email} ${candidate.jobTitle ?? ""} ${candidate.source}`.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="ats-page">
      <div className="ats-page-topline"><span className="workspace-crumb"><span className="crumb-mark">t</span> Workspace <span> / </span> Candidates</span><span className="workspace-status"><i /> Shared team pipeline</span></div>
      <header className="ats-page-header"><div><span className="eyebrow">PEOPLE FIRST, PROCESS SECOND</span><h1>Candidates</h1><p>Every conversation has a next step. Keep the whole team in the loop.</p></div><button className="button" type="button" onClick={() => { setShowForm((value) => !value); setError(""); }}>＋ Add a candidate</button></header>
      {error && <div className="ats-alert" role="alert">{error}<button type="button" onClick={() => void refresh()}>Refresh</button></div>}
      <section className="ats-candidate-summary">
        <div><span>IN YOUR PIPELINE</span><strong>{candidates.length}</strong></div>
        {stages.slice(0, 4).map((stage) => <div key={stage}><span>{label(stage)}</span><strong>{candidates.filter((candidate) => candidate.stage === stage).length}</strong></div>)}
      </section>
      {showForm && <form className="ats-form-panel" onSubmit={save}>
        <div className="ats-form-heading"><div><h2>Add someone to your pipeline</h2><p>Candidate information is only visible to your workspace team.</p></div><button className="icon-close" aria-label="Close form" type="button" onClick={() => setShowForm(false)}>×</button></div>
        <div className="ats-form-grid">
          <label>Full name<input autoComplete="name" required maxLength={100} placeholder="e.g. Alex Morgan" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
          <label>Email address<input type="email" autoComplete="email" required maxLength={254} placeholder="alex@example.com" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
          <label>Phone <span className="muted">(optional)</span><input type="tel" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} /></label>
          <label>Location<input placeholder="e.g. Remote · US" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} /></label>
          <label>Source<select value={form.source} onChange={(e) => setForm({ ...form, source: e.target.value })}><option>Direct</option><option>Referral</option><option>LinkedIn</option><option>Careers page</option><option>Agency</option><option>Other</option></select></label>
          <label>Link to a job<select value={form.jobId ?? ""} onChange={(e) => setForm({ ...form, jobId: e.target.value ? Number(e.target.value) : undefined })}><option value="">No role assigned</option>{jobs.map((job) => <option key={job.id} value={job.id}>{job.title}</option>)}</select></label>
          <label className="ats-form-wide">Resume or portfolio URL <span className="muted">(optional)</span><input type="url" placeholder="https://" value={form.resumeUrl} onChange={(e) => setForm({ ...form, resumeUrl: e.target.value })} /></label>
          <label className="ats-form-wide">Notes <span className="muted">(optional)</span><textarea rows={3} maxLength={10000} placeholder="Useful context for your hiring team" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></label>
        </div>
        <div className="ats-form-actions"><button className="button secondary" type="button" onClick={() => setShowForm(false)}>Cancel</button><button className="button" disabled={saving}>{saving ? "Adding…" : "Add candidate"}</button></div>
      </form>}

      <section className="ats-list-panel">
        <div className="ats-list-toolbar"><div><h2>All candidates <span className="count-badge">{candidates.length}</span></h2><p>Search people and move them through your pipeline.</p></div><div className="ats-list-controls"><label className="search-box"><span aria-hidden="true">⌕</span><input aria-label="Search candidates" placeholder="Search candidates..." value={query} onChange={(e) => setQuery(e.target.value)} /></label><select aria-label="Filter candidates by stage" value={stageFilter} onChange={(e) => setStageFilter(e.target.value as CandidateStage | "ALL")}><option value="ALL">All stages</option>{stages.map((stage) => <option value={stage} key={stage}>{label(stage)}</option>)}</select></div></div>
        {loading ? <div className="ats-empty">Loading your candidates…</div> : visible.length ? <div className="ats-table-wrap"><table className="ats-table candidate-table"><thead><tr><th>Candidate</th><th>Role</th><th>Source</th><th>Added</th><th>Stage</th></tr></thead><tbody>{visible.map((candidate) => <tr key={candidate.id}><td><span className="candidate-cell"><i className="candidate-avatar">{initials(candidate.name)}</i><span><b>{candidate.name}</b><small>{candidate.email}{candidate.location ? ` · ${candidate.location}` : ""}</small></span></span></td><td>{candidate.jobTitle || <span className="muted">Unassigned</span>}</td><td>{candidate.source}</td><td>{new Date(candidate.createdAt).toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" })}</td><td><select aria-label={`Stage for ${candidate.name}`} className={`status-select stage-select ${candidate.stage.toLowerCase()}`} value={candidate.stage} onChange={(e) => void moveCandidate(candidate, e.target.value as CandidateStage)}>{stages.map((stage) => <option value={stage} key={stage}>{label(stage)}</option>)}</select></td></tr>)}</tbody></table></div> : <div className="ats-empty"><span className="empty-symbol">◎</span><strong>{candidates.length ? "No candidates match those filters" : "Make space for a great conversation"}</strong><span>{candidates.length ? "Try a different search or pipeline stage." : "Add a candidate to start building your shared hiring pipeline."}</span>{!candidates.length && <button className="button small" type="button" onClick={() => setShowForm(true)}>Add a candidate</button>}</div>}
        <div className="ats-list-footer">Showing {visible.length} of {candidates.length} candidates <span>Candidate records are scoped to your workspace</span></div>
      </section>
    </div>
  );
}
