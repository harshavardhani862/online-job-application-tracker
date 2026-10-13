export type ApplicationStatus =
  | "APPLIED"
  | "SHORTLISTED"
  | "INTERVIEW"
  | "SELECTED"
  | "OFFER"
  | "REJECTED"
  | "WITHDRAWN";

export interface JobApplication {
  id?: number;
  applicationId?: number;
  companyName: string;
  jobTitle: string;
  jobUrl?: string;
  location?: string;
  salary?: string;
  applicationDate: string;
  status: ApplicationStatus;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ApplicationRequest {
  companyName: string;
  jobTitle: string;
  jobUrl?: string;
  location?: string;
  salary?: string;
  applicationDate: string;
  status: ApplicationStatus;
  notes?: string;
}
