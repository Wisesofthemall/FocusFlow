# Milestone 3 — REST API Endpoints

All endpoints are under the base `http://localhost:8080` during local development. The API is versioned informally via the `/api` prefix.

JSON throughout. Authenticated endpoints require `Authorization: Bearer <jwt>` — the JWT is issued by `/api/auth/login` or `/api/auth/register`.

## Auth

| Method | Path | Auth | Request body | Response |
|---|---|---|---|---|
| POST | `/api/auth/register` | public | `{ name, email, password }` (password ≥ 8) | `201 { token, expiresAt, user }` |
| POST | `/api/auth/login` | public | `{ email, password }` | `200 { token, expiresAt, user }` / `401 Invalid email or password` |

## Users

| Method | Path | Auth | Request body | Response |
|---|---|---|---|---|
| GET | `/api/users/me` | JWT | — | `200 { id, name, email }` |
| PUT | `/api/users/me` | JWT | `{ name?, googleRefreshToken? }` | `200 { id, name, email }` |
| DELETE | `/api/users/me` | JWT | — | `204` |

## Applications (Job applications — the primary domain object)

| Method | Path | Auth | Request body / params | Response |
|---|---|---|---|---|
| GET | `/api/applications` | JWT | — | `200 [JobApplication]` (sorted by `nextActionDate` asc) |
| GET | `/api/applications/due-between` | JWT | query `start`, `end` (ISO `LocalDateTime`) | `200 [JobApplication]` |
| GET | `/api/applications/{id}` | JWT + ownership | — | `200 JobApplication` / `403` / `404` |
| POST | `/api/applications` | JWT | `{ company, roleTitle, status, nextActionDate, priority }` | `201 JobApplication` |
| PUT | `/api/applications/{id}` | JWT + ownership | same as POST | `200 JobApplication` |
| DELETE | `/api/applications/{id}` | JWT + ownership | — | `204` |

Enums:
- `status`: `APPLIED` · `INTERVIEWING` · `OFFER` · `REJECTED` · `WITHDRAWN`
- `priority`: `LOW` · `MEDIUM` · `HIGH`

## Calendar events

| Method | Path | Auth | Request body / params | Response |
|---|---|---|---|---|
| GET | `/api/calendar-events/between` | JWT | query `start`, `end` (ISO `Instant`) | `200 [CalendarEvent]` |
| GET | `/api/calendar-events/{id}` | JWT + ownership | — | `200 CalendarEvent` |
| POST | `/api/calendar-events` | JWT | `{ title, startsAt, endsAt, source, externalId? }` | `201 CalendarEvent` |
| PUT | `/api/calendar-events/{id}` | JWT + ownership | same as POST | `200 CalendarEvent` |
| DELETE | `/api/calendar-events/{id}` | JWT + ownership | — | `204` |

Enum `source`: `GOOGLE` · `MANUAL`.

## Dashboard

| Method | Path | Auth | Params | Response |
|---|---|---|---|---|
| GET | `/api/dashboard` | JWT | `appWindowStart`, `appWindowEnd` (LocalDateTime), `eventWindowStart`, `eventWindowEnd` (Instant) | `200 { applicationsDueInRange: [...], eventsInRange: [...] }` |

## External integration — RemoteOK (public jobs feed)

| Method | Path | Auth | Params | Response |
|---|---|---|---|---|
| GET | `/api/jobs/remote` | JWT | `keyword` (default `""`), `limit` (default `10`) | `200 [RemoteOkJob]` — proxied from https://remoteok.com/api; cached 10 min; returns `[]` on upstream failure. |

## Error response format

All handled errors return:
```json
{ "message": "..." }
```
with appropriate HTTP status. Validation errors (`400`) include the first failing field name. `IllegalArgumentException` is mapped by message content to `400` / `403` (`"does not belong"`) / `404` (`"not found"`) / `409` (`"already registered"`).
