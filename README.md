# ProPath / FocusFlow

**Career preparation & professional development** — semester project built with React, Spring Boot, file-based H2, and a public jobs API.

**Team:** BlackCS — Lovinson Dieujuste, Malcolm Richards, Terrance Holloway

**Repository:** [github.com/Wisesofthemall/FocusFlow](https://github.com/Wisesofthemall/FocusFlow)

## Milestone 3 — Frontend · Database · External API · Authentication

Milestone 3 layers four capabilities on top of the Sprint-1 backend:
1. **React frontend** (Vite + TypeScript) with five pages — Login, Register, Dashboard, Applications List (GET), and New Application (POST).
2. **Persistent database** — file-based H2 at `backend/data/propath.mv.db`, survives restarts.
3. **Spring Security + JWT** — stateless auth, Bearer tokens, ownership enforcement on all per-user resources.
4. **External public API** — RemoteOK jobs feed, proxied through the backend, surfaced on the Dashboard.

### Quick start

Two terminals:

```bash
# Terminal 1 — backend (seeds demo user on first boot)
cd backend && ./mvnw spring-boot:run

# Terminal 2 — frontend
cd frontend && npm install && npm run dev
```

Then open **http://localhost:5173**. Log in with the pre-seeded demo account:

| Email | Password |
|---|---|
| `demo@propath.local` | `Passw0rd!` |

### End-to-end demo flow
1. Open `/` → redirected to `/login` (auth guard working).
2. Sign in with demo account → Dashboard shows three cards (apps due this week, upcoming events, trending remote jobs from RemoteOK).
3. Navigate to **Applications** → see the 3 seeded applications sorted by urgency.
4. Click **+ New application** → fill form → submit → redirected back to the list with the new entry visible.
5. **Restart the backend** (Ctrl+C + re-run) → refresh the page → your new application is still there (persistence confirmed).
6. Sign out → protected pages redirect back to `/login`.

### Architecture

```
┌─────────────────┐   HTTPS/CORS   ┌───────────────────────┐   Bearer JWT   ┌──────────────────────┐
│ React (Vite)    │ ─────────────▶ │ Spring Boot REST API  │ ─────────────▶ │ Service + JPA layer  │
│  :5173          │                │  :8080                 │                │ (User / JobApp /     │
│  - AuthContext  │                │  - SecurityFilterChain │                │  CalendarEvent)      │
│  - Axios w/     │                │  - JwtAuthFilter       │                └─────────┬────────────┘
│    interceptor  │                │  - Controllers (/api/…)│                          │
└─────────────────┘                └────────┬───────────────┘                          ▼
                                            │                                  ┌────────────────┐
                                            │  WebClient (reactive)             │ File-based H2  │
                                            ▼                                  │ backend/data/  │
                                    ┌─────────────────┐                        └────────────────┘
                                    │ RemoteOK API    │
                                    │ remoteok.com    │
                                    └─────────────────┘
```

### Milestone 3 documentation

| Artifact | File |
|---|---|
| Endpoint table (with auth column) | [docs/endpoints.md](docs/endpoints.md) |
| ERD (Mermaid) | [docs/erd.md](docs/erd.md) |
| Example request/response JSON | [docs/api-examples.md](docs/api-examples.md) |
| AI usage log (all milestones) | [docs/ProPath-Milestone-Document.md §5.2](docs/ProPath-Milestone-Document.md#52-ai-usage-log-appendix) |

### Security notes (known tradeoffs for this milestone)
- JWT is stored in browser `localStorage`. This is the standard student-SPA pattern and is XSS-vulnerable; a production deployment should migrate to an `httpOnly` cookie with CSRF protection.
- JWT secret is loaded from the `JWT_SECRET` environment variable with a dev-only default in [application.properties](backend/src/main/resources/application.properties). Override it for any non-local environment.
- H2 console remains enabled on `/h2-console` for grader inspection; disable in production.

## Backend (Spring Boot 3.3.5, Java 17)

The API lives in [`backend/`](backend/).

```bash
cd backend
./mvnw test              # run tests
./mvnw spring-boot:run   # start on :8080
```

- H2 console (dev): `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:file:./data/propath`, user `sa`, empty password.
- Reset the DB: stop the server and `rm -rf backend/data/`.
- Register/login endpoints are `POST /api/auth/register` and `POST /api/auth/login`; all other `/api/**` endpoints require `Authorization: Bearer <token>`.

## Frontend (React 19, Vite 8, TypeScript)

Lives in [`frontend/`](frontend/).

```bash
cd frontend
npm install
npm run dev        # dev server on :5173
npm run build      # production bundle into dist/
```

Environment (`.env.development`): `VITE_API_BASE_URL=http://localhost:8080`.

## Background docs

| Path | Description |
|---|---|
| [docs/ProPath-Milestone-Document.md](docs/ProPath-Milestone-Document.md) | Original proposal and system design |
| [docs/product-backlog.md](docs/product-backlog.md) | Prioritized user stories |
| [docs/sprint1-plan.md](docs/sprint1-plan.md) | Sprint 1 plan (backend foundation) |
| [docs/wireframes/](docs/wireframes/) | Wireframes (Figures 1–4) |
| [docs/diagrams/](docs/diagrams/) | DFD and architecture SVGs |
