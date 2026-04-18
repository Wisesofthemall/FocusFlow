export type ApplicationStatus =
  | 'APPLIED'
  | 'INTERVIEWING'
  | 'OFFER'
  | 'REJECTED'
  | 'WITHDRAWN';

export type ApplicationPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export type EventSource = 'GOOGLE' | 'MANUAL';

export interface JobApplication {
  id: number;
  company: string;
  roleTitle: string;
  status: ApplicationStatus;
  nextActionDate: string;
  priority: ApplicationPriority;
  createdAt: string;
  updatedAt: string;
}

export interface CalendarEvent {
  id: number;
  title: string;
  startsAt: string;
  endsAt: string;
  source: EventSource;
  externalId: string | null;
}

export interface DashboardSlice {
  applicationsDueInRange: JobApplication[];
  eventsInRange: CalendarEvent[];
}

export interface AuthUser {
  id: number;
  name: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  expiresAt: number;
  user: AuthUser;
}

export interface RemoteOkJob {
  id: string;
  company: string | null;
  position: string | null;
  tags: string[] | null;
  url: string | null;
  date: string | null;
  location: string | null;
}
