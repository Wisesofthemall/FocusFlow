# AI usage log — Sprint 1 (backend foundation)

**Team:** BlackCS · **Milestone:** Backend structure (models, repositories, services, REST)

Document **every** meaningful AI-assisted step. Add rows as you work.

| Date | Tool | Prompt summary (paraphrased) | Purpose | How output was used or modified |
| ---- | ---- | ------------------------------ | ------- | ------------------------------- |
| 2026-04-04 | Cursor / Composer | Implement Spring Boot backend per course milestone: entities User, job Application, CalendarEvent; JpaRepository interfaces; services with CRUD; REST under `/api`; H2 in-memory. | Scaffold codebase and align with [ProPath-Milestone-Document.md](ProPath-Milestone-Document.md). | **Used as starting point; reviewed and changed:** named entity `JobApplication` (Java `Application` vs Spring naming); mapped calendar columns to `starts_at` / `ends_at` after H2 reserved-word DDL failure; added `DashboardDataService`; tightened validation and error mapping. Team should **append** their own rows for any ChatGPT/Copilot/etc. use. |

## Instructions for teammates

1. When you use AI, add a **new table row** the same day when possible.
2. **Purpose** should tie to a concrete task (e.g. “derive `JobApplicationRepository` query names”).
3. In **How output was used or modified**, state what you **accepted**, **edited**, or **rejected** so graders see critical review.
