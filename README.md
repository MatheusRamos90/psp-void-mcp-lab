# PSP Void MCP Lab

**PSP (Project Store Planet) Void — MCP Lab**

Laboratório de arquitetura backend moderna criado para explorar a integração entre **AI Agents**, **MCP (Model Context Protocol)**, **Spring Boot**, **arquitetura em camadas**, **orquestração de serviços**, **PostgreSQL** e **Elasticsearch**.

O objetivo é entender como sistemas backend podem ser projetados para permitir que **agentes de inteligência artificial operem APIs de forma segura e estruturada**, simulando cenários reais encontrados em sistemas corporativos.

---

## Objetivo do Projeto

Este laboratório foi criado para estudar e experimentar os seguintes conceitos:

- Integração de **AI Agents** com sistemas backend
- Uso do **Model Context Protocol (MCP)**
- Transformação automática de **APIs Swagger/OpenAPI em Tools**
- Arquitetura com **camada de orquestração**
- Separação entre **domínio, persistência e infraestrutura**
- Uso de **Elasticsearch para logs e observabilidade**
- Construção de um backend **AI-ready**

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

Clientes tradicionais acessam diretamente a camada de orquestração:

```
Web / Mobile Client
        │
        ▼
  psp-void-orch  :8081
```

O header **`X-Origin`** (WEB | MOBILE | MCP) é propagado em cada requisição e registrado nos logs do Elasticsearch.

---

## Estrutura do Repositório

```
psp-void-mcp-lab/
│
├── psp-void-tools-orch/          # AI Tools / MCP Layer
│   └── psp-void-tools-orch/
│       ├── src/main/java/.../
│       │   ├── tools/            # Tool model
│       │   ├── swagger/          # SwaggerClient, SwaggerToolLoader
│       │   ├── registry/         # ToolRegistry
│       │   ├── executor/         # ToolExecutor
│       │   ├── client/           # OrchApiClient (WebClient)
│       │   ├── config/           # WebClientConfig, ToolsConfig
│       │   └── controller/       # ToolController
│       └── Dockerfile
│
├── psp-void-orch/                # API Gateway / Orchestration Layer
│   └── psp-void-orch/
│       ├── src/main/java/.../
│       │   ├── dto/              # ProductRequest/Response, PriceRequest/Response,
│       │   │                     # UserRequest/Response, RoleRequest/Response,
│       │   │                     # UserRoleRequest/Response, LogResponse
│       │   ├── client/           # CoreApiClient (WebClient)
│       │   ├── controller/       # ProductController, ProductPriceController,
│       │   │                     # UserController, RoleController, LogController
│       │   └── config/           # WebClientConfig, OpenApiConfig
│       └── Dockerfile
│
├── psp-void-core/                # Business Logic Layer
│   └── psp-void-core/
│       ├── src/main/java/.../
│       │   ├── dto/              # ProductRequest/Response, PriceRequest/Response,
│       │   │                     # UserRequest/Response, RoleRequest/Response,
│       │   │                     # UserRoleRequest/Response, LogResponse
│       │   ├── enums/            # Origin (WEB, MOBILE, MCP)
│       │   ├── client/           # PostgresDbClient, ElasticsearchDbClient (WebClient)
│       │   ├── service/          # ProductService, ProductPriceService,
│       │   │                     # UserService, RoleService, LogService
│       │   ├── controller/       # ProductController, ProductPriceController,
│       │   │                     # UserController, RoleController, LogController
│       │   └── config/           # WebClientConfig
│       └── Dockerfile
│
├── psp-void-postgres-db/         # PostgreSQL Persistence Adapter
│   └── psp-void-postgres-db/
│       ├── src/main/java/.../
│       │   ├── entity/           # ProductEntity, ProductPriceEntity, UserEntity,
│       │   │                     # RoleEntity, UserRoleEntity
│       │   ├── repository/       # ProductRepository, ProductPriceRepository,
│       │   │                     # UserRepository, RoleRepository, UserRoleRepository
│       │   ├── dto/              # (mesmos shapes das entities)
│       │   ├── service/          # ProductService, ProductPriceService, UserService,
│       │   │                     # RoleService, UserRoleService
│       │   └── controller/       # ProductController, ProductPriceController,
│       │                         # UserController, RoleController, UserRoleController
│       ├── src/main/resources/
│       │   ├── db/migration/     # V1__create_tables.sql
│       │   └── application.yaml
│       └── Dockerfile
│
├── psp-void-elasticsearch-db/    # Elasticsearch Logging Adapter
│   └── psp-void-elasticsearch-db/
│       ├── src/main/java/.../
│       │   ├── document/         # LogDocument (@Document indexName="logs")
│       │   ├── repository/       # LogRepository
│       │   ├── dto/              # LogRequest, LogResponse
│       │   ├── service/          # LogService
│       │   └── controller/       # LogController
│       └── Dockerfile
│
├── docs/
│   └── prompts/
│       └── arch-system-prompt.md
│
├── docker-compose.yml
└── README.md
```

