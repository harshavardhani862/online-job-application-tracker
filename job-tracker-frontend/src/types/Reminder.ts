export interface Reminder {
  id?: number;
  reminderId?: number;
  applicationId?: number;
  companyName?: string;
  jobTitle?: string;
  title: string;
  reminderDateTime: string;
  notes?: string;
  completed?: boolean;
  notificationSent?: boolean;
  notificationSentAt?: string | null;
}
