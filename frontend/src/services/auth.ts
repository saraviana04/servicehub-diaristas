import api from './api';

export type Role = 'CLIENTE' | 'DIARISTA' | 'ADMIN';

export interface RegisterPayload {
  email: string;
  senha: string;
  role: Role;
  nome?: string;
  telefone?: string;
  bairro?: string;
  experiencia?: string;
}

export async function login(email: string, senha: string) {
  const { data } = await api.post('/auth/login', { email, senha });
  return data as { token: string };
}

export async function register(payload: RegisterPayload) {
  const { data } = await api.post('/auth/register', payload);
  return data as { token: string };
}

export function setToken(token: string) {
  localStorage.setItem('servicehub_token', token);
}

export function clearToken() {
  localStorage.removeItem('servicehub_token');
}

export function getToken() {
  return localStorage.getItem('servicehub_token');
}
