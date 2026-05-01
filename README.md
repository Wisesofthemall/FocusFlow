# ProPath

**Career preparation & professional development** — semester project built with React, Spring Boot, file-based H2, and a public jobs API.

**Team:** BlackCS — Lovinson Dieujuste, Malcolm Richards

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

| Email                | Password    |
| -------------------- | ----------- |
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

| Artifact                          | File                                                                                                   |
| --------------------------------- | ------------------------------------------------------------------------------------------------------ |
| Endpoint table (with auth column) | [docs/endpoints.md](docs/endpoints.md)                                                                 |
| ERD (Mermaid)                     | [docs/erd.md](docs/erd.md)                                                                             |
| Example request/response JSON     | [docs/api-examples.md](docs/api-examples.md)                                                           |
| AI usage log (all milestones)     | [docs/ProPath-Milestone-Document.md §5.2](docs/ProPath-Milestone-Document.md#52-ai-usage-log-appendix) |

### Security notes (known tradeoffs for this milestone)

- JWT is stored in browser `localStorage`. This is the standard student-SPA pattern and is XSS-vulnerable; a production deployment should migrate to an `httpOnly` cookie with CSRF protection.
- JWT secret is loaded from the `JWT_SECRET` environment variable with a dev-only default in [application.properties](backend/src/main/resources/application.properties). Override it for any non-local environment.
- H2 console remains enabled on `/h2-console` for grader inspection; disable in production.

## Demo Video milestone 3

[Demo Video](https://youtu.be/8M7t6lzVS9s)

## Milestone 4 — Testing, Performance & Accessibility

Milestone 4 verifies ProPath's reliability and quality through automated tests, peer usability testing, and accessibility/ethics review.

### Test summary

| Suite                   | Framework            | Tests | Result          | Command                          |
| ----------------------- | -------------------- | ----- | --------------- | -------------------------------- |
| Backend (JUnit 5)       | Spring Boot Test + MockMvc | 5     | 5 passed, 0 failed | `cd backend && ./mvnw test`      |
| Frontend (Vitest + RTL) | Vitest + React Testing Library | 10    | 10 passed, 0 failed | `cd frontend && npm run test:run` |

**Total: 15 automated tests, all passing.** Frontend coverage (V8): 62.92% statements / 46.34% branches across exercised files.

### What is tested

**Backend** ([backend/src/test/java/com/blackcs/propath/web/](backend/src/test/java/com/blackcs/propath/web/)):

- `ApplicationControllerTest` — full CRUD on `/api/applications`: create + read by id, update status + delete + verify gone (covers POST, GET, PUT, DELETE).
- `UserControllerTest` — authenticated route `/api/users/me` returns 200 with a valid JWT and 401 without one (proves `JwtAuthenticationFilter` is wired into the `SecurityFilterChain`).
- `ProPathApplicationTests` — Spring context smoke test.

Tests use an in-memory H2 database via [backend/src/test/resources/application.properties](backend/src/test/resources/application.properties) so the file-based dev DB at `backend/data/propath.mv.db` is never touched.

**Frontend** ([frontend/src/](frontend/src/)):

- `StatusBadge.test.tsx` — renders the correct human label and a status-specific class for each of the 5 `ApplicationStatus` values.
- `ProtectedRoute.test.tsx` — redirects to `/login` when no token is in `localStorage`; renders children when a token is present (exercises `AuthContext` hydration).
- `LoginPage.test.tsx` — form submit calls the auth API, stores the JWT in `localStorage`, and navigates to `/`; on API failure shows the error banner without storing a token.

### Test evidence

Screenshots captured during this milestone live in [docs/test-evidence/screenshots/](docs/test-evidence/screenshots/):

**Backend — `./mvnw test`** (`Tests run: 5, Failures: 0, Errors: 0, Skipped: 0`, `BUILD SUCCESS`):

![Backend tests passing](docs/test-evidence/screenshots/backendtest.png)

**Frontend — `npm run test:coverage`** (3 test files passed, 10 tests passed, V8 coverage table):

![Frontend tests + coverage](docs/test-evidence/screenshots/frontendtest.png)

### Reflection — How testing improved ProPath's design and usability

**Design.** Writing the JUnit tests forced a clearer mental model of how the security layer actually works. My first instinct was to mock the `SecurityContext` with `@WithMockUser`, but that would have skipped `JwtAuthenticationFilter` entirely — the very component most likely to break in production. Switching to a real `register → capture JWT → use Bearer token` flow turned the tests into honest end-to-end checks of the auth pipeline; the green `BUILD SUCCESS` below ([backendtest.png](docs/test-evidence/screenshots/backendtest.png)) is real proof that `/api/users/me` returns 200 with a valid JWT and 401 without one. A second design signal: when I wrote the test for `JobApplication`, I noticed the JSON response leaks the entire entity (with `@JsonIgnore` on `user` patching over it) instead of using a dedicated response DTO. The test still passes, but the asymmetry between request DTOs (`CreateJobApplicationRequest`) and the entity-as-response is now obvious — a refactor candidate I would not have spotted without writing field-level assertions.

**Usability.** The frontend tests on `ProtectedRoute` and `AuthContext` exposed an implicit timing assumption: `AuthContext` hydrates from `localStorage` inside a `useEffect`, so on the very first render `token` is `null` even when the user is logged in. Without `waitFor`, the tests flaked. That's the same race condition a real user would hit on a slow device, where the flash of the login redirect appears before hydration completes — something automated tests can catch but a developer using a fast laptop never would. Peer testing (3 sessions) surfaced two issues no automated test could have flagged: (1) the empty state on `/applications` doesn't tell brand-new users what to click first, and (2) the delete button on the list page has no confirmation dialog, which one peer triggered accidentally. Both are now in the punch list.

**What I would refactor next.** Extract a `JobApplicationResponse` DTO so the entity stops leaking through the API; add a confirmation modal before destructive actions; add Vitest's `coverage.include` to honestly report coverage across the whole `src/` tree. The coverage report ([frontendtest.png](docs/test-evidence/screenshots/frontendtest.png)) shows 62.92% statements / 46.34% branches — but only across files actually imported by tests. `StatusBadge.tsx` doesn't even appear in the table, which means the real coverage number is lower than it looks. That gap between "what the tool says" and "what's actually covered" is itself a testing lesson: green checkmarks don't equal comprehensive testing unless you configure the tool to be honest with you. The biggest takeaway: tests that are *easy* to write usually mean the code is well-structured; tests that fight you (mocking gymnastics, async flakiness) are pointing at design smells worth fixing rather than papering over.

## Demo Video milestone 5

[Final Demo Video](https://youtu.be/4ZjV88uo7Fk)

## Milestone 5 — Final Presentation, Reflection & Team Report

The final milestone consolidates everything into a 7-minute team presentation and the written reflection / team-report deliverables.

### Submission deliverables

| Deliverable                  | File / link                                                                                          |
| ---------------------------- | ---------------------------------------------------------------------------------------------------- |
| Final repository                 | [github.com/Wisesofthemall/FocusFlow](https://github.com/Wisesofthemall/FocusFlow)                   |
| Milestone 5 final demo video     | [youtu.be/4ZjV88uo7Fk](https://youtu.be/4ZjV88uo7Fk)                                                 |
| Team summary report              | [docs/team-summary-report.pdf](docs/team-summary-report.pdf)                                         |
| Individual reflection — Lovinson | [docs/reflection-lovinson.pdf](docs/reflection-lovinson.pdf)                                         |
| Individual reflection — Malcolm  | [docs/reflection-malcolm.pdf](docs/reflection-malcolm.pdf)                                           |
| AI usage log (appendix)          | [docs/ProPath-Milestone-Document.md §5.2](docs/ProPath-Milestone-Document.md#52-ai-usage-log-appendix) |
| Milestone 3 demo video           | [youtu.be/8M7t6lzVS9s](https://youtu.be/8M7t6lzVS9s)                                                 |

### 7-minute presentation outline

| # | Segment                                       | Time  | Notes                                                                                       |
| - | --------------------------------------------- | ----- | ------------------------------------------------------------------------------------------- |
| 1 | Problem & solution                            | ~1 min | Job-search fragmentation; ProPath as one dashboard for applications + schedule context.    |
| 2 | Live demo — login, CRUD, external API         | ~3 min | Sign in as `demo@propath.local` → dashboard cards → create application → restart-and-refresh persistence proof → trending RemoteOK jobs. |
| 3 | Architecture summary                          | ~1 min | React (Vite) → Spring Boot REST → file-based H2; JWT auth; RemoteOK proxy via `WebClient`.  |
| 4 | Major technical challenge & solution          | ~1 min | Ownership-leak refactor (see below).                                                        |
| 5 | Lessons learned & future plans                | ~1 min | Tests-as-design-feedback; punch list (confirm-delete, empty state, response DTO, httpOnly cookie). |

### Major technical challenge & solution

**Challenge.** The first cut of the application API took `userId` as a path variable: `GET /api/users/{userId}/applications/{id}`. Any authenticated user could change `{userId}` to someone else's ID and read or modify their applications. Two ownership leaks (read and delete) were live in the codebase before this was caught.

**Solution.** Refactored every per-user endpoint to derive `userId` from the authenticated principal in `SecurityContextHolder` rather than from the URL. Path variables now identify only the resource (the `/{id}` part); a server-side ownership check rejects any mismatch with 403. The fix is in commit [82a0bc5](https://github.com/Wisesofthemall/FocusFlow/commit/82a0bc5) — *"refactor(backend): derive userId from JWT principal, fix two ownership leaks"*.

**Why it matters.** This is the kind of bug that passes manual testing (every developer logs in as themselves, so the ID always matches) and only fails when a hostile user actually tries to read another user's data. Verifying the fix required driving JUnit tests through a real `register → capture JWT → use Bearer token` flow rather than `@WithMockUser`, because mocking the security context would have skipped the very `JwtAuthenticationFilter` that enforces ownership. The 5 backend tests passing in [backendtest.png](docs/test-evidence/screenshots/backendtest.png) are honest end-to-end proof, not a green checkmark on mocked-out code.

### Lessons learned

1. **Tests are design feedback, not a tax.** Hard-to-write tests pointed at design smells we'd never have spotted otherwise — the entity-as-response leak in `JobApplicationController`, and the `AuthContext` hydration race in `ProtectedRoute`. Easy tests usually mean well-structured code; tests that fight you are pointing somewhere worth fixing.
2. **Document tradeoffs honestly.** The "known tradeoffs" section (JWT in `localStorage`, file-based H2 over Postgres, H2 console enabled in dev) is more valuable than pretending we shipped a hardened production system. A reviewer can see what we knew, what we deferred, and why.
3. **AI is a planning aid, not an author.** Logging every AI session in [§5.2](docs/ProPath-Milestone-Document.md#52-ai-usage-log-appendix) — prompt, purpose, and how the output influenced the work — makes the line between "Claude helped me think" and "Claude wrote the code" inspectable. If a session isn't in the log, it didn't happen.
4. **Peer testing finds bugs automation cannot.** Three peer-testing sessions surfaced two real issues — a confusing empty state on `/applications` and an unconfirmed delete button — that no Vitest or JUnit suite would ever flag. Real users are still the most expensive and most useful test fixture we have.

### Future plans

Ordered by cost-of-not-fixing, not difficulty:

1. **Confirmation modal before destructive actions.** Cheap fix; real harm if skipped (one peer triggered an accidental delete).
2. **Empty-state CTA on `/applications`.** Onboarding fix for new users; the current empty panel doesn't tell first-timers what to click.
3. **`JobApplicationResponse` DTO.** Stop leaking the JPA entity through the API; the asymmetry between request DTOs and entity-as-response was visible the moment we wrote field-level test assertions.
4. **Migrate JWT from `localStorage` → `httpOnly` cookie + CSRF protection.** Documented as a known tradeoff; the right next step once milestone scope no longer constrains us.
5. **Honest coverage reporting.** Configure Vitest's `coverage.include` so the report covers the whole `src/` tree, not just files imported by tests. The headline 62.92% number is overstated.
6. **Google Calendar OAuth.** Originally the planned external API; deferred to RemoteOK for Milestone 3 scope. The obvious feature direction for v2.
7. **Postgres + managed secrets** for any non-local deployment; disable the H2 console outside dev.

### Acknowledgments

- **Course staff** — for the milestone cadence (Sprint 1 → Milestone 3 → Milestone 4 → Milestone 5) that forced us to ship the smallest demonstrable version of each capability rather than batch everything to the end.
- **Peer reviewers** — three peer-testing sessions surfaced two real harm-reduction issues (empty state, unconfirmed delete) that no automated test would have caught.
- **Open-source projects** — Spring Boot, React, Vite, TypeScript, Vitest, RTL, JUnit 5, H2, RemoteOK's public jobs feed.
- **AI tools (planning aid only).** ChatGPT and Claude (Opus 4.7) were used for ideation, architecture planning, and a best-practices audit, all logged in [§5.2](docs/ProPath-Milestone-Document.md#52-ai-usage-log-appendix). No code in the repository was generated by AI; every Java class, React component, config, test, and doc was authored, reviewed, and committed by BlackCS members.

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

| Path                                                                     | Description                                |
| ------------------------------------------------------------------------ | ------------------------------------------ |
| [docs/ProPath-Milestone-Document.md](docs/ProPath-Milestone-Document.md) | Original proposal and system design        |
| [docs/product-backlog.md](docs/product-backlog.md)                       | Prioritized user stories                   |
| [docs/sprint1-plan.md](docs/sprint1-plan.md)                             | Sprint 1 plan (backend foundation)         |
| [docs/wireframes/](docs/wireframes/)                                     | Wireframes (Figures 1–4)                   |
| [docs/diagrams/](docs/diagrams/)                                         | DFD and architecture SVGs                  |
| [docs/team-summary-report.pdf](docs/team-summary-report.pdf)             | Milestone 5 — team summary report          |
| [docs/reflection-lovinson.pdf](docs/reflection-lovinson.pdf)             | Milestone 5 — Lovinson's reflection        |
| [docs/reflection-malcolm.pdf](docs/reflection-malcolm.pdf)               | Milestone 5 — Malcolm's reflection         |
