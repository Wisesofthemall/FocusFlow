# Milestone 3 — Example Requests and Responses

Captured locally against `http://localhost:8080` with the demo user pre-seeded by [DevDataSeeder](../../backend/src/main/java/com/blackcs/propath/config/DevDataSeeder.java).

## 1. Register

```
POST /api/auth/register
Content-Type: application/json

{ "name": "Alice", "email": "alice@propath.local", "password": "Passw0rd!" }
```

```json
HTTP 201 Created

{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZUBwcm9wYXRoLmxvY2FsIiwidXNlcklkIjoyLCJuYW1lIjoiQWxpY2UiLCJpYXQiOjE3NzY1NTAwMDAsImV4cCI6MTc3NjYzNjQwMH0._zY6438g7dLqvcZBCFdXBYhTwa5CQeOwz7Re798RWI4",
  "expiresAt": 1776636400322,
  "user": { "id": 2, "name": "Alice", "email": "alice@propath.local" }
}
```

If the email is already registered:
```
HTTP 409 Conflict
{ "message": "Email already registered" }
```

## 2. Login

```
POST /api/auth/login
Content-Type: application/json

{ "email": "demo@propath.local", "password": "Passw0rd!" }
```

```json
HTTP 200 OK

{
  "token": "eyJhbGciOi…",
  "expiresAt": 1776636400322,
  "user": { "id": 1, "name": "Demo User", "email": "demo@propath.local" }
}
```

Bad credentials → `401 { "message": "Invalid email or password" }`.

## 3. Unauthenticated request is rejected

```
GET /api/applications
```

```
HTTP 401 Unauthorized
```

## 4. Authenticated list

```
GET /api/applications
Authorization: Bearer <token>
```

```json
HTTP 200 OK

[
  { "id": 2, "company": "Globex",    "roleTitle": "Backend Engineer",         "status": "INTERVIEWING", "nextActionDate": "2026-04-19T17:55:53", "priority": "MEDIUM", "createdAt": "...", "updatedAt": "..." },
  { "id": 1, "company": "Acme Corp", "roleTitle": "Software Engineer Intern", "status": "APPLIED",      "nextActionDate": "2026-04-21T17:55:53", "priority": "HIGH",   "createdAt": "...", "updatedAt": "..." },
  { "id": 3, "company": "Initech",   "roleTitle": "Full-Stack Developer",     "status": "APPLIED",      "nextActionDate": "2026-04-24T17:55:53", "priority": "LOW",    "createdAt": "...", "updatedAt": "..." }
]
```

## 5. Create application

```
POST /api/applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "company": "Stark Industries",
  "roleTitle": "Platform Engineer",
  "status": "APPLIED",
  "nextActionDate": "2026-04-25T12:00:00",
  "priority": "HIGH"
}
```

```
HTTP 201 Created
{ "id": 4, "company": "Stark Industries", ... }
```

## 6. External API — RemoteOK jobs feed

```
GET /api/jobs/remote?keyword=java&limit=3
Authorization: Bearer <token>
```

```json
HTTP 200 OK

[
  { "id": "…", "company": "Headway", "position": "Senior React Native Developer", "tags": ["react","…"], "url": "https://remoteok.com/remote-jobs/…", "date": "…", "location": null },
  { "id": "…", "company": "Eleks",   "position": "Banking Full Stack Software Developer TSCM 43657", "…": "…" },
  { "id": "…", "company": "TrueML",  "position": "Engineering Manager Data Platform", "…": "…" }
]
```

If RemoteOK is rate-limited / unreachable, the backend catches the error and returns `200 []`; the frontend dashboard card shows "Job feed unavailable right now."

## 7. Ownership enforcement

```
GET /api/applications/1
Authorization: Bearer <TOKEN_OF_ANOTHER_USER>
```

```
HTTP 403 Forbidden
{ "message": "Application does not belong to this user" }
```
