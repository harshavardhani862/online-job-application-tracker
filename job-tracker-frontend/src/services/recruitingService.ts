import api from "./api";
import type {
  Candidate,
  CandidateRequest,
  InvitationDetails,
  JobOpening,
  JobRequest,
  RecruitingOverview,
  Team,
  TeamInvitation,
  TeamMember,
  TeamRole,
} from "../types/Recruiting";

export async function getRecruitingOverview() {
  const response = await api.get<RecruitingOverview>("/ats/overview");
  return response.data;
}

export async function getOpenings() {
  const response = await api.get<JobOpening[]>("/ats/jobs");
  return response.data;
}

export async function createOpening(data: JobRequest) {
  const response = await api.post<JobOpening>("/ats/jobs", data);
  return response.data;
}

export async function updateOpening(id: number, data: JobRequest) {
  const response = await api.put<JobOpening>(`/ats/jobs/${id}`, data);
  return response.data;
}

export async function deleteOpening(id: number) {
  await api.delete(`/ats/jobs/${id}`);
}

export async function getCandidates() {
  const response = await api.get<Candidate[]>("/ats/candidates");
  return response.data;
}

export async function createCandidate(data: CandidateRequest) {
  const response = await api.post<Candidate>("/ats/candidates", data);
  return response.data;
}

export async function updateCandidate(id: number, data: CandidateRequest) {
  const response = await api.put<Candidate>(`/ats/candidates/${id}`, data);
  return response.data;
}

export async function deleteCandidate(id: number) {
  await api.delete(`/ats/candidates/${id}`);
}

export async function getTeam() {
  const response = await api.get<Team>("/ats/team");
  return response.data;
}

export async function createTeamInvitation(email: string, role: Exclude<TeamRole, "OWNER">) {
  const response = await api.post<TeamInvitation>("/ats/team/invitations", { email, role });
  return response.data;
}

export async function getInvitation(token: string) {
  const response = await api.get<InvitationDetails>(`/ats/invitations/${token}`);
  return response.data;
}
