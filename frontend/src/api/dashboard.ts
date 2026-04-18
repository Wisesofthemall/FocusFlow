import { client } from './client';
import type { DashboardSlice } from '../types';

function formatLocalDateTime(d: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

export async function getDashboard(): Promise<DashboardSlice> {
  const now = new Date();
  const inAWeek = new Date(now.getTime() + 7 * 24 * 3600 * 1000);
  const params = {
    appWindowStart: formatLocalDateTime(now),
    appWindowEnd: formatLocalDateTime(inAWeek),
    eventWindowStart: now.toISOString(),
    eventWindowEnd: inAWeek.toISOString(),
  };
  const { data } = await client.get<DashboardSlice>('/api/dashboard', { params });
  return data;
}
