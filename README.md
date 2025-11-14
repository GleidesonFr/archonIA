# Archon IA

## Visão Geral

_ArchonIA_ é um chatbot inteligente desenvolvido ainda como um protótipo.
A aplicação permite interação simultânea de vários usuários, mantendo sessões independentes e histórico isolado para cada um.

O sistema combina _Angular, Spring Boot, Spring Cloud Gateway, Python (FastAPI)_ e _LangChain_ para oferecer um fluxo robusto de comunicação com LLMs.
Toda a solução é implantada em uma instância AWS EC2, com endpoint público acessível.

[![Tools](https://skillicons.dev/icons?i=angular,java,spring,python,fastapi,redis,docker,aws)](https://skillicons.dev)

### Principais Funcionalidades

* Chat multiusuário com sessões separadas por ID único;
* Arquitetura distribuída (Frontend → Gateway → Backend → IA)
* Guardrails e rate limiting via API Gateway
* Respostas geradas por LLM usando LangChain
* Histórico de conversas por sessão (in-memory)
* Deploy completo na AWS EC2
* Documentação e endpoints públicos

## Tecnologias Principais

* **Front-end:** Angular
* **Back-end / Orquestração:** Spring Boot
* **Serviço de IA:** FastAPI + LangChain
* **Armazenamento de Sessão:** Redis
* **Contêineres:** Docker / docker-compose
* **Infraestrutura:** AWS EC2

## Arquitetura

````mermaid
---
config:
      theme: redux
---
flowchart LR
    subgraph Frontend [Angular Frontend]
        UI[Interface Web]
    end

    subgraph Gateway  [Spring Cloud Gateway]
        GL[GuardRails Filter]
        RL[Rate Limiter]
        RT[Routing]
    end

    subgraph Backend[Spring Boot Backend]
        SS[SessionService]
        HC[HistoryController]
        AIProxy[AI Proxy Controller]
    end

    subgraph PythonLLM [Python + LangChain]
        Router[FastAPI Router]
        LLM[LangChain LLM]
        Summarizer[Summarizer]
    end

    UI --> |HTTP Request| Gateway
    Gateway --> Backend
    Backend -->|Requisição de IA|PythonLLM
    PythonLLM --> |Resposta processada| Backend
    Backend --> Gateway
    Gateway --> |HTTP Response| UI
````

## Endpoints & Protocolos

[Em andamento...]

## Gerenciamento de Sessões (Redis)

[Em andamento...]

## Serviço de IA (FastAPI + LangChain)

[Em andamento...]

## Docker & docker-compose

* **Serviços:** redis, spring, python.

### Exemplo de desenvolvimento e deploy

[Em andamento...]

## Deploy na AWS EC2

[Em andamento...]

## Segurança Básica

* Proteção das chaves de _API_.
* CORS limitado.
* Rate limiting e proteção contra abuso.

## Testes e Validação

[Em andamento...]

## Melhoria e Próximos Passos

* Persistência em DB relacional.
* Resumos automáticos de sessões.
* Dashboard de métricas / latência.

## Estrutura do Repositório

```bash
|- backend/
|- bot/
|- docs/
|- frontend/
README.md
```
**OBS.:** Sujeito a alteração.

## Referências e Créditos

[Em andamento...]