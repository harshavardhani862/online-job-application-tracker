import { useCallback, useEffect, useMemo, useState } from "react";
import ReminderCard from "../components/ReminderCard";
import { getApplications } from "../services/applicationService";
import {
  createReminder,
  deleteReminder,
  getReminders,
  sendTestEmail,
} from "../services/reminderService";
import type { Reminder } from "../types/Reminder";
import type { JobApplication } from "../types/Application";

const emptyForm: Reminder = {
  title: "",
  reminderDateTime: "",
  applicationId: undefined,
  notes: "",
};

function getApplicationId(app: JobApplication): number | undefined {
  const id = app.id ?? app.applicationId;
  return typeof id === "number" && Number.isFinite(id) ? id : undefined;
}

function localDateTimeValue(date = new Date()): string {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 16);
}

function getErrorMessage(error: unknown): string {
  if (typeof error !== "object" || error === null) return "An unexpected error occurred.";
  const e = error as {
    message?: string;
    response?: { status?: number; data?: { message?: string; error?: string } };
  };
  const status = e.response?.status;
  const message = e.response?.data?.message ?? e.response?.data?.error;
  if (status === 401) return "Your session has expired. Sign in again.";
  if (status === 403) return "Access denied. Please sign in with the correct account.";
  if (status === 404) return "The API endpoint was not found. Restart the latest backend.";
  if (status === 500) return `Backend error: ${message ?? "Check the Spring Boot terminal."}`;
  if (status) return `Request failed (${status}): ${message ?? e.message ?? "Unknown error"}`;
  return `Could not connect to the backend: ${e.message ?? "Check whether Spring Boot is running."}`;
}

