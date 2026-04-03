import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';
import { Agendamento, DiaristaPerfil, PageResponse } from '../services/types';
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
  const [diarista, setDiarista] = useState<DiaristaPerfil | null>(null);
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
  const hoje = new Date().toISOString().split('T')[0];

  function formatarData(data?: string) {
    if (!data) return '';
    const parsed = new Date(`${data}T00:00:00`);
    if (Number.isNaN(parsed.getTime())) return data;
    return new Intl.DateTimeFormat('pt-BR').format(parsed);
  }

  function formatarMoeda(valor?: number) {
    if (valor === undefined || valor === null) return null;
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
  }

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const diaristaResponse = await api.get<DiaristaPerfil>(`/diaristas/${id}`);
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
    if (dataServico < hoje) {
      setErro('A data do serviço não pode ser no passado.');
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
                  <p className="profile-location">
                    {diarista.bairro || 'Belém'} • Atendimento residencial
                    {diarista.raioAtendimentoKm ? ` • ${diarista.raioAtendimentoKm} km de raio` : ''}
                  </p>
                  <p className="profile-price">
                    {formatarMoeda(diarista.precoBase)
                      ? `A partir de ${formatarMoeda(diarista.precoBase)}`
                      : 'Preço sob consulta'}
                    {diarista.disponibilidade ? ` • Disponibilidade ${diarista.disponibilidade.toLowerCase()}` : ''}
                  </p>
                  <div className="profile-badges">
                    {diarista.agendaFlexivel && <span className="profile-badge">Agenda flexível</span>}
                    {diarista.materiaisProprios && <span className="profile-badge">Materiais próprios</span>}
                    {diarista.checklist && <span className="profile-badge">Checklist</span>}
                    {!diarista.agendaFlexivel && !diarista.materiaisProprios && !diarista.checklist && (
                      <span className="profile-badge">Atendimento residencial</span>
                    )}
                  </div>
                </div>
              </div>

              <div className="profile-stats">
                <div className="profile-stat">
                  <span>Experiência</span>
                  <strong>{diarista.experiencia || 'Sem dados'}</strong>
                </div>
                <div className="profile-stat">
                  <span>Nota média</span>
                  <strong>{diarista.notaMedia ? diarista.notaMedia.toFixed(1) : 'Sem avaliações'}</strong>
                  <small>{diarista.totalAvaliacoes ? `${diarista.totalAvaliacoes} avaliações` : 'Sem avaliações ainda'}</small>
                </div>
                <div className="profile-stat">
                  <span>Serviços concluídos</span>
                  <strong>{diarista.servicosConcluidos ?? 0}</strong>
                </div>
                <div className="profile-stat">
                  <span>Disponibilidade</span>
                  <strong>{diarista.disponibilidade || 'Sob consulta'}</strong>
                </div>
              </div>

              <div className="profile-about">
                <h4>Sobre a diarista</h4>
                <p>
                  {diarista.bio ||
                    'Profissional especializada em limpeza residencial, pós-obra e organização de ambientes. Atendimento com produtos próprios e checklist personalizado.'}
                </p>
                <div className="profile-tags">
                  {(diarista.especialidades && diarista.especialidades.length > 0
                    ? diarista.especialidades
                    : ['Limpeza pesada', 'Organização', 'Pós-obra', 'Pet friendly']
                  ).map((tag) => (
                    <span key={tag}>{tag}</span>
                  ))}
                </div>
              </div>

              {diarista.avaliacoesRecentes && diarista.avaliacoesRecentes.length > 0 && (
                <div className="profile-panel profile-reviews">
                  <h4>Avaliações recentes</h4>
                  <div className="review-list">
                    {diarista.avaliacoesRecentes.map((avaliacao) => (
                      <article key={avaliacao.id} className="review-card">
                        <div className="review-header">
                          <strong>{avaliacao.clienteNome || 'Cliente'}</strong>
                          <span className="review-rating">{avaliacao.nota} ★</span>
                        </div>
                        <p>{avaliacao.comentario || 'Sem comentário.'}</p>
                      </article>
                    ))}
                  </div>
                </div>
              )}

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
                            <span className="schedule-date">{formatarData(agendamento.dataServico)}</span>
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
              ) : (
                <p className="helper-text">Ainda não há agendamentos relacionados a esta diarista.</p>
              )}
            </div>

          {user?.role === 'CLIENTE' && (
            <div className="detail-form">
              <h3>Agendar serviço</h3>
              <>
                <label>
                  Data do serviço
                  <input
                    type="date"
                    value={dataServico}
                    min={hoje}
                    onChange={(e) => setDataServico(e.target.value)}
                  />
                </label>
                <label>
                  Observações
                  <textarea
                    value={observacoes}
                    onChange={(e) => setObservacoes(e.target.value)}
                    placeholder="Descreva detalhes do imóvel, número de cômodos, etc."
                  />
                </label>
                {diarista.precoBase && (
                  <p className="helper-text">Valor estimado: {formatarMoeda(diarista.precoBase)}</p>
                )}
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
                      {formatarData(agendamentoSelecionado.dataServico)} •{' '}
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
