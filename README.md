# ProPath

**Career preparation & professional development** — semester project (React, Spring Boot, relational DB, Google Calendar API).

**Team:** BlackCS — Lovinson Dieujuste, Malcolm Richards, Terrance Holloway

## Milestone document (proposal & system design)

Milestone materials live in [`docs/`](docs/):

| Path                                                                 | Description                    |
| -------------------------------------------------------------------- | ------------------------------ |
| [docs/ProPath-Milestone-Document.md](docs/ProPath-Milestone-Document.md) | Full milestone text (Markdown) |
| [docs/ProPath-Milestone-Document.pdf](docs/ProPath-Milestone-Document.pdf) | Milestone PDF export           |
| [docs/wireframes/](docs/wireframes/)                                 | SVG wireframes (Figures 1–4)   |
| [docs/diagrams/](docs/diagrams/)                                     | DFD and architecture SVGs      |

**Repository:** [github.com/Wisesofthemall/FocusFlow](https://github.com/Wisesofthemall/FocusFlow)

## Backend (Spring Boot)

The API lives in [`backend/`](backend/). It uses **Java 17**, **Spring Boot 3.3**, **Spring Data JPA**, and an **in-memory H2** database for local development (no MySQL/PostgreSQL configuration in this milestone).

From the `backend/` directory:

```bash
./mvnw test
./mvnw spring-boot:run
```

- Default URL: `http://localhost:8080`
- Example: `POST /api/users` (JSON body: `name`, `email`, `password`) to register a user.
- H2 console (dev): `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:propath`, user `sa`, empty password — see [`backend/src/main/resources/application.properties`](backend/src/main/resources/application.properties)).

**Sprint / backlog:** [docs/product-backlog.md](docs/product-backlog.md), [docs/sprint1-plan.md](docs/sprint1-plan.md). **AI log (Sprint 1):** [docs/ai-usage-log-sprint1.md](docs/ai-usage-log-sprint1.md).
