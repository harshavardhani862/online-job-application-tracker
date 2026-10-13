import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getApplication } from "../services/applicationService";
import type { JobApplication } from "../types/Application";
import StatusBadge from "../components/StatusBadge";

export default function ApplicationDetails() {
  const { id } = useParams();
  const [application, setApplication] = useState<JobApplication | null>(null);

  useEffect(() => {
    if (id) getApplication(Number(id)).then(setApplication).catch(() => setApplication(null));
  }, [id]);

  if (!application) return <p>Loading application...</p>;

  return (
    <div>
      <div className="page-header"><h1>{application.jobTitle}</h1><StatusBadge status={application.status} /></div>
      <div className="card details">
        <h2>{application.companyName}</h2>
        <p><b>Location:</b> {application.location || "-"}</p>
        <p><b>Salary:</b> {application.salary || "-"}</p>
        <p><b>Applied Date:</b> {application.applicationDate}</p>
        <p><b>Notes:</b> {application.notes || "-"}</p>
        {application.jobUrl && <p><a href={application.jobUrl} target="_blank">Open Job Posting</a></p>}
        {application.id && <Link className="button" to={`/applications/${application.id}/edit`}>Edit</Link>}
      </div>
    </div>
  );
}
