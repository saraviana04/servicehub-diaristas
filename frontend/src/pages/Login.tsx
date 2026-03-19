import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login, setToken } from '../services/auth';
import { fetchMe } from '../services/user';

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setErro('');
    setLoading(true);
    try {
      const { token } = await login(email, senha);
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
        setErro(firstKey ? `${firstKey}: ${data[firstKey]}` : 'Não foi possível entrar.');
      } else {
        setErro(data?.erro || 'Não foi possível entrar.');
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-layout">
      <div className="auth-copy">
        <h1>Conecte-se com diaristas confiáveis em Belém</h1>
        <p>
          Organize seu serviço, acompanhe avaliações e tenha segurança em cada contratação.
        </p>
        <div className="auth-highlight">
          <div>
            <strong>+200</strong>
            <span>profissionais cadastradas</span>
          </div>
          <div>
            <strong>4.8</strong>
            <span>média de avaliações</span>
          </div>
        </div>
      </div>
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Entrar</h2>
        <label>
          E-mail
          <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="seuemail@exemplo.com" />
        </label>
        <label>
          Senha
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder="••••••••" />
        </label>
        {erro && <p className="error-text">{erro}</p>}
        <button className="primary-button" disabled={loading}>
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
        <p className="helper-text">
          Não tem conta? <Link to="/cadastro">Criar conta agora</Link>
        </p>
      </form>
    </section>
  );
}
