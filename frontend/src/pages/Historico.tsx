import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Agendamento, Avaliacao, PageResponse } from '../services/types';
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
  const [avaliacoes, setAvaliacoes] = useState<Record<number, { nota: number; comentario: string; loading: boolean; erro: string; sucesso: boolean }>>({});
  const [concluindo, setConcluindo] = useState<Record<number, boolean>>({});
  const [modalSucesso, setModalSucesso] = useState('');
  const user = getStoredUser();
  const navigate = useNavigate();
  const hoje = new Date().toISOString().split('T')[0];

  function formatarData(data?: string) {
    if (!data) return '';
    const parsed = new Date(`${data}T00:00:00`);
    if (Number.isNaN(parsed.getTime())) return data;
    return new Intl.DateTimeFormat('pt-BR').format(parsed);
  }

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const { data } = await api.get<PageResponse<Agendamento>>('/agendamentos?page=0&size=20&sort=dataServico,desc');
      setAgendamentos(data.content);
      if (user?.role === 'CLIENTE') {
        const avaliacoesResponse = await api.get<PageResponse<Avaliacao>>('/avaliacoes?page=0&size=200&sort=id,desc');
        const avaliacaoMap: Record<number, { nota: number; comentario: string; loading: boolean; erro: string; sucesso: boolean }> = {};
        avaliacoesResponse.data.content.forEach((avaliacao) => {
          if (avaliacao.agendamentoId) {
            avaliacaoMap[avaliacao.agendamentoId] = {
              nota: avaliacao.nota || 5,
              comentario: avaliacao.comentario || '',
              loading: false,
              erro: '',
              sucesso: true
            };
          }
        });
        setAvaliacoes((prev) => ({ ...prev, ...avaliacaoMap }));
      }
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
        sucesso: prev[id]?.sucesso || false,
        [field]: value
      }
    }));
  }

  async function enviarAvaliacao(agendamento: Agendamento) {
    if (!user?.clienteId) {
      setErro('Cliente não encontrado. Faça login novamente.');
      return;
    }
    const state = avaliacoes[agendamento.id] || { nota: 5, comentario: '', loading: false, erro: '', sucesso: false };
    setAvaliacoes((prev) => ({
      ...prev,
      [agendamento.id]: { ...state, loading: true, erro: '', sucesso: false }
    }));
    try {
      await api.post('/avaliacoes', {
        clienteId: user.clienteId,
        diaristaId: agendamento.diarista?.id,
        agendamentoId: agendamento.id,
        nota: state.nota,
        comentario: state.comentario
      });
      setAvaliacoes((prev) => ({
        ...prev,
        [agendamento.id]: { ...state, loading: false, erro: '', sucesso: true }
      }));
      await carregar();
      setModalSucesso('Avaliação enviada com sucesso!');
      setTimeout(() => {
        setModalSucesso('');
        navigate('/diaristas');
      }, 1600);
    } catch (e: any) {
      setAvaliacoes((prev) => ({
        ...prev,
        [agendamento.id]: {
          ...state,
          loading: false,
          erro: e?.response?.data?.erro || 'Não foi possível enviar a avaliação.',
          sucesso: false
        }
      }));
    }
  }

  async function concluirServico(agendamento: Agendamento) {
    if (!user?.clienteId) {
      setErro('Cliente não encontrado. Faça login novamente.');
      return;
    }
    setConcluindo((prev) => ({ ...prev, [agendamento.id]: true }));
    setErro('');
    try {
      await api.put(`/agendamentos/${agendamento.id}`, {
        clienteId: agendamento.cliente?.id,
        diaristaId: agendamento.diarista?.id,
        dataServico: agendamento.dataServico,
        status: 'CONCLUIDO',
        observacoes: agendamento.observacoes
      });
      setAgendamentos((prev) =>
        prev.map((item) => (item.id === agendamento.id ? { ...item, status: 'CONCLUIDO' } : item))
      );
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível concluir o serviço.');
    } finally {
      setConcluindo((prev) => ({ ...prev, [agendamento.id]: false }));
    }
  }

  return (
    <section className="page">
      <div className="section-header">
        <h2>Histórico de serviços</h2>
        <p>Acompanhe todos os seus agendamentos e avaliações.</p>
      </div>
      {modalSucesso && (
        <div className="modal-overlay" onClick={() => setModalSucesso('')}>
          <div className="modal-card" onClick={(event) => event.stopPropagation()}>
            <div className="modal-header">
              <div>
                <p className="eyebrow">Avaliação enviada</p>
                <h3>{modalSucesso}</h3>
                <p className="helper-text">Voltando para a tela de diaristas...</p>
              </div>
              <button type="button" className="modal-close" onClick={() => setModalSucesso('')}>
                Fechar
              </button>
            </div>
          </div>
        </div>
      )}
      {erro && <p className="error-text">{erro}</p>}
      {loading ? (
        <p className="loading-text">Carregando histórico...</p>
      ) : (
        <div className="stack">
          {agendamentos.map((agendamento) => (
            <div key={agendamento.id} className="row-card">
              <div>
                <h3>{agendamento.diarista?.nome || 'Diarista'}</h3>
                <p>{agendamento.diarista?.bairro || 'Belém'} • {formatarData(agendamento.dataServico)}</p>
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
              {user?.role === 'CLIENTE' &&
                agendamento.status === 'CONFIRMADO' &&
                agendamento.dataServico <= hoje && (
                  <button
                    className="secondary-button"
                    onClick={() => concluirServico(agendamento)}
                    disabled={concluindo[agendamento.id]}
                  >
                    {concluindo[agendamento.id] ? 'Concluindo...' : 'Concluir serviço'}
                  </button>
                )}
              {user?.role === 'CLIENTE' && agendamento.status === 'CONCLUIDO' && (
                avaliacoes[agendamento.id]?.sucesso ? (
                  <p className="success-text">Avaliação enviada. Obrigado!</p>
                ) : (
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
                )
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
