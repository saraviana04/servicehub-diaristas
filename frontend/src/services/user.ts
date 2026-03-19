import api from './api';
import { Role } from './auth';

export interface MeResponse {
  email: string;
  role: Role;
  clienteId?: number | null;
  diaristaId?: number | null;
}

const STORAGE_KEY = 'servicehub_user';

export async function fetchMe() {
  const { data } = await api.get<MeResponse>('/auth/me');
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  return data;
}

export function getStoredUser(): MeResponse | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as MeResponse;
  } catch {
    return null;
  }
}

export function clearStoredUser() {
  localStorage.removeItem(STORAGE_KEY);
}
