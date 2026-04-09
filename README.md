# PSP Void MCP Lab

**PSP (Project Store Planet) Void — MCP Lab**

Laboratório de arquitetura backend moderna criado para explorar a integração entre **AI Agents**, **MCP (Model Context Protocol)**, **Spring Boot**, **arquitetura em camadas**, **orquestração de serviços**, **PostgreSQL** e **Elasticsearch**.

O objetivo é entender como sistemas backend podem ser projetados para permitir que **agentes de inteligência artificial operem APIs de forma segura e estruturada**, simulando cenários reais encontrados em sistemas corporativos.

---

## Visão Geral da Arquitetura

```
AI Clients (ChatGPT, Claude, Copilot, Cursor)
            │
            ▼
  psp-void-tools-orch  :8080
  (AI Tools / MCP Layer)
            │
            ▼
  psp-void-orch  :8081
  (API Gateway / Orchestration Layer)
            │
            ▼
  psp-void-core  :8082
  (Business Logic Layer)
            │
   ┌────────┴────────┐
   ▼                 ▼
psp-void-postgres-db :8083    psp-void-elasticsearch-db :8084
(PostgreSQL Adapter)          (Elasticsearch Adapter)
   │                               │
   ▼                               ▼
PostgreSQL :5432              Elasticsearch :9200
```

Clientes tradicionais (Web / Mobile) acessam diretamente `psp-void-orch :8081`. O header **`X-Origin`** (WEB | MOBILE | MCP) é propagado em cada requisição e registrado nos logs do Elasticsearch.

---

## Módulos

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `psp-void-tools-orch` | 8080 | Expõe as Tools via MCP para AI Agents; converte o OpenAPI spec de `psp-void-orch` em tools chamáveis; autentica via service account com JWT auto-renovável |
| `psp-void-orch` | 8081 | API Gateway e ponto de entrada REST; documentado via Swagger/OpenAPI |
| `psp-void-core` | 8082 | Lógica de negócio; toda escrita gera log de auditoria no Elasticsearch |
| `psp-void-postgres-db` | 8083 | Adaptador PostgreSQL (Spring Data JPA + Flyway) |
| `psp-void-elasticsearch-db` | 8084 | Adaptador Elasticsearch para logs de auditoria |

---

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 4.0.3 | Framework base |
| Spring Web MVC | Controllers REST |
| Spring WebFlux (WebClient) | Chamadas HTTP entre serviços |
| Spring Security 7 | Autenticação JWT + RBAC |
| Spring Data JPA | Persistência relacional |
| Spring Data Elasticsearch | Persistência de logs |
| Spring AI 2.0.0-M4 (MCP Server) | Infraestrutura MCP via SSE |
| Flyway 11 | Migrations de banco de dados |
| PostgreSQL 16 | Banco relacional |
| Elasticsearch 9 | Banco de logs e busca |
| springdoc-openapi | Documentação Swagger/OpenAPI |
| jjwt | Geração e validação de JWT |
| Lombok | Redução de boilerplate |
| Docker + Docker Compose v2 | Containerização e orquestração |

---

## Segurança

| Camada | Mecanismo |
|---|---|
| AI Client → `psp-void-tools-orch` | Header `X-Api-Key` validado por `ApiKeyFilter` |
| `psp-void-tools-orch` → `psp-void-orch` | Bearer JWT de service account; renovado automaticamente pelo `TokenRefreshScheduler` |
| Qualquer cliente → `psp-void-orch` | Bearer JWT via `POST /auth/login` |
| Operações administrativas | Role `ADMIN` via `@PreAuthorize` |
| Segredos de configuração | Variáveis de ambiente — nenhum valor sensível em YAML ou código |

### Variáveis de ambiente

Copie `.env.example` para `.env` e preencha antes de subir a stack:

```bash
cp .env.example .env
```

| Variável | Descrição |
|---|---|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Credenciais do PostgreSQL |
| `JWT_SECRET` | Chave de assinatura JWT (mín. 32 chars) |
| `JWT_EXPIRATION_MS` | TTL do token em ms (padrão: `86400000` = 24 h) |
| `SERVICE_EMAIL` / `SERVICE_PASSWORD` | Conta de serviço usada pelo `psp-void-tools-orch` |
| `SEED_ADMIN_PASSWORD` / `SEED_SERVICE_PASSWORD` | Senhas criadas no seed inicial do banco |
| `MCP_API_KEY` | API Key exigida pelo `psp-void-tools-orch` |

---

## Executando o Projeto

