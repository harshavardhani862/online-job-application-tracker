import { useEffect, useState } from "react";
import InterviewCard from "../components/InterviewCard";
import { createInterview, deleteInterview, getInterviews } from "../services/interviewService";
import type { Interview } from "../types/Interview";

export default function Interviews() {
  const [items, setItems] = useState<Interview[]>([]);
  const [form, setForm] = useState<Interview>({
    applicationId: 0, interviewDate: "", interviewTime: "", interviewType: "Technical", interviewer: "", meetingLink: "", notes: ""
  });

  const load = () => getInterviews().then(setItems).catch(() => setItems([]));
  useEffect(() => { load(); }, []);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try { await createInterview(form); setForm({ ...form, applicationId: 0, interviewDate: "" }); load(); }
    catch { alert("Unable to create interview"); }
  };

  const remove = async (id: number) => { if (confirm("Delete interview?")) { await deleteInterview(id); load(); } };

  return (
    <div>
      <h1>Interviews</h1>
      <form className="form card" onSubmit={submit}>
        <h3>Schedule Interview</h3>
        <label>Application ID<input type="number" value={form.applicationId} onChange={e => setForm({...form, applicationId: Number(e.target.value)})} /></label>
        <label>Date<input type="date" value={form.interviewDate} onChange={e => setForm({...form, interviewDate: e.target.value})} required /></label>
        <label>Time<input type="time" value={form.interviewTime} onChange={e => setForm({...form, interviewTime: e.target.value})} /></label>
        <label>Type<input value={form.interviewType} onChange={e => setForm({...form, interviewType: e.target.value})} /></label>
        <label>Interviewer<input value={form.interviewer} onChange={e => setForm({...form, interviewer: e.target.value})} /></label>
        <label>Meeting Link<input value={form.meetingLink} onChange={e => setForm({...form, meetingLink: e.target.value})} /></label>
        <label>Notes<textarea value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} /></label>
        <button className="button">Schedule</button>
      </form>
      <div className="grid">{items.map((i, n) => <InterviewCard key={i.id ?? i.interviewId ?? n} interview={i} onDelete={remove} />)}</div>
    </div>
  );
}