function formatDate(value?: string | null): string {
  if (!value) return "Not scheduled";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("en-IN", {
    weekday: "short",
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

export default function Reminders() {
  const [items, setItems] = useState<Reminder[]>([]);
  const [applications, setApplications] = useState<JobApplication[]>([]);
  const [form, setForm] = useState<Reminder>(emptyForm);
  const [saving, setSaving] = useState(false);
  const [sendingTest, setSendingTest] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [reminderError, setReminderError] = useState("");
  const [notice, setNotice] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setLoadError("");
    setReminderError("");
    const [remindersResult, applicationsResult] = await Promise.allSettled([
      getReminders(),
      getApplications(),
    ]);

    if (remindersResult.status === "fulfilled") {
      setItems(remindersResult.value);
    } else {
      console.error("Failed to load reminders:", remindersResult.reason);
      setReminderError(getErrorMessage(remindersResult.reason));
    }

    if (applicationsResult.status === "fulfilled") {
      const valid = applicationsResult.value.filter((app) => {
        const id = getApplicationId(app);
        return id !== undefined && Boolean(app.companyName?.trim()) && Boolean(app.jobTitle?.trim());
      });
      setApplications(valid);
      setForm((current) => {
        const exists = valid.some((app) => getApplicationId(app) === current.applicationId);
        if (exists) return current;
        return { ...current, applicationId: valid.length ? getApplicationId(valid[0]) : undefined };
      });
    } else {
      console.error("Failed to load applications:", applicationsResult.reason);
      setLoadError(getErrorMessage(applicationsResult.reason));
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    void load();
    const timer = window.setInterval(() => void load(), 60000);
    return () => window.clearInterval(timer);
  }, [load]);

  const upcomingCount = useMemo(
    () => items.filter((item) => !item.completed && new Date(item.reminderDateTime).getTime() >= Date.now()).length,
    [items],
  );
  const sentCount = useMemo(() => items.filter((item) => item.notificationSent).length, [items]);

  const submit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setNotice("");
    if (form.applicationId == null) {
      setNotice("Please add a job application before creating a reminder.");
      return;
    }
    if (!form.reminderDateTime || new Date(form.reminderDateTime).getTime() <= Date.now()) {
      setNotice("Choose a date and time in the future so the email can be sent when it is due.");
      return;
    }
    setSaving(true);
    try {
      await createReminder({ ...form, completed: false, notificationSent: false });
      setForm({ ...emptyForm, applicationId: form.applicationId, reminderDateTime: "" });
      setNotice("Reminder saved. Keep the backend running and configure Gmail SMTP to receive the email at the scheduled time.");
      await load();
    } catch (error) {
      console.error("Failed to create reminder:", error);
      setNotice(getErrorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  const remove = async (id: number) => {
    if (!window.confirm("Delete this reminder?")) return;
    try {
      await deleteReminder(id);
      setNotice("Reminder deleted.");
      await load();
    } catch (error) {
      setNotice(getErrorMessage(error));
    }
  };

  const testEmail = async () => {
    setSendingTest(true);
    setNotice("");
    try {
      const result = await sendTestEmail();
      setNotice(`${result.message}. Check your inbox: ${result.email}`);
    } catch (error) {
      console.error("Test email failed:", error);
      setNotice(getErrorMessage(error));
    } finally {
      setSendingTest(false);
    }
  };

  return (
    <section className="reminders-page">
      <div className="reminder-hero">
        <div className="reminder-hero-copy">
          <span className="eyebrow light-eyebrow">STAY ONE STEP AHEAD</span>
          <h1>Never miss a follow-up.</h1>
          <p>Plan your next move, set a time, and let JobTrack email you when your reminder is due.</p>
          <div className="hero-points">
            <span>✓ Scheduled reminders</span>
            <span>✓ Email alerts</span>
            <span>✓ All applications in one place</span>
          </div>
        </div>
        <div className="reminder-hero-art" aria-hidden="true">
          <div className="orbit orbit-one" />
          <div className="orbit orbit-two" />
          <div className="floating-calendar"><span>MON</span><strong>12</strong><i>● ● ●</i></div>
          <div className="floating-bell">🔔</div>
          <div className="floating-mail">✉</div>
        </div>
      </div>

      <div className="reminder-stats">
        <article className="reminder-stat"><span className="stat-icon purple">◷</span><div><strong>{items.length}</strong><span>Total reminders</span></div></article>
        <article className="reminder-stat"><span className="stat-icon blue">↗</span><div><strong>{upcomingCount}</strong><span>Upcoming</span></div></article>
        <article className="reminder-stat"><span className="stat-icon green">✉</span><div><strong>{sentCount}</strong><span>Email alerts sent</span></div></article>
      </div>

      {notice && <div className={`reminder-notice ${notice.toLowerCase().includes("failed") || notice.toLowerCase().includes("error") || notice.toLowerCase().includes("could not") ? "notice-error" : ""}`} role="status">{notice}<button type="button" aria-label="Dismiss message" onClick={() => setNotice("")}>×</button></div>}

      <div className="reminder-workspace">
        <section className="reminder-form-panel">
          <div className="panel-heading">
            <div className="panel-icon">＋</div>
            <div><h2>Create a reminder</h2><p>Pick the application and when you want to be notified.</p></div>
          </div>
          <form className="reminder-form" onSubmit={submit}>
            <label>Job application
              <select value={form.applicationId ?? ""} onChange={(e) => setForm((current) => ({ ...current, applicationId: e.target.value ? Number(e.target.value) : undefined }))} required>
                <option value="">Choose a job application</option>
                {applications.map((app) => {
                  const id = getApplicationId(app);
                  return id === undefined ? null : <option key={id} value={id}>{app.companyName} — {app.jobTitle}</option>;
                })}
              </select>
            </label>
            {loading && <p className="muted">Loading your applications…</p>}
            {loadError && <div className="inline-error"><p>{loadError}</p><button className="button secondary" type="button" onClick={() => void load()}>Try again</button></div>}
            {!loading && !loadError && applications.length === 0 && <div className="empty-apps">No applications yet. Add a job application first, then create a reminder.</div>}
            <label>Reminder title
              <input value={form.title} onChange={(e) => setForm((current) => ({ ...current, title: e.target.value }))} placeholder="e.g. Follow up with recruiter" required maxLength={160} />
            </label>
            <label>Date and time
              <input type="datetime-local" min={localDateTimeValue()} value={form.reminderDateTime} onChange={(e) => setForm((current) => ({ ...current, reminderDateTime: e.target.value }))} required />
              <small className="field-hint">Email is checked by the backend scheduler every 30 seconds.</small>
            </label>
            <label>Notes <span className="optional-label">Optional</span>
              <textarea rows={3} value={form.notes ?? ""} onChange={(e) => setForm((current) => ({ ...current, notes: e.target.value }))} placeholder="What do you need to remember?" maxLength={1000} />
            </label>
            {reminderError && <div className="inline-error"><p>Unable to load reminders: {reminderError}</p><button className="button secondary" type="button" onClick={() => void load()}>Retry</button></div>}
            <button className="button reminder-submit" type="submit" disabled={saving || loading || applications.length === 0 || Boolean(loadError)}>{saving ? "Saving reminder…" : "＋  Save reminder"}</button>
          </form>
          <div className="email-tip"><span>✉</span><p><strong>Email notifications</strong><br />A reminder email is sent to the email address you use to sign in, when Gmail SMTP is configured and the backend is running.</p></div>
        </section>

        <section className="reminder-list-panel">
          <div className="reminder-list-heading"><div><span className="eyebrow">YOUR PLAN</span><h2>Upcoming reminders</h2><p>Keep your job search moving forward.</p></div><button className="button secondary refresh-button" type="button" onClick={() => void load()} disabled={loading}>{loading ? "Refreshing…" : "↻ Refresh"}</button></div>
          {reminderError ? <div className="empty-state"><div className="empty-state-icon">!</div><h3>Could not load reminders</h3><p>{reminderError}</p><button className="button" type="button" onClick={() => void load()}>Try again</button></div> : items.length === 0 ? <div className="empty-state"><div className="empty-state-icon">◷</div><h3>Your schedule starts here</h3><p>Create your first reminder and we’ll keep it organized here.</p></div> : <div className="reminder-cards">{items.map((reminder, index) => <ReminderCard key={reminder.id ?? reminder.reminderId ?? index} reminder={reminder} onDelete={remove} />)}</div>}
          <div className="scheduler-note"><span>⏱</span><p><strong>Automatic email delivery</strong><br />Keep the Spring Boot backend running. If your computer or server is off at the scheduled time, the email will be sent when the scheduler runs again.</p></div>
        </section>
      </div>

      <section className="email-test-panel"><div className="email-test-icon">✉</div><div><h3>Check your email setup</h3><p>Send a test email to the email address linked to your account before relying on scheduled reminders.</p></div><button className="button secondary" type="button" onClick={testEmail} disabled={sendingTest}>{sendingTest ? "Sending test…" : "Send test email"}</button></section>
    </section>
  );
}