### Pré-requisitos

- Docker Desktop instalado e em execução

### Subir a stack

```bash
# 1. Copie e preencha o .env
cp .env.example .env

# 2. Suba todos os serviços
docker compose up --build
```

O Docker Compose respeita a ordem de dependência via `depends_on + healthcheck`, garantindo que cada serviço só sobe depois que suas dependências estão prontas.

### Desenvolvimento local (sem Docker)

Suba os serviços nessa ordem:

```
1. PostgreSQL        (localhost:5432)
2. Elasticsearch     (localhost:9200)
3. psp-void-postgres-db      → mvnw spring-boot:run  (porta 8083)
4. psp-void-elasticsearch-db → mvnw spring-boot:run  (porta 8084)
5. psp-void-core             → mvnw spring-boot:run  (porta 8082)
6. psp-void-orch             → mvnw spring-boot:run  (porta 8081)
7. psp-void-tools-orch       → mvnw spring-boot:run  (porta 8080)
```

---

## URLs de Acesso

| Serviço | URL |
|---|---|
| psp-void-tools-orch | http://localhost:8080 |
| psp-void-orch | http://localhost:8081 |
| psp-void-orch Swagger UI | http://localhost:8081/swagger-ui.html |
| psp-void-core | http://localhost:8082 |
| psp-void-postgres-db | http://localhost:8083 |
| psp-void-elasticsearch-db | http://localhost:8084 |
| PostgreSQL | localhost:5432 |
| Elasticsearch | http://localhost:9200 |

---

## Conectando AI Clients ao MCP Server

O `psp-void-tools-orch` expõe o MCP Server via **SSE** em `http://localhost:8080/sse`. Todos os clientes precisam enviar o header `X-Api-Key` com o valor definido em `MCP_API_KEY` no `.env`.

### Cursor IDE

O repositório já inclui `.cursor/mcp.json` configurado. Basta garantir que o valor da `X-Api-Key` bate com o seu `.env`:

```json
{
  "mcpServers": {
    "psp-void": {
      "url": "http://localhost:8080/sse",
      "headers": {
        "X-Api-Key": "<valor de MCP_API_KEY>"
      }
    }
  }
}
```

Após subir a stack, abra **Cursor → Settings → MCP** e confirme que `psp-void` aparece com status ativo e as tools listadas.

### Claude Desktop

Claude Desktop não suporta SSE diretamente. É necessário o bridge **`mcp-remote`**:

```bash
npm install -g mcp-remote
```

Localize o arquivo de configuração do Claude Desktop:

| Sistema | Caminho |
|---|---|
| Windows | `%APPDATA%\Claude\claude_desktop_config.json` |
| macOS | `~/Library/Application Support/Claude/claude_desktop_config.json` |

Adicione a entrada do servidor MCP:

```json
{
  "mcpServers": {
    "psp-void": {
      "command": "mcp-remote",
      "args": [
        "http://localhost:8080/sse",
        "--header",
        "X-Api-Key: <valor de MCP_API_KEY>"
      ]
    }
  }
}
```

Reinicie o Claude Desktop. Um ícone de ferramentas no campo de mensagem confirma a conexão.

> **Dica:** se o Claude pedir confirmação antes de executar uma tool, clique em **Allow** para autorizar a chamada.

---

## Testando com Postman

A pasta `docs/postman/` contém uma collection e um environment prontos para importar.

| Arquivo | Descrição |
|---|---|
| `PSP-Void-API.postman_collection.json` | Collection com todos os endpoints organizados por serviço |
| `PSP-Void-Local-Dev.postman_environment.json` | Environment com variáveis pré-configuradas |

**Como importar:** Postman → **Import** → selecione os dois arquivos → ative o environment **PSP Void - Local Dev**.

Fluxo sugerido: **Auth/Login → Create Product → Create Price → Create User → Create Role → Assign Role → Logs**

---

## Futuras Melhorias

- RBAC aplicado nas Tools (restrição por role dentro do MCP)
- Observabilidade com OpenTelemetry + Jaeger
- Integração com Kafka para eventos assíncronos
- Rate limiting para Tools
- Dashboard de logs com Kibana
- Cache com Redis
- Testes de integração com Testcontainers
- Suporte ao transporte Streamable HTTP (MCP protocol `2025-11-25`)

---

## Motivação

Este projeto foi criado para estudar **como sistemas backend modernos podem ser preparados para interagir com AI Agents**, permitindo que inteligência artificial execute operações em sistemas corporativos de forma segura, auditável e escalável.
