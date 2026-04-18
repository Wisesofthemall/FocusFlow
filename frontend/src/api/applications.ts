import { client } from './client';
import type { ApplicationPriority, ApplicationStatus, JobApplication } from '../types';

export interface CreateApplicationBody {
  company: string;
  roleTitle: string;
  status: ApplicationStatus;
  nextActionDate: string;
  priority: ApplicationPriority;
}

export async function listApplications(): Promise<JobApplication[]> {
  const { data } = await client.get<JobApplication[]>('/api/applications');
  return data;
}

export async function createApplication(body: CreateApplicationBody): Promise<JobApplication> {
  const { data } = await client.post<JobApplication>('/api/applications', body);
  return data;
}

export async function deleteApplication(id: number): Promise<void> {
  await client.delete(`/api/applications/${id}`);
}
