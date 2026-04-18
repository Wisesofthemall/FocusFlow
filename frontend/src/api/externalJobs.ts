import { client } from './client';
import type { RemoteOkJob } from '../types';

export async function getRemoteJobs(keyword = '', limit = 6): Promise<RemoteOkJob[]> {
  const { data } = await client.get<RemoteOkJob[]>('/api/jobs/remote', {
    params: { keyword, limit },
  });
  return data;
}