---

## Módulos do Sistema

### psp-void-tools-orch — porta 8080

Camada responsável por expor **Tools utilizadas por AI Agents** via MCP.

**Responsabilidades:**
- Buscar o OpenAPI spec de `psp-void-orch` no startup
- Converter cada endpoint em um `Tool` (nome, método, path, descrição)
- Manter um `ToolRegistry` em memória
- Executar tools via `ToolExecutor` → `OrchApiClient`

**Componentes:**

| Classe | Responsabilidade |
|---|---|
| `SwaggerClient` | Busca o JSON do OpenAPI de psp-void-orch |
| `SwaggerToolLoader` | Parseia os paths/métodos e cria objetos `Tool` |
| `ToolRegistry` | Armazena tools em memória (Map por nome) |
| `ToolExecutor` | Resolve tool pelo nome e delega ao OrchApiClient |
| `OrchApiClient` | Faz a chamada HTTP real para psp-void-orch |

**Endpoints:**

```
GET  /tools                          → lista todas as tools disponíveis
POST /tools/reload                   → recarrega tools do OpenAPI
POST /tools/{toolName}/execute       → executa uma tool pelo nome
```

**Convenção de nomes das tools:**

```
POST /products          →  post_products
GET  /products/{id}     →  get_products_id
DELETE /products/{id}   →  delete_products_id
PUT  /roles/{id}        →  put_roles_id
POST /roles/assign      →  post_roles_assign
```

---

### psp-void-orch — porta 8081

Camada de **API Gateway e orquestração**. Ponto de entrada para Web, Mobile e AI Agents.

**Responsabilidades:**
- Expor endpoints REST documentados via Swagger/OpenAPI
- Propagar o header `X-Origin` para o core
- Delegar toda lógica de negócio para `psp-void-core`

**Endpoints disponíveis:**

```
# Products
POST   /products
GET    /products
GET    /products/{id}
PUT    /products/{id}
DELETE /products/{id}

# Prices
POST   /prices
GET    /prices
GET    /prices/{id}
PUT    /prices/{id}
DELETE /prices/{id}

# Users
POST   /users
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}

# Roles
POST   /roles
GET    /roles
GET    /roles/{id}
PUT    /roles/{id}
DELETE /roles/{id}
POST   /roles/assign           → atribuir role a um usuário
GET    /roles/by-user/{userId} → listar roles de um usuário

# Logs
GET    /logs

# Swagger
GET    /swagger-ui.html
GET    /v3/api-docs
```

---

### psp-void-core — porta 8082

Camada de **lógica de negócio**. Coordena as operações entre os adaptadores de persistência.

**Responsabilidades:**
- Aplicar regras de domínio
- Chamar `psp-void-postgres-db` para persistência transacional
- Chamar `psp-void-elasticsearch-db` para registrar logs de auditoria
- Toda operação de criação, atualização e deleção **gera um log** automaticamente

**Regra de logging:**

| Operação | Log gerado |
|---|---|
| Criar produto | `product created: {id}` |
| Atualizar produto | `product updated: {id}` |
| Deletar produto | `product deleted: {id}` |
| Criar preço | `price created: {id}` |
| Atualizar preço | `price updated: {id}` |
| Deletar preço | `price deleted: {id}` |
| Criar usuário | `user created: {id}` |
| Atualizar usuário | `user updated: {id}` |
| Deletar usuário | `user deleted: {id}` |
| Criar role | `role created: {name}` |
| Atribuir role | `role assigned: user={id} role={id}` |
| Revogar role | `role revoked: user={id} role={id}` |

---

### psp-void-postgres-db — porta 8083

Adaptador de persistência para **PostgreSQL**. Expõe uma API REST interna consumida apenas por `psp-void-core`.

**Tecnologias:** Spring Data JPA + Flyway

---

### psp-void-elasticsearch-db — porta 8084

Adaptador de persistência para **Elasticsearch**. Indexa e consulta logs de auditoria.

**Tecnologias:** Spring Data Elasticsearch

---

## Modelo de Domínio

### Product

```
id:          UUID
name:        String
description: String
status:      Boolean
createdAt:   LocalDateTime
updatedAt:   LocalDateTime
```

### ProductPrice

```
id:              UUID
productId:       UUID  (FK → products)
value:           BigDecimal
discountPercent: BigDecimal
createdAt:       LocalDateTime
updatedAt:       LocalDateTime
```

### User

```
id:        UUID
name:      String
email:     String  (unique)
status:    Boolean
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### Role

```
id:        UUID
name:      String  (unique)
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### UserRole

