import { NavLink, useNavigate } from 'react-router-dom';
import { clearToken, getToken } from '../services/auth';
import { clearStoredUser, getStoredUser } from '../services/user';

export default function Navbar() {
  const navigate = useNavigate();
  const token = getToken();
  const user = getStoredUser();

  function handleLogout() {
    clearToken();
    clearStoredUser();
    navigate('/login');
  }

  return (
    <header className="nav-shell">
      <div className="nav-brand">
        <div className="brand-mark">SH</div>
        <div>
          <p className="brand-title">ServiceHub</p>
          <span className="brand-subtitle">Diaristas</span>
        </div>
      </div>
      <nav className="nav-links">
        {token ? (
          <>
            <NavLink to="/diaristas">Diaristas</NavLink>
            <NavLink to="/historico">Histórico</NavLink>
            <button className="ghost-button" onClick={handleLogout}>Sair</button>
          </>
        ) : (
          <>
            <NavLink to="/login">Entrar</NavLink>
            <NavLink to="/cadastro">Criar conta</NavLink>
          </>
        )}
      </nav>
      {token && user && (
        <div className="nav-user">
          <span>{user.role}</span>
        </div>
      )}
    </header>
  );
}
