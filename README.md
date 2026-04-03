# ServiceHub Diaristas

Plataforma full-stack para conectar clientes e diaristas, com autenticação JWT, agendamentos e avaliações.

## Stack
- Backend: Java 21, Spring Boot, Spring Security, JPA, H2
- Frontend: React, TypeScript, Vite, Axios

## Estrutura
- `backend/`: API Spring Boot
- `frontend/`: App React

## Requisitos
- Java 21
- Maven
- Node.js 18+
- npm

## Como rodar

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Crie um `.env` em `frontend/`:
```
VITE_API_URL=http://localhost:8080
```

## Dados iniciais
O backend carrega dados de exemplo via `backend/src/main/resources/data.sql`.
O H2 é em memória, então os dados são reiniciados a cada execução.

## Autenticação
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

Use o token JWT no header:
```
Authorization: Bearer <token>
```

## Endpoints principais
- `clientes`: `GET /clientes`, `GET /clientes/{id}`, `POST`, `PUT`, `DELETE`
- `diaristas`: `GET /diaristas`, `GET /diaristas/{id}` (perfil com métricas), `POST`, `PUT`, `DELETE`
- `agendamentos`: `GET /agendamentos`, `GET /agendamentos/{id}`, `POST`, `PUT`, `DELETE`
- `avaliacoes`: `GET /avaliacoes`, `GET /avaliacoes/{id}`, `POST`, `PUT`, `DELETE`

## Avaliações
- `POST /avaliacoes` requer `clienteId`, `diaristaId`, `agendamentoId`, `nota` e (opcional) `comentario`.
- Só é permitido avaliar se o agendamento estiver com status `CONCLUIDO`.
- Um agendamento pode receber apenas uma avaliação.

## Regras de acesso
- `CLIENTE` acessa apenas seus dados e seus agendamentos/avaliacoes
- `DIARISTA` acessa apenas seus dados e seus agendamentos/avaliacoes
- `ADMIN` acessa tudo

## Swagger
- `http://localhost:8080/swagger-ui/index.html`
