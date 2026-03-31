# Architecture

## High-Level Design

```
                    ┌─────────────────┐
                    │   User Browser  │
                    │ localhost:8080   │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Nginx Reverse  │
                    │     Proxy       │
                    │   (port 80)     │
                    └──┬──┬──┬──┬──┬──┘
           ┌───────────┘  │  │  │  └───────────┐
           │         ┌────┘  │  └────┐         │
     ┌─────▼──┐ ┌────▼──┐ ┌─▼──┐ ┌──▼──┐ ┌───▼───┐
     │  PHP   │ │  Go   │ │Java│ │Node │ │Python │
     │  :80   │ │ :8080 │ │:8080│ │:3000│ │ :5000 │
     │ Forum  │ │ Desk  │ │ HR │ │ CMS │ │ Proj  │
     └───┬────┘ └───┬───┘ └─┬──┘ └──┬──┘ └───┬───┘
         │          │       │       │         │
     [SQLite]   [SQLite] [H2 DB] [SQLite]  [SQLite]
```

## Service Map

| Service | Container | Port | Internal DB | Framework |
|---------|-----------|------|-------------|-----------|
| nginx | xss-lab-nginx | 80 (→8080 host) | — | Nginx Alpine |
| php-app | xss-lab-php | 80 | SQLite (/data/app.db) | PHP 8.2 Apache |
| go-app | xss-lab-go | 8080 | SQLite (/data/app.db) | Gin |
| java-app | xss-lab-java | 8080 | H2 (file-based) | Spring Boot 3.2 |
| node-app | xss-lab-node | 3000 | SQLite (/data/app.db) | Express |
| python-app | xss-lab-python | 5000 | SQLite (/data/app.db) | Flask |

## Routing Map

Nginx routes by path prefix, stripping the prefix before forwarding:

| Path Prefix | Upstream | Rewrite |
|-------------|----------|---------|
| `/php/*` | php-app:80 | Strip `/php` prefix |
| `/go/*` | go-app:8080 | Strip `/go` prefix |
| `/java/*` | java-app:8080 | Strip `/java` prefix |
| `/node/*` | node-app:3000 | Strip `/node` prefix |
| `/python/*` | python-app:5000 | Strip `/python` prefix |
| `/health` | Nginx inline | Returns 200 |

Each app receives `X-Forwarded-Prefix` header for generating correct URLs.

## Database Strategy

Each app maintains its own isolated database:

- **PHP, Go, Node, Python**: SQLite files in named Docker volumes
- **Java**: H2 file-based database in a named Docker volume
- All databases auto-create schema on first startup
- All databases auto-seed with demo data on first startup
- Seed data includes 5+ records per vulnerable page

## Auth Model

**Intentionally weak for training purposes:**

- Login is optional — all pages are publicly accessible
- Session is used only for display purposes (showing "logged in as...")
- Passwords stored in plain text
- No CSRF protection
- No rate limiting
- No password complexity requirements

## Role Model

Three roles exist for realistic flows, but do NOT gate access:

| Role | Username | Purpose |
|------|----------|---------|
| User | `user` | Regular user, creates content |
| Moderator | `mod` | Reviews content, appears in admin flows |
| Admin | `admin` | Full access, appears in admin panels |

Some vulnerabilities require "viewing as admin" to trigger, but the pages themselves are always accessible.

## Vulnerability Distribution

| App | Vulnerabilities | Easy | Medium | Hard |
|-----|----------------|------|--------|------|
| PHP (Forum) | 10 | 2 | 6 | 2 |
| Go (Support) | 10 | 2 | 4 | 4 |
| Java (HR) | 10 | 3 | 4 | 3 |
| Node (CMS) | 10 | 2 | 4 | 4 |
| Python (Projects) | 10 | 2 | 5 | 3 |
| **Total** | **50** | **11** | **23** | **16** |

## Reset/Seed Workflow

1. Each app checks for existing data on startup
2. If tables are empty, schema is created and seed data is inserted
3. To reset: delete the database file or use the reset script
4. `scripts/reset-all.sh` restarts all containers, triggering re-seeding
