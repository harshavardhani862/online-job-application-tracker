import api from "./api";
import type { Interview } from "../types/Interview";

const list = (data: unknown): Interview[] =>
  Array.isArray(data) ? data : ((data as { content?: Interview[] })?.content ?? []);

export async function getInterviews() {
  const response = await api.get("/interviews");
  return list(response.data);
}
export async function getInterview(id: number) {
  const response = await api.get(`/interviews/${id}`);
  return response.data;
}
export async function createInterview(data: Interview) {
  const response = await api.post("/interviews", data);
  return response.data;
}
export async function updateInterview(id: number, data: Interview) {
  const response = await api.put(`/interviews/${id}`, data);
  return response.data;
}
export async function deleteInterview(id: number) {
  await api.delete(`/interviews/${id}`);
}
