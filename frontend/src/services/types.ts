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

export interface AvaliacaoResumo {
  id: number;
  nota: number;
  comentario?: string;
  clienteNome: string;
}

export interface DiaristaPerfil extends Diarista {
  bio?: string;
  especialidades?: string[];
  disponibilidade?: string;
  materiaisProprios?: boolean;
  agendaFlexivel?: boolean;
  checklist?: boolean;
  raioAtendimentoKm?: number;
  precoBase?: number;
  notaMedia?: number;
  totalAvaliacoes?: number;
  servicosConcluidos?: number;
  avaliacoesRecentes?: AvaliacaoResumo[];
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
  agendamentoId?: number | null;
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
