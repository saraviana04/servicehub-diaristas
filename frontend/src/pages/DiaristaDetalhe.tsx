import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';
import { Agendamento, Diarista, PageResponse } from '../services/types';
import { getStoredUser } from '../services/user';

const statusLabels: Record<string, string> = {
  PENDENTE: 'Pendente',
  CONFIRMADO: 'Confirmado',
  CONCLUIDO: 'Concluído',
  CANCELADO: 'Cancelado'
};

const statusOrder: Record<string, number> = {
  PENDENTE: 1,
  CONFIRMADO: 2,
  CONCLUIDO: 3,
  CANCELADO: 4
};

export default function DiaristaDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [diarista, setDiarista] = useState<Diarista | null>(null);
  const [agendamentosRelacionados, setAgendamentosRelacionados] = useState<Agendamento[]>([]);
  const [agendamentosLoading, setAgendamentosLoading] = useState(false);
  const [agendamentosErro, setAgendamentosErro] = useState('');
  const [agendamentoSelecionado, setAgendamentoSelecionado] = useState<Agendamento | null>(null);
  const [agendamentoSaving, setAgendamentoSaving] = useState(false);
  const [decisaoStatus, setDecisaoStatus] = useState('CONFIRMADO');
  const [modalSucesso, setModalSucesso] = useState('');
  const [clienteId, setClienteId] = useState<number | null>(null);
  const [dataServico, setDataServico] = useState('');
  const [observacoes, setObservacoes] = useState('');
  const [erro, setErro] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const user = getStoredUser();

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const diaristaResponse = await api.get<Diarista>(`/diaristas/${id}`);
      setDiarista(diaristaResponse.data);
      if (user?.role === 'CLIENTE') {
        if (user.clienteId) {
          setClienteId(user.clienteId);
        } else {
          try {
            const clientesResponse = await api.get<PageResponse<{ id: number }>>('/clientes?page=0&size=1');
            const cliente = clientesResponse.data.content[0];
            if (cliente) {
              setClienteId(cliente.id);
            }
          } catch {
            // ignora erro de cliente para não bloquear o perfil da diarista
          }
        }
      }

      if (user?.role === 'CLIENTE' || user?.role === 'DIARISTA') {
        setAgendamentosLoading(true);
        setAgendamentosErro('');
        try {
          const { data } = await api.get<PageResponse<Agendamento>>(
            '/agendamentos?page=0&size=20&sort=dataServico,desc'
          );
          const diaristaId = diaristaResponse.data.id;
          const relacionados = data.content.filter((agendamento) => agendamento.diarista?.id === diaristaId);
          setAgendamentosRelacionados(relacionados);
        } catch (e: any) {
          setAgendamentosErro(e?.response?.data?.erro || 'Não foi possível carregar os agendamentos.');
        } finally {
          setAgendamentosLoading(false);
        }
      }
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível carregar o perfil.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregar();
  }, [id]);

  useEffect(() => {
    if (agendamentoSelecionado) {
      setDecisaoStatus('CONFIRMADO');
    }
  }, [agendamentoSelecionado?.id]);

  async function criarAgendamento() {
    if (!clienteId) {
      setErro('Cliente não encontrado. Faça login novamente.');
      return;
    }
    if (!dataServico) {
      setErro('Informe a data do serviço.');
      return;
    }
    setSaving(true);
    setErro('');
    try {
      await api.post('/agendamentos', {
        clienteId,
        diaristaId: Number(id),
        dataServico,
        observacoes
      });
      navigate('/historico');
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível agendar.');
    } finally {
      setSaving(false);
    }
  }

  async function atualizarStatusAgendamento(novoStatus: string) {
    if (!agendamentoSelecionado) return;
    setAgendamentoSaving(true);
    setAgendamentosErro('');
    setModalSucesso('');
    try {
      await api.put(`/agendamentos/${agendamentoSelecionado.id}`, {
        clienteId: agendamentoSelecionado.cliente?.id,
        diaristaId: agendamentoSelecionado.diarista?.id,
        dataServico: agendamentoSelecionado.dataServico,
        status: novoStatus,
        observacoes: agendamentoSelecionado.observacoes
      });
      setAgendamentosRelacionados((prev) =>
        prev.map((item) => (item.id === agendamentoSelecionado.id ? { ...item, status: novoStatus } : item))
      );
      setModalSucesso(novoStatus === 'CONFIRMADO' ? 'Você confirmou o serviço.' : 'Você cancelou o serviço.');
      setTimeout(() => {
        setModalSucesso('');
        setAgendamentoSelecionado(null);
      }, 2000);
    } catch (e: any) {
      setAgendamentosErro(e?.response?.data?.erro || 'Não foi possível atualizar o agendamento.');
    } finally {
      setAgendamentoSaving(false);
    }
  }

  function tempoRestante(agendamento: Agendamento) {
    if (!agendamento.createdAt) return 'Prazo não disponível';
    const criadoEm = new Date(agendamento.createdAt);
    if (Number.isNaN(criadoEm.getTime())) return 'Prazo não disponível';
    const limite = new Date(criadoEm.getTime() + 48 * 60 * 60 * 1000);
    const diffMs = limite.getTime() - Date.now();
    if (diffMs <= 0) return 'Expirado';
    const horas = Math.floor(diffMs / (1000 * 60 * 60));
    const minutos = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    if (horas <= 0) return `${minutos} min restantes`;
    return `${horas}h ${minutos}m restantes`;
  }

  return (
    <section className="page">
      {loading ? (
        <p className="loading-text">Carregando perfil...</p>
      ) : erro ? (
        <p className="error-text">{erro}</p>
      ) : diarista ? (
        <>
          <div className={`detail-layout${user?.role === 'CLIENTE' ? '' : ' single'}`}>
          <div className="detail-card profile-card">
            <div className="profile-hero">
              <div className="profile-avatar">{diarista.nome.slice(0, 2).toUpperCase()}</div>
              <div className="profile-hero-text">
                <p className="eyebrow">Diarista verificada</p>
                <h2>{diarista.nome}</h2>
                <p className="profile-location">{diarista.bairro || 'Belém'} • Atendimento residencial</p>
                <div className="profile-badges">
                  <span className="profile-badge">Agenda flexível</span>
                  <span className="profile-badge">Materiais próprios</span>
                  <span className="profile-badge">Checklist</span>
                </div>
              </div>
            </div>

            <div className="profile-stats">
              <div className="profile-stat">
                <span>Experiência</span>
                <strong>{diarista.experiencia || '4+ anos'}</strong>
              </div>
              <div className="profile-stat">
                <span>Nota média</span>
                <strong>4.9</strong>
              </div>
              <div className="profile-stat">
                <span>Disponibilidade</span>
                <strong>Alta</strong>
              </div>
            </div>

            <div className="profile-about">
              <h4>Sobre a diarista</h4>
              <p>
                Profissional especializada em limpeza residencial, pós-obra e organização de ambientes.
                Atendimento com produtos próprios e checklist personalizado.
              </p>
              <div className="profile-tags">
                <span>Limpeza pesada</span>
                <span>Organização</span>
                <span>Pós-obra</span>
                <span>Pet friendly</span>
              </div>
            </div>

            {agendamentosLoading ? (
              <p className="loading-text">Carregando agendamentos...</p>
            ) : agendamentosErro ? (
              <p className="error-text">{agendamentosErro}</p>
            ) : agendamentosRelacionados.length > 0 ? (
              <div className="profile-panel">
                <h4>{user?.role === 'DIARISTA' ? 'Seus agendamentos' : 'Agendamento encontrado'}</h4>
                {user?.role === 'DIARISTA' && (
                  <p className="helper-text">Você tem 48 horas para responder ou o agendamento será cancelado.</p>
                )}
                <div className="schedule-list">
                  {[...agendamentosRelacionados]
                    .sort((a, b) => (statusOrder[a.status] || 99) - (statusOrder[b.status] || 99))
                    .map((agendamento) => {
                    const statusLabel = statusLabels[agendamento.status] || agendamento.status;
                    const podeInteragir = user?.role === 'DIARISTA' && agendamento.status === 'PENDENTE';
                    return (
                      <button
                        key={agendamento.id}
                        className={`schedule-item status-${agendamento.status.toLowerCase()}${podeInteragir ? ' is-clickable' : ''}`}
                        type="button"
                        onClick={() => podeInteragir && setAgendamentoSelecionado(agendamento)}
                        disabled={!podeInteragir}
                      >
                        <div>
                          <span className="schedule-date">{agendamento.dataServico}</span>
                          <span className="schedule-status">{statusLabel}</span>
                        </div>
                        <div className="schedule-meta">
                          {user?.role === 'DIARISTA' ? (
                            <span>Cliente: {agendamento.cliente?.nome || 'Cliente'}</span>
                          ) : (
                            <span>Diarista: {agendamento.diarista?.nome || 'Diarista'}</span>
                          )}
                          {user?.role === 'DIARISTA' && agendamento.status === 'PENDENTE' && (
                            <span className="schedule-deadline">Prazo: {tempoRestante(agendamento)}</span>
                          )}
                          {agendamento.observacoes && <span>Obs: {agendamento.observacoes}</span>}
                        </div>
                        {user?.role === 'CLIENTE' && agendamento.status === 'PENDENTE' && (
                          <span className="profile-waiting">Aguardando confirmação</span>
                        )}
                      </button>
                    );
                  })}
                </div>
                {user?.role === 'DIARISTA' && (
                  <p className="helper-text">Clique em um agendamento pendente para confirmar ou cancelar.</p>
                )}
              </div>
            ) : null}
          </div>

          {user?.role === 'CLIENTE' && (
            <div className="detail-form">
              <h3>Agendar serviço</h3>
              <>
                <label>
                  Data do serviço
                  <input type="date" value={dataServico} onChange={(e) => setDataServico(e.target.value)} />
                </label>
                <label>
                  Observações
                  <textarea
                    value={observacoes}
                    onChange={(e) => setObservacoes(e.target.value)}
                    placeholder="Descreva detalhes do imóvel, número de cômodos, etc."
                  />
                </label>
                {erro && <p className="error-text">{erro}</p>}
                <button className="primary-button" onClick={criarAgendamento} disabled={saving}>
                  {saving ? 'Agendando...' : 'Confirmar agendamento'}
                </button>
              </>
            </div>
          )}
          </div>
          {agendamentoSelecionado && (
            <div className="modal-overlay" onClick={() => setAgendamentoSelecionado(null)}>
              <div className="modal-card" onClick={(event) => event.stopPropagation()}>
                <div className="modal-header">
                  <div>
                    <p className="eyebrow">Responder agendamento</p>
                    <h3>Agendamento de {agendamentoSelecionado.cliente?.nome || 'Cliente'}</h3>
                    <p className="helper-text">
                      {agendamentoSelecionado.dataServico} •{' '}
                      {statusLabels[agendamentoSelecionado.status] || agendamentoSelecionado.status}
                    </p>
                  </div>
                  <button type="button" className="modal-close" onClick={() => setAgendamentoSelecionado(null)}>
                    Fechar
                  </button>
                </div>
              <div className="modal-body">
                <label className="modal-label">
                  Decisão da diarista
                  <select value={decisaoStatus} onChange={(event) => setDecisaoStatus(event.target.value)}>
                    <option value="CONFIRMADO">Confirmar</option>
                    <option value="CANCELADO">Cancelar</option>
                  </select>
                </label>
                <p className="helper-text">
                  Observações: {agendamentoSelecionado.observacoes || 'Sem observações'}
                </p>
                <p className="helper-text">Tem certeza da sua decisão?</p>
                {modalSucesso && <p className="success-text">{modalSucesso}</p>}
              </div>
              <div className="modal-actions">
                <button
                  type="button"
                  className="primary-button"
                  onClick={() => atualizarStatusAgendamento(decisaoStatus)}
                  disabled={agendamentoSaving}
                >
                  {agendamentoSaving ? 'Atualizando...' : 'Confirmar decisão'}
                </button>
              </div>
                {agendamentosErro && <p className="error-text">{agendamentosErro}</p>}
              </div>
            </div>
          )}
        </>
      ) : null}
    </section>
  );
}
