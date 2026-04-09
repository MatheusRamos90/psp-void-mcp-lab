# Changelog

## [1.0.0] — 2026-04-08

### Primeira versão — PSP Void MCP Lab

Laboratório backend desenvolvido para explorar a integração entre **AI Agents**, **MCP (Model Context Protocol)**, **Spring Boot 4** e uma **arquitetura em camadas** inspirada em sistemas corporativos reais.

---

### O que foi desenvolvido

#### Arquitetura geral

O projeto é composto por **5 módulos Spring Boot** independentes, orquestrados via Docker Compose, formando um pipeline que vai do cliente de IA até o banco de dados:

```
AI Clients (Cursor, Claude, ChatGPT...)
        │  MCP/SSE + X-Api-Key
        ▼
psp-void-tools-orch   :8080   ← Camada MCP/AI Tools
        │  Bearer JWT (service account)
        ▼
psp-void-orch         :8081   ← API Gateway + JWT
        │  HTTP interno
        ▼
psp-void-core         :8082   ← Regras de negócio
        │                  │
        ▼                  ▼
psp-void-postgres-db  :8083   psp-void-elasticsearch-db  :8084
(PostgreSQL / JPA)            (Elasticsearch / logs)
```

---

#### Módulos

**`psp-void-tools-orch`** — Camada MCP e AI Tools
- Implementa um **MCP Server** usando `spring-ai-starter-mcp-server-webmvc` (Spring AI 2.0.0-M4)
- Carrega dinamicamente o spec **OpenAPI** do `psp-void-orch` e converte cada endpoint em uma `Tool` chamável por agentes de IA
- Registra as tools via `McpAsyncServer.addTool()` após autenticação com service account JWT
- Expõe endpoint `/sse` (Server-Sent Events) consumido pelo Cursor IDE
- Protegido por **filtro de API Key** (`X-Api-Key`) em todos os endpoints
- Expõe também `/tools` REST para listagem, recarga e execução manual de tools

**`psp-void-orch`** — API Gateway / Orquestração
- Gateway principal com **Spring Security + JWT** (jjwt)
- Centraliza autenticação: `POST /auth/login` emite tokens Bearer
- Delega todas as operações ao `psp-void-core` via `WebClient`
- Endpoints cobertos: `Auth`, `Products`, `Prices`, `Users`, `Roles`, `Logs`
- Operações de escrita em `Users` e `Roles` exigem role `ADMIN`
- Expõe spec **OpenAPI/Swagger** usada para geração automática das MCP tools

**`psp-void-core`** — Lógica de Negócio
- Orquestra as operações entre as camadas de persistência (postgres-db e elasticsearch-db)
- Envia logs de auditoria para o Elasticsearch após cada operação
- Sem exposição direta à internet — acesso apenas via `psp-void-orch`

**`psp-void-postgres-db`** — Persistência Relacional
- **Spring Data JPA** + **Flyway** para migrações
- Schema: `products`, `product_prices`, `users`, `roles`, `user_roles`
- `DataSeederConfig`: seed automático de roles (`ADMIN`, `CUSTOMER`) e usuário de serviço (`tools@pspvoid.internal`) na primeira inicialização
- Senhas dos seeds via variáveis de ambiente (`SEED_ADMIN_PASSWORD`, `SEED_SERVICE_PASSWORD`)

**`psp-void-elasticsearch-db`** — Persistência de Logs
- **Spring Data Elasticsearch** para auditoria de operações
- `LogDocument` com campo `createdAt` como `Instant` (`epoch_millis`)
- Expõe `POST /logs` para gravação e `GET /logs` para consulta

---

#### Infraestrutura e DevOps

- **Docker Compose** orquestra os 5 serviços + PostgreSQL 16 + Elasticsearch 9
- **Multi-stage Dockerfiles** para builds otimizados (Maven + JRE slim)
- **Health checks** em todos os serviços garantindo order de inicialização correta
- **`.env`** para todos os segredos (não commitado); **`.env.example`** como template público

---

#### Segurança

| Camada | Mecanismo |
|---|---|
| Cursor → `tools-orch` | Header `X-Api-Key` validado por `ApiKeyFilter` |
| `tools-orch` → `orch` | Bearer JWT de service account (renovado no startup) |
| Qualquer cliente → `orch` | Bearer JWT emitido via `POST /auth/login` |
| Operações administrativas | Role `ADMIN` via `@PreAuthorize` |
| Segredos | Variáveis de ambiente — nenhum valor sensível em YAML ou código |

---

#### Testes e ferramentas

- **Postman Collection** (`docs/postman/`) com todos os endpoints organizados por pasta, environment com variáveis automáticas e scripts de teste para salvar JWT e IDs
- **Cursor MCP** configurado em `.cursor/mcp.json` para consumir o server SSE com API key
- Seed automático garante que o stack funciona completamente em qualquer fresh start

---

#### Principais desafios resolvidos

- Migração de **Jackson 2 → Jackson 3** exigida pelo Spring Boot 4
- Registro dinâmico de tools no MCP Server **após** o `ApplicationRunner` (timing problem)
- Configuração explícita de `spring.ai.mcp.server.type: ASYNC` para expor `McpAsyncServer` como bean
- Mapeamento de `LocalDateTime` → `Instant` no Elasticsearch para compatibilidade de índice
- Propagação de anotações Lombok em builds Docker via `lombok.config`

---

### Stack

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Spring AI | 2.0.0-M4 |
| Spring Security | 7.0.3 |
| PostgreSQL | 16 |
| Elasticsearch | 9.0.0 |
| Flyway | 11.x |
| Docker Compose | v2 |
| MCP Protocol | 2024-11-05 (SSE) |
