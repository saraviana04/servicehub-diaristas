# ServiceHub Backend

Backend Spring Boot para o projeto ServiceHub Diaristas.

## Requisitos
- Java 21
- Maven

## Como rodar
```bash
mvn spring-boot:run
```

## H2 Console
- URL: `http://localhost:8080/h2`
- JDBC URL: `jdbc:h2:mem:servicehub`
- User: `sa`
- Senha: (em branco)

## Dados iniciais
Existe um `data.sql` com clientes, diaristas, agendamentos e avaliações.
Usuários precisam ser criados via `/auth/register`.

## Endpoints
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `GET /clientes`
- `GET /clientes/{id}`
- `POST /clientes`
- `PUT /clientes/{id}`
- `DELETE /clientes/{id}`
- `GET /diaristas`
- `GET /diaristas/{id}`
- `POST /diaristas`
- `PUT /diaristas/{id}`
- `DELETE /diaristas/{id}`
- `GET /agendamentos`
- `GET /agendamentos/{id}`
- `POST /agendamentos`
- `PUT /agendamentos/{id}`
- `DELETE /agendamentos/{id}`
- `GET /avaliacoes`
- `GET /avaliacoes/{id}`
- `POST /avaliacoes`
- `PUT /avaliacoes/{id}`
- `DELETE /avaliacoes/{id}`

## Autenticação
- Registre e faça login em `/auth/*` para obter o token JWT.
- Use o header `Authorization: Bearer <token>` nos outros endpoints.
- Para `role=CLIENTE` ou `role=DIARISTA`, envie também `nome`, `telefone` e (opcional) `bairro`.

## Paginação e ordenação
Todos os endpoints de listagem aceitam `page`, `size` e `sort`.
Exemplo: `GET /clientes?page=0&size=10&sort=nome,asc`.

## Regras de acesso
- `CLIENTE` acessa apenas seu próprio cadastro, agendamentos e avaliações.
- `DIARISTA` acessa apenas seu próprio cadastro, agendamentos e avaliações.
- `ADMIN` acessa tudo.

## Swagger (OpenAPI)
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
