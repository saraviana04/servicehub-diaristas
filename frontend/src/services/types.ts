export interface Cliente {
  id: number;
  nome: string;
  bairro?: string;
}

export interface Diarista {
  id: number;
  nome: string;
  bairro?: string;
  experiencia?: string;
}

export interface Agendamento {
  id: number;
  cliente: Cliente;
  diarista: Diarista;
  dataServico: string;
  status: string;
  observacoes?: string;
  createdAt?: string;
}

export interface Avaliacao {
  id: number;
  cliente: Cliente;
  diarista: Diarista;
  nota: number;
  comentario?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
