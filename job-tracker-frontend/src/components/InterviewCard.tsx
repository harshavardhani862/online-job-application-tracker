import type { Interview } from "../types/Interview";

export default function InterviewCard({ interview, onDelete }: {
  interview: Interview;
  onDelete: (id: number) => void;
}) {
  const id = interview.id ?? interview.interviewId;
  return (
    <div className="card">
      <h3>{interview.interviewType || "Interview"}</h3>
      <p>Application ID: {interview.applicationId}</p>
      <p><b>Date:</b> {interview.interviewDate} {interview.interviewTime || ""}</p>
      <p><b>Interviewer:</b> {interview.interviewer || "Not specified"}</p>
      {interview.meetingLink && <a href={interview.meetingLink} target="_blank">Meeting Link</a>}
      {interview.notes && <p>{interview.notes}</p>}
      {id && <button className="button danger small" onClick={() => onDelete(id)}>Delete</button>}
    </div>
  );
}
