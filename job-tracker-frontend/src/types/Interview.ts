export interface Interview {
  id?: number;
  interviewId?: number;
  applicationId: number;
  interviewDate: string;
  interviewTime?: string;
  interviewType?: string;
  interviewer?: string;
  meetingLink?: string;
  notes?: string;
}
