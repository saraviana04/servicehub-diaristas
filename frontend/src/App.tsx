import { Routes, Route, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Diaristas from './pages/Diaristas';
import DiaristaDetalhe from './pages/DiaristaDetalhe';
import Historico from './pages/Historico';
import ProtectedRoute from './components/ProtectedRoute';
import { getToken } from './services/auth';
import { fetchMe, getStoredUser } from './services/user';

export default function App() {
  useEffect(() => {
    const token = getToken();
    const user = getStoredUser();
    if (token && !user) {
      fetchMe().catch(() => null);
    }
  }, []);

  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Navigate to="/diaristas" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<Register />} />
          <Route
            path="/diaristas"
            element={
              <ProtectedRoute>
                <Diaristas />
              </ProtectedRoute>
            }
          />
          <Route
            path="/diaristas/:id"
            element={
              <ProtectedRoute>
                <DiaristaDetalhe />
              </ProtectedRoute>
            }
          />
          <Route
            path="/historico"
            element={
              <ProtectedRoute>
                <Historico />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}
