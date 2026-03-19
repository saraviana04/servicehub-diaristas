import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../services/api';
import { Diarista, PageResponse } from '../services/types';
import { getStoredUser } from '../services/user';

export default function DiaristaDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [diarista, setDiarista] = useState<Diarista | null>(null);
  const [clienteId, setClienteId] = useState<number | null>(null);
  const [dataServico, setDataServico] = useState('');
  const [observacoes, setObservacoes] = useState('');
  const [status, setStatus] = useState('PENDENTE');
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
      if (user?.clienteId) {
        setClienteId(user.clienteId);
      } else {
        const clientesResponse = await api.get<PageResponse<{ id: number }>>('/clientes?page=0&size=1');
        const cliente = clientesResponse.data.content[0];
        if (cliente) {
          setClienteId(cliente.id);
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
        status,
        observacoes
      });
      navigate('/historico');
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível agendar.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="page">
      {loading ? (
        <p className="loading-text">Carregando perfil...</p>
      ) : erro ? (
        <p className="error-text">{erro}</p>
      ) : diarista ? (
        <div className="detail-layout">
          <div className="detail-card">
            <div className="detail-header">
              <div className="avatar big">{diarista.nome.slice(0, 2).toUpperCase()}</div>
              <div>
                <h2>{diarista.nome}</h2>
                <p>{diarista.bairro || 'Belém'}</p>
              </div>
            </div>
            <div className="detail-info">
              <div>
                <span>Experiência</span>
                <strong>{diarista.experiencia || '4+ anos'}</strong>
              </div>
              <div>
                <span>Nota média</span>
                <strong>4.9</strong>
              </div>
              <div>
                <span>Disponibilidade</span>
                <strong>Alta</strong>
              </div>
            </div>
            <p className="detail-text">
              Profissional especializada em limpeza residencial, pós-obra e organização de ambientes.
              Atendimento com produtos próprios e checklist personalizado.
            </p>
          </div>

          <div className="detail-form">
            <h3>Agendar serviço</h3>
            {user?.role !== 'CLIENTE' ? (
              <p className="helper-text">
                Apenas clientes podem criar agendamentos. Troque para o perfil de cliente para continuar.
              </p>
            ) : (
              <>
                <label>
                  Data do serviço
                  <input type="date" value={dataServico} onChange={(e) => setDataServico(e.target.value)} />
                </label>
                <label>
                  Status inicial
                  <select value={status} onChange={(e) => setStatus(e.target.value)}>
                    <option value="PENDENTE">Pendente</option>
                    <option value="CONFIRMADO">Confirmado</option>
                  </select>
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
            )}
          </div>
        </div>
      ) : null}
    </section>
  );
}
