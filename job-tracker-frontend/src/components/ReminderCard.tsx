import type { Reminder } from "../types/Reminder";

function formatReminderTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("en-IN", {
    weekday: "short", day: "numeric", month: "short", year: "numeric",
    hour: "numeric", minute: "2-digit",
  });
}

export default function ReminderCard({ reminder, onDelete }: {
  reminder: Reminder;
  onDelete: (id: number) => void;
}) {
  const id = reminder.id ?? reminder.reminderId;
  const due = new Date(reminder.reminderDateTime);
  const overdue = !reminder.completed && !reminder.notificationSent && !Number.isNaN(due.getTime()) && due.getTime() < Date.now();
  const status = reminder.completed ? "Completed" : reminder.notificationSent ? "Email sent" : overdue ? "Due · email pending" : "Scheduled";

  return (
    <article className="reminder-card">
      <div className="reminder-card-top">
        <span className={`reminder-status ${reminder.completed ? "is-complete" : reminder.notificationSent ? "is-sent" : overdue ? "is-overdue" : "is-scheduled"}`}><span />{status}</span>
        {id !== undefined && <button className="icon-delete" aria-label={`Delete ${reminder.title}`} title="Delete reminder" onClick={() => onDelete(id)}>×</button>}
      </div>
      <h3>{reminder.title}</h3>
      <p className="reminder-company">{reminder.companyName || "Job application"}{reminder.jobTitle ? ` · ${reminder.jobTitle}` : ""}</p>
      <div className="reminder-date-row"><span className="date-icon">◷</span><div><strong>{formatReminderTime(reminder.reminderDateTime)}</strong><small>{reminder.notificationSent ? "Notification sent successfully" : reminder.completed ? "Marked as completed" : "Email notification pending"}</small></div></div>
      {reminder.notes && <p className="reminder-notes">{reminder.notes}</p>}
      {reminder.notificationSentAt && <p className="email-sent-time">✓ Email sent {formatReminderTime(reminder.notificationSentAt)}</p>}
    </article>
  );
}
