import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register, setToken, Role } from '../services/auth';
import { fetchMe } from '../services/user';

export default function Register() {
  const navigate = useNavigate();
  const [role, setRole] = useState<Role>('CLIENTE');
  const [nome, setNome] = useState('');
  const [telefone, setTelefone] = useState('');
  const [bairro, setBairro] = useState('');
  const [experiencia, setExperiencia] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setErro('');
    setLoading(true);
    try {
      const { token } = await register({
        email,
        senha,
        role,
        nome,
        telefone,
        bairro,
        experiencia: role === 'DIARISTA' ? experiencia : undefined
      });
      setToken(token);
      try {
        await fetchMe();
      } catch {
        // ignora erro de perfil para não bloquear navegação
      }
      navigate('/diaristas');
    } catch (e: any) {
      const data = e?.response?.data;
      if (data && typeof data === 'object' && !Array.isArray(data)) {
        const firstKey = Object.keys(data)[0];
        setErro(firstKey ? `${firstKey}: ${data[firstKey]}` : 'Não foi possível cadastrar.');
      } else {
        setErro(data?.erro || 'Não foi possível cadastrar.');
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-layout">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Criar conta</h2>
        <div className="role-switch">
          <button type="button" className={role === 'CLIENTE' ? 'active' : ''} onClick={() => setRole('CLIENTE')}>
            Sou cliente
          </button>
          <button type="button" className={role === 'DIARISTA' ? 'active' : ''} onClick={() => setRole('DIARISTA')}>
            Sou diarista
          </button>
        </div>
        <label>
          Nome completo
          <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Digite seu nome" />
        </label>
        <label>
          Telefone
          <input value={telefone} onChange={(e) => setTelefone(e.target.value)} placeholder="(91) 90000-0000" />
        </label>
        <label>
          Bairro
          <input value={bairro} onChange={(e) => setBairro(e.target.value)} placeholder="Ex: Umarizal" />
        </label>
        {role === 'DIARISTA' && (
          <label>
            Experiência
            <input value={experiencia} onChange={(e) => setExperiencia(e.target.value)} placeholder="Ex: 4 anos" />
          </label>
        )}
        <label>
          E-mail
          <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="seuemail@exemplo.com" />
        </label>
        <label>
          Senha
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder="Crie uma senha" />
        </label>
        {erro && <p className="error-text">{erro}</p>}
        <button className="primary-button" disabled={loading}>
          {loading ? 'Criando...' : 'Criar conta'}
        </button>
        <p className="helper-text">
          Já tem conta? <Link to="/login">Entrar agora</Link>
        </p>
      </form>
      <div className="auth-copy">
        <h1>Seja cliente ou diarista e ganhe organização</h1>
        <p>
          Agende com segurança, receba pagamentos e mantenha um histórico claro de atendimentos.
        </p>
        <ul className="auth-list">
          <li>Calendário de serviços</li>
          <li>Pagamentos e avaliações centralizadas</li>
          <li>Perfil profissional em destaque</li>
        </ul>
      </div>
    </section>
  );
}
