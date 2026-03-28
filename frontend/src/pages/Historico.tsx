import { useEffect, useState } from 'react';
import api from '../services/api';
import { Agendamento, PageResponse } from '../services/types';
import { getStoredUser } from '../services/user';

const statusLabels: Record<string, string> = {
  PENDENTE: 'Pendente',
  CONFIRMADO: 'Confirmado',
  CONCLUIDO: 'Concluído',
  CANCELADO: 'Cancelado'
};

export default function Historico() {
  const [agendamentos, setAgendamentos] = useState<Agendamento[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');
  const [avaliacoes, setAvaliacoes] = useState<Record<number, { nota: number; comentario: string; loading: boolean; erro: string }>>({});
  const user = getStoredUser();

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const { data } = await api.get<PageResponse<Agendamento>>('/agendamentos?page=0&size=20&sort=dataServico,desc');
      setAgendamentos(data.content);
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível carregar o histórico.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  function updateAvaliacao(id: number, field: 'nota' | 'comentario', value: string | number) {
    setAvaliacoes((prev) => ({
      ...prev,
      [id]: {
        nota: prev[id]?.nota || 5,
        comentario: prev[id]?.comentario || '',
        loading: prev[id]?.loading || false,
        erro: prev[id]?.erro || '',
        [field]: value
      }
    }));
  }

  async function enviarAvaliacao(agendamento: Agendamento) {
    if (!user?.clienteId) {
      setErro('Cliente não encontrado. Faça login novamente.');
      return;
    }
    const state = avaliacoes[agendamento.id] || { nota: 5, comentario: '', loading: false, erro: '' };
    setAvaliacoes((prev) => ({
      ...prev,
      [agendamento.id]: { ...state, loading: true, erro: '' }
    }));
    try {
      await api.post('/avaliacoes', {
        clienteId: user.clienteId,
        diaristaId: agendamento.diarista?.id,
        nota: state.nota,
        comentario: state.comentario
      });
      setAvaliacoes((prev) => ({
        ...prev,
        [agendamento.id]: { ...state, loading: false, erro: '' }
      }));
    } catch (e: any) {
      setAvaliacoes((prev) => ({
        ...prev,
        [agendamento.id]: {
          ...state,
          loading: false,
          erro: e?.response?.data?.erro || 'Não foi possível enviar a avaliação.'
        }
      }));
    }
  }

  return (
    <section className="page">
      <div className="section-header">
        <h2>Histórico de serviços</h2>
        <p>Acompanhe todos os seus agendamentos e avaliações.</p>
      </div>
      {erro && <p className="error-text">{erro}</p>}
      {loading ? (
        <p className="loading-text">Carregando histórico...</p>
      ) : (
        <div className="stack">
          {agendamentos.map((agendamento) => (
            <div key={agendamento.id} className="row-card">
              <div>
                <h3>{agendamento.diarista?.nome || 'Diarista'}</h3>
                <p>{agendamento.diarista?.bairro || 'Belém'} • {agendamento.dataServico}</p>
              </div>
              <div>
                <span className={`pill ${agendamento.status.toLowerCase()}`}>
                  {user?.role === 'CLIENTE' && agendamento.status === 'PENDENTE'
                    ? 'Aguardando confirmação'
                    : statusLabels[agendamento.status]}
                </span>
              </div>
              <div className="row-meta">
                <span>Cliente: {agendamento.cliente?.nome}</span>
                <span>Obs: {agendamento.observacoes || 'Sem observações'}</span>
              </div>
              {user?.role === 'CLIENTE' && agendamento.status === 'CONCLUIDO' && (
                <div className="avaliacao-box">
                  <div>
                    <label>
                      Nota
                      <select
                        value={avaliacoes[agendamento.id]?.nota || 5}
                        onChange={(e) => updateAvaliacao(agendamento.id, 'nota', Number(e.target.value))}
                      >
                        {[1, 2, 3, 4, 5].map((n) => (
                          <option key={n} value={n}>{n}</option>
                        ))}
                      </select>
                    </label>
                  </div>
                  <div className="avaliacao-form">
                    <textarea
                      placeholder="Conte como foi o atendimento"
                      value={avaliacoes[agendamento.id]?.comentario || ''}
                      onChange={(e) => updateAvaliacao(agendamento.id, 'comentario', e.target.value)}
                    />
                    <button
                      className="primary-button"
                      onClick={() => enviarAvaliacao(agendamento)}
                      disabled={avaliacoes[agendamento.id]?.loading}
                    >
                      {avaliacoes[agendamento.id]?.loading ? 'Enviando...' : 'Enviar avaliação'}
                    </button>
                    {avaliacoes[agendamento.id]?.erro && (
                      <p className="error-text">{avaliacoes[agendamento.id]?.erro}</p>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
