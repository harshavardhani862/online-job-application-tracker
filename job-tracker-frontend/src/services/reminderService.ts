import api from "./api";
import type { Reminder } from "../types/Reminder";

const list = (data: unknown): Reminder[] =>
  Array.isArray(data) ? data : ((data as { content?: Reminder[] })?.content ?? []);

export async function getReminders() {
  const response = await api.get("/reminders");
  return list(response.data);
}

export async function getReminder(id: number) {
  const response = await api.get(`/reminders/${id}`);
  return response.data as Reminder;
}

export async function createReminder(data: Reminder) {
  const response = await api.post("/reminders", data);
  return response.data as Reminder;
}

export async function updateReminder(id: number, data: Reminder) {
  const response = await api.put(`/reminders/${id}`, data);
  return response.data as Reminder;
}

export async function deleteReminder(id: number) {
  await api.delete(`/reminders/${id}`);
}

export async function sendTestEmail() {
  const response = await api.post("/notifications/test");
  return response.data as { message: string; email: string };
}
