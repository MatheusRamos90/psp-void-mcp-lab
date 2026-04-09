You are a senior software architect specialized in Java, Spring Boot, distributed systems, and AI-integrated backends.

I am building a learning laboratory project called **psp-void-mcp-lab** to study MCP (Model Context Protocol), AI agents, orchestration layers, and modern backend architectures.

The repository already contains the following Spring Boot projects created with Spring Initializr:

* psp-void-tools-orch
* psp-void-orch
* psp-void-core
* psp-void-postgres-db
* psp-void-elasticsearch-db

Your task is to implement the architecture described below.

IMPORTANT:
This is a **learning lab**, so the code should prioritize **clarity, good architecture, and separation of concerns**, not excessive abstraction.

Use **Spring Boot 3+, Java 21, Maven**.

---

# High Level Architecture

The system simulates an e-commerce style backend with AI agent integration.

Flow:

AI Client (ChatGPT, Claude, Copilot, Cursor)
↓
psp-void-tools-orch  (AI Tools Layer / MCP Layer)
↓
psp-void-orch        (API Gateway / Orchestration Layer)
↓
psp-void-core        (Business Logic Layer)
↓
Persistence Layers
├── psp-void-postgres-db
└── psp-void-elasticsearch-db

Web/Mobile clients also call **psp-void-orch**.

---

# Responsibilities per project

## 1️⃣ psp-void-tools-orch

Purpose:
Acts as the **AI tools gateway**.

Responsibilities:

* expose "tools" that AI agents can call
* dynamically load API endpoints from the Swagger/OpenAPI of psp-void-orch
* convert endpoints into executable tools
* forward requests to psp-void-orch

This project should include:

packages:

tools
swagger
registry
executor
client
config

Main components:

SwaggerClient
SwaggerToolLoader
ToolRegistry
ToolExecutor
OrchApiClient

Behavior:

1. On startup, fetch OpenAPI spec from:
   http://localhost:8081/v3/api-docs

2. Parse paths and HTTP methods.

3. Convert them into internal Tool definitions.

Example generated tool:

Tool:
name = "post_products"
method = POST
path = "/products"
description = "Create product"

4. When a tool is executed:

   * call the corresponding endpoint in psp-void-orch using WebClient.

---

## 2️⃣ psp-void-orch

Purpose:
Acts as the **API Gateway / Orchestration layer**.

Responsibilities:

* expose REST endpoints
* document endpoints using Swagger/OpenAPI
* validate requests
* call the core service

Example endpoints:

POST /products
GET /products
GET /products/{id}
PUT /products/{id}
DELETE /products/{id}

POST /prices
GET /prices
GET /prices/{id}

GET /logs

Packages:

controller
service
client
dto
config

---

## 3️⃣ psp-void-core

Purpose:
Business logic layer.

Responsibilities:

* domain rules
* persistence orchestration
* logging of operations

Entities:

Product
ProductPrice
User
Log

Example Product:

UUID id
String name
String description
Boolean status
LocalDateTime createdAt
LocalDateTime updatedAt

Example Log:

UUID id
String trace
EnumOrigin origin (WEB, MOBILE, MCP)
LocalDateTime createdAt

Every create/update/delete operation must generate a log entry.

---

## 4️⃣ psp-void-postgres-db

Purpose:
Persistence adapter for PostgreSQL.

Responsibilities:

* JPA entities
* repositories
* Flyway migrations

Tables:

products
product_prices
users

---

## 5️⃣ psp-void-elasticsearch-db

Purpose:
Logging and search persistence.

Responsibilities:

* index logs
* allow querying logs

Index:

logs

Document structure:

id
trace
origin
createdAt

Use Spring Data Elasticsearch.

---

# Logging rule

Every important action must generate a log entry stored in Elasticsearch.

Example events:

product created
product updated
product deleted
price created
price updated
price deleted

Origin field must indicate:

WEB
MOBILE
MCP

---

# Technologies to use

Java 21
Spring Boot 3
Spring Web
Spring Data JPA
Spring Data Elasticsearch
Flyway
PostgreSQL
WebClient
OpenAPI / Swagger (springdoc-openapi)

---

# What I want you to do

1. Generate the folder/package structure for each project.
2. Create entities and DTOs.
3. Create REST controllers in psp-void-orch.
4. Implement services and repositories.
5. Implement the SwaggerToolLoader that reads OpenAPI and converts endpoints to tools.
6. Implement ToolExecutor that calls the orchestration API.
7. Implement logging in Elasticsearch.
8. Ensure everything compiles.

Start by implementing **psp-void-core**, then **psp-void-orch**, then **psp-void-tools-orch**.