```
id:        UUID
userId:    UUID  (FK → users)
roleId:    UUID  (FK → roles)
createdAt: LocalDateTime
updatedAt: LocalDateTime
           UNIQUE (userId, roleId)
```

### Log _(Elasticsearch)_

```
id:        String  (UUID)
trace:     String
origin:    WEB | MOBILE | MCP
createdAt: LocalDateTime
```

---

## Banco de Dados

### PostgreSQL — Migração Flyway (V1)

```sql
products       → id, name, description, status, created_at, updated_at
product_prices → id, product_id, value, discount_percent, created_at, updated_at
users          → id, name, email, status, created_at, updated_at
roles          → id, name, created_at, updated_at
user_roles     → id, user_id, role_id, created_at, updated_at  [UNIQUE user_id+role_id]
```

### Elasticsearch — Índice

```
logs → id, trace, origin, createdAt
```

---

## Fluxo do Header X-Origin

O campo `origin` identifica a fonte da operação nos logs. Ele é enviado como header HTTP e propagado por todas as camadas.

```
AI Agent  →  X-Origin: MCP   →  tools-orch → orch → core → log(origin=MCP)
Web App   →  X-Origin: WEB   →  orch → core → log(origin=WEB)
Mobile    →  X-Origin: MOBILE →  orch → core → log(origin=MOBILE)
```

---

## Integração com AI Agents

Exemplo de operação executada por um agente:

```
Agente: "Crie um produto chamado Galaxy Shirt"
         │
         ▼
Tool resolvida: post_products
         │
         ▼
POST /tools/post_products/execute
{ "name": "Galaxy Shirt", "description": "...", "status": true }
         │
         ▼
psp-void-tools-orch  →  psp-void-orch  →  psp-void-core
         │
         ├──▶  psp-void-postgres-db  (persiste produto)
         └──▶  psp-void-elasticsearch-db  (registra log com origin=MCP)
```

---

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 4 | Framework base |
| Spring Web MVC | Controllers REST |
| Spring WebFlux (WebClient) | Chamadas HTTP entre serviços |
| Spring Data JPA | Persistência relacional |
| Spring Data Elasticsearch | Persistência de logs |
| Spring AI (MCP Server/Client) | Infraestrutura MCP |
| Flyway | Migrations de banco de dados |
| PostgreSQL 16 | Banco relacional |
| Elasticsearch 8 | Banco de logs e busca |
| springdoc-openapi | Documentação Swagger/OpenAPI |
| Lombok | Redução de boilerplate |
| Docker + Docker Compose | Containerização |

---

## Executando o Projeto

### Pré-requisitos

- Docker Desktop instalado e em execução

### Subir toda a stack

```bash
docker compose up --build
```

O Docker Compose sobe os serviços na ordem correta via `depends_on + healthcheck`:

```
postgres (5432) ──────────────────────────────────────────┐
elasticsearch (9200) ──────────────────────────────────────┤
                                                           ▼
                          psp-void-postgres-db (8083) ─────┐
                          psp-void-elasticsearch-db (8084) ─┤
                                                            ▼
                                    psp-void-core (8082) ──┐
                                                           ▼
                                  psp-void-orch (8081) ────┐
                                                           ▼
                            psp-void-tools-orch (8080)
```

### Desenvolvimento local (sem Docker)

Inicie os serviços na seguinte ordem:

```
1. PostgreSQL   (localhost:5432)
2. Elasticsearch (localhost:9200)
3. psp-void-postgres-db     → mvnw spring-boot:run  (porta 8083)
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
| psp-void-orch OpenAPI JSON | http://localhost:8081/v3/api-docs |
| psp-void-core | http://localhost:8082 |
| psp-void-postgres-db | http://localhost:8083 |
| psp-void-elasticsearch-db | http://localhost:8084 |
| PostgreSQL | localhost:5432 |
| Elasticsearch | http://localhost:9200 |

---

## Futuras Melhorias

- Autenticação com OAuth2 / JWT
- RBAC aplicado nas Tools (restrição por role)
- Observabilidade com OpenTelemetry + Jaeger
- Integração com Kafka para eventos assíncronos
- Rate limiting para Tools
- Dashboard de logs com Kibana
- Cache com Redis
- Testes de integração com Testcontainers

---

## Motivação

Este projeto foi criado para estudar **como sistemas backend modernos podem ser preparados para interagir com AI Agents**, permitindo que inteligência artificial execute operações em sistemas corporativos de forma segura, auditável e escalável.

A arquitetura demonstra como separar claramente:

- **Camada de ferramentas de IA** — exposição via MCP
- **Camada de orquestração** — gateway público documentado
- **Domínio de negócio** — regras, validações, auditoria
- **Persistência relacional** — PostgreSQL + JPA + Flyway
- **Observabilidade** — logs estruturados no Elasticsearch
