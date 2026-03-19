import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import { Diarista, PageResponse } from '../services/types';
import { getStoredUser } from '../services/user';

export default function Diaristas() {
  const [bairro, setBairro] = useState('');
  const [diaristas, setDiaristas] = useState<Diarista[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState('');
  const user = getStoredUser();

  async function carregar() {
    setLoading(true);
    setErro('');
    try {
      const params = new URLSearchParams();
      params.set('page', '0');
      params.set('size', '12');
      params.set('sort', 'nome,asc');
      if (bairro.trim()) {
        params.set('bairro', bairro.trim());
      }
      const { data } = await api.get<PageResponse<Diarista>>(`/diaristas?${params.toString()}`);
      setDiaristas(data.content);
    } catch (e: any) {
      setErro(e?.response?.data?.erro || 'Não foi possível carregar as diaristas.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregar();
  }, []);

  return (
    <section className="page">
      <div className="hero">
        <div>
          <p className="eyebrow">Profissionais verificadas</p>
          <h1>
            {user?.role === 'DIARISTA'
              ? 'Seu perfil profissional em destaque'
              : 'Encontre a diarista certa para o seu momento'}
          </h1>
          <p className="hero-subtext">
            {user?.role === 'DIARISTA'
              ? 'Acompanhe como seu perfil aparece para clientes e mantenha seu atendimento organizado.'
              : 'Avaliações transparentes, agenda organizada e pagamentos simplificados em um só lugar.'}
          </p>
        </div>
        {user?.role !== 'DIARISTA' && (
          <div className="hero-card">
            <div>
              <span>Filtro rápido</span>
              <h3>Buscar por bairro</h3>
            </div>
            <div className="hero-form">
              <input
                value={bairro}
                onChange={(e) => setBairro(e.target.value)}
                placeholder="Ex: Nazaré, Umarizal"
              />
              <button className="primary-button" onClick={carregar}>Buscar</button>
            </div>
          </div>
        )}
      </div>

      <div className="section-header">
        <h2>{user?.role === 'DIARISTA' ? 'Seu cartão profissional' : 'Diaristas em destaque'}</h2>
        <p>
          {user?.role === 'DIARISTA'
            ? 'Este é o perfil que seus clientes visualizam.'
            : 'Resultados atualizados com base no seu bairro.'}
        </p>
      </div>

      {erro && <p className="error-text">{erro}</p>}
      {loading ? (
        <p className="loading-text">Carregando diaristas...</p>
      ) : (
        <div className="grid">
          {diaristas.map((diarista) => (
            <article key={diarista.id} className="card">
              <div className="card-top">
                <div className="avatar">{diarista.nome.slice(0, 2).toUpperCase()}</div>
                <div>
                  <h3>{diarista.nome}</h3>
                  <p>{diarista.bairro || 'Bairro não informado'}</p>
                </div>
              </div>
              <p className="card-text">{diarista.experiencia || 'Experiência em limpeza residencial e pós-obra.'}</p>
              <div className="card-actions">
                <Link to={`/diaristas/${diarista.id}`} className="secondary-button">
                  Ver perfil
                </Link>
                <span className="badge">Disponível</span>
              </div>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
