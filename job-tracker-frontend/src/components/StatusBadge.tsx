import type { ApplicationStatus } from "../types/Application";

export default function StatusBadge({ status }: { status: ApplicationStatus }) {
  return <span className={`status status-${status.toLowerCase()}`}>{status}</span>;
}
