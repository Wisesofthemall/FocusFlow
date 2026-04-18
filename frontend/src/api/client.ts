import axios from 'axios';

export const TOKEN_KEY = 'focusflow_token';
export const USER_KEY = 'focusflow_user';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const client = axios.create({ baseURL });

client.interceptors.request.use((cfg) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    cfg.headers.Authorization = `Bearer ${token}`;
  }
  return cfg;
});

client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  },
);

export function apiErrorMessage(err: unknown, fallback = 'Request failed'): string {
  if (axios.isAxiosError(err)) {
    const msg = err.response?.data?.message;
    if (typeof msg === 'string' && msg.length > 0) return msg;
    if (err.response?.status) return `${err.response.status} ${err.response.statusText || fallback}`;
  }
  return fallback;
}
