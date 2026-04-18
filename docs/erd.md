# Milestone 3 — Entity–Relationship Diagram

FocusFlow persists three entities in a file-based H2 database (`jdbc:h2:file:./data/propath`). Schema is derived from JPA annotations via Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

```mermaid
erDiagram
    USERS ||--o{ APPLICATIONS : "owns"
    USERS ||--o{ CALENDAR_EVENTS : "owns"

    USERS {
        BIGINT id PK
        VARCHAR name
        VARCHAR email "UNIQUE"
        VARCHAR password_hash
        VARCHAR google_refresh_token "nullable"
    }
    APPLICATIONS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR company
        VARCHAR role_title
        VARCHAR status "enum: APPLIED/INTERVIEWING/OFFER/REJECTED/WITHDRAWN"
        TIMESTAMP next_action_date
        VARCHAR priority "enum: LOW/MEDIUM/HIGH"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    CALENDAR_EVENTS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR title
        TIMESTAMP starts_at
        TIMESTAMP ends_at
        VARCHAR source "enum: GOOGLE/MANUAL"
        VARCHAR external_id "nullable, for Google Calendar sync"
    }
```

## Relationships

- **User 1 — N JobApplication**: `@ManyToOne(fetch = LAZY, optional = false)` on [JobApplication.user](../../backend/src/main/java/com/blackcs/propath/model/JobApplication.java). Cascade: none (applications created/deleted independently via the service layer; ownership validated at the service level).
- **User 1 — N CalendarEvent**: same pattern on [CalendarEvent.user](../../backend/src/main/java/com/blackcs/propath/model/CalendarEvent.java).

## Validation

All write endpoints use Jakarta Bean Validation annotations on the request DTOs:
- [RegisterUserRequest](../../backend/src/main/java/com/blackcs/propath/dto/RegisterUserRequest.java): `@NotBlank`, `@Email`, `@Size(min=8)` on password.
- [CreateJobApplicationRequest](../../backend/src/main/java/com/blackcs/propath/dto/CreateJobApplicationRequest.java): all fields required; enum values enforced by Jackson.
- [LoginRequest](../../backend/src/main/java/com/blackcs/propath/dto/LoginRequest.java): `@NotBlank`, `@Email`.

Business rules enforced in services:
- `email` uniqueness (case-insensitive) on registration → 409 Conflict.
- `endsAt > startsAt` for calendar events.
- Ownership — every mutation and single-record GET on applications and calendar events verifies the record belongs to the authenticated user → 403 otherwise.
