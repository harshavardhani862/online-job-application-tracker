export type OpeningStatus = "OPEN" | "ON_HOLD" | "CLOSED";
export type CandidateStage = "NEW" | "SCREENING" | "INTERVIEW" | "OFFER" | "HIRED" | "REJECTED";
export type TeamRole = "OWNER" | "ADMIN" | "RECRUITER";

export interface JobOpening {
  id: number;
  title: string;
  department: string;
  location: string;
  employmentType: string;
  description?: string;
  status: OpeningStatus;
  createdAt: string;
}

export interface JobRequest {
  title: string;
  department: string;
  location: string;
  employmentType: string;
  description?: string;
  status: OpeningStatus;
}

export interface Candidate {
  id: number;
  name: string;
  email: string;
  phone?: string;
  location?: string;
  source: string;
  resumeUrl?: string;
  notes?: string;
  jobId?: number;
  jobTitle?: string;
  stage: CandidateStage;
  createdAt: string;
  updatedAt: string;
}

export interface CandidateRequest {
  name: string;
  email: string;
  phone?: string;
  location?: string;
  source: string;
  resumeUrl?: string;
  notes?: string;
  jobId?: number;
  stage: CandidateStage;
}

export interface TeamMember {
  id: number;
  name: string;
  email: string;
  role: TeamRole;
  joinedAt: string;
}

export interface Team {
  organizationName: string;
  members: TeamMember[];
  pendingInvitations: TeamInvitation[];
}

export interface TeamInvitation {
  id: number;
  email: string;
  role: Exclude<TeamRole, "OWNER">;
  token: string;
  createdAt: string;
  expiresAt: string;
}

export interface InvitationDetails {
  organizationName: string;
  email: string;
  role: Exclude<TeamRole, "OWNER">;
}

export interface RecruitingOverview {
  organizationName: string;
  openJobs: number;
  totalCandidates: number;
  newCandidates: number;
  screeningCandidates: number;
  interviews: number;
  offers: number;
  hires: number;
  jobs: JobOpening[];
  recentCandidates: Candidate[];
}
