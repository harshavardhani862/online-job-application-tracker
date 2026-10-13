import { Link } from "react-router-dom";
import JobCard from "../components/JobCard";
import { useApplications } from "../hooks/useApplications";

export default function Applications() {
  const { applications, loading, remove } = useApplications();

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this application?")) return;
    try { await remove(id); } catch { alert("Delete failed"); }
  };

  return (
    <div>
      <div className="page-header">
        <div><h1>My Applications</h1><p className="muted">Track all your job applications.</p></div>
        <Link className="button" to="/applications/add">+ Add Application</Link>
      </div>
      {loading ? <p>Loading...</p> :
        applications.length === 0 ? <div className="empty card">No applications yet.</div> :
        <div className="grid">{applications.map((a, i) => <JobCard key={a.id ?? a.applicationId ?? i} application={a} onDelete={handleDelete} />)}</div>}
    </div>
  );
}
