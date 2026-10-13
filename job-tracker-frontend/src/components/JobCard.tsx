import { Link } from "react-router-dom";
import type { JobApplication } from "../types/Application";
import StatusBadge from "./StatusBadge";

export default function JobCard({ application, onDelete }: {
  application: JobApplication;
  onDelete: (id: number) => void;
}) {
  const id = application.id ?? application.applicationId;
  return (
    <article className="card job-card">
      <div>
        <h3>{application.jobTitle}</h3>
        <p className="muted">{application.companyName}</p>
        <p>{application.location || "Location not specified"}</p>
      </div>
      <StatusBadge status={application.status} />
      <p>Applied: {application.applicationDate}</p>
      <div className="actions">
        {id && <Link className="button small" to={`/applications/${id}`}>View</Link>}
        {id && <Link className="button small secondary" to={`/applications/${id}/edit`}>Edit</Link>}
        {id && <button className="button small danger" onClick={() => onDelete(id)}>Delete</button>}
      </div>
    </article>
  );
}
