# Sprint 1 — backend foundation (1 week)

**Team:** BlackCS (Lovinson Dieujuste, Malcolm Richards, Terrance Holloway)  
**Goal:** Implement Spring Boot domain models, Spring Data repository interfaces, service-layer operations, and REST endpoints (no production database; in-memory H2 for local development).  
**Sprint dates (example):** align with your course calendar.

## Selected backlog items (this sprint)

| # | Backlog item (summary) | Primary owner |
| - | ---------------------- | ------------- |
| 1 | **User account model & user API** — register, read, update profile, delete; `UserRepository`, `UserService`, `/api/users` | Terrance Holloway |
| 2 | **Job application model & application API** — CRUD-style flows; `JobApplicationRepository`, `ApplicationService`, `/api/.../applications` | Malcolm Richards |
| 3 | **Calendar event model & events API** — create/read/update/delete; `CalendarEventRepository`, `CalendarEventService`, `/api/.../calendar-events` | Terrance Holloway |
| 4 | **Dashboard aggregation stub** — `DashboardDataService` + `/api/users/{id}/dashboard` combining applications due in a window and events in a window | Malcolm Richards |
| 5 | **Documentation & API contract notes** — product backlog, this sprint plan, README backend runbook, review PRs for consistency with milestone entities | Lovinson Dieujuste |

Every member owns **at least one** feature above. Split subtasks (e.g. tests, exception handling polish) in your standups.

## Definition of done (Sprint 1)

- [ ] `backend` builds with Maven; `./mvnw test` passes (from `backend/`).
- [ ] App starts with H2; core REST paths documented in [README.md](../README.md).
- [ ] Three related JPA entities (`User`, `JobApplication`, `CalendarEvent`) match [ProPath-Milestone-Document.md](ProPath-Milestone-Document.md) §3.3.
- [ ] Repository interfaces and services implement create / read / update or delete as required by the milestone rubric.

## GitHub collaboration (milestone requirement)

**Requirement:** each teammate completes **at least three meaningful commits** with **clear messages**; work spread across the team.

**Practices:**

1. Use **feature branches** (e.g. `feat/user-api`, `feat/applications`) and **pull requests** into `main`.
2. Prefer **small, incremental commits** (entity → repository → service → controller → docs).
3. **Commit message examples:** `feat(backend): add JobApplication entity and repository`, `fix(backend): reserve H2 column names for calendar events`, `docs: add Sprint 1 plan and backlog`.

**Team checklist (self-verify before submit):**

| Member | ≥3 meaningful commits? | Branch/PR link (optional) |
| ------ | ---------------------- | --------------------------- |
| Lovinson Dieujuste | ☐ | |
| Malcolm Richards | ☐ | |
| Terrance Holloway | ☐ | |

## AI usage

Log prompts and outcomes in [ai-usage-log-sprint1.md](ai-usage-log-sprint1.md) (required for the milestone).
