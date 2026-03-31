# XSS Training Lab

**Intentionally Vulnerable Stored XSS Training Environment — Local Use Only**

> **WARNING**: This project contains 50 intentionally vulnerable stored XSS scenarios across 5 web applications. It must **NEVER** be exposed to the public internet. Use only on localhost in an isolated environment.

## Purpose

This lab provides a hands-on training environment for learning and practicing stored XSS (Cross-Site Scripting) detection, exploitation, and remediation. It includes 50 distinct vulnerabilities across realistic web application features, covering a wide range of rendering contexts and difficulty levels.

## Stack Overview

| # | App | Language | Framework | Theme | Prefix |
|---|-----|----------|-----------|-------|--------|
| 1 | Community Forum | PHP 8.2 | Plain PHP + PDO | Forum / Guestbook / Profiles | `/php/` |
| 2 | Support Desk | Go | Gin | Ticketing / Helpdesk / Ops | `/go/` |
| 3 | HR Portal | Java 17 | Spring Boot + Thymeleaf | HR / Applicant Tracking | `/java/` |
| 4 | CMS Dashboard | Node.js | Express + EJS | Content / Marketing | `/node/` |
| 5 | Project Manager | Python | Flask + Jinja2 | Projects / Notes / Reports | `/python/` |

All apps are fronted by a single **Nginx** reverse proxy and run via **Docker Compose** on an isolated network.

## Directory Structure

```
stored-xss-lab/
├── docker-compose.yml        # Docker Compose orchestration
├── nginx/
│   └── nginx.conf            # Reverse proxy config
├── php-app/                  # PHP Community Forum
│   ├── Dockerfile
│   ├── config.php
│   ├── index.php
│   ├── posts.php
│   ├── guestbook.php
│   ├── profile.php
│   ├── messages.php
│   ├── notifications.php
│   ├── activity.php
│   ├── admin.php
│   ├── search.php
│   ├── survey.php
│   ├── notif-settings.php
│   ├── login.php / logout.php
│   ├── help.php / seed.php / reset.php / health.php
│   ├── templates/
│   └── css/
├── go-app/                   # Go Support Desk
│   ├── Dockerfile
│   ├── go.mod / go.sum
│   ├── main.go
│   └── templates/
├── java-app/                 # Java HR Portal
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/...
├── node-app/                 # Node.js CMS Dashboard
│   ├── Dockerfile
│   ├── package.json
│   ├── server.js
│   ├── views/
│   └── public/
├── python-app/               # Python Project Manager
│   ├── Dockerfile
│   ├── requirements.txt
│   ├── app.py
│   ├── templates/
│   └── static/
├── scripts/
│   ├── check-health.sh       # Health check for all services
│   └── reset-all.sh          # Reset all databases
├── docs/
│   ├── architecture.md
│   └── vulnerability-catalog.md
├── README.md
└── prompt.md
```

## How to Build and Run

```bash
# Build and start all services
docker compose up --build -d

# Wait for all services to be healthy (about 30 seconds for Java app)
docker compose ps

# Check health
curl http://localhost:4444/health

# Access the lab
open http://localhost:4444
```

## Demo Credentials

All apps share the same demo accounts:

| Username | Password | Role |
|----------|----------|------|
| `user` | `user123` | Regular user |
| `mod` | `mod123` | Moderator |
| `admin` | `admin123` | Admin |

Additional seeded users vary by app (e.g., `alice/alice123`, `bob/bob123`, `editor1/edit123`, etc.).

**Note**: Login is optional. All pages are publicly accessible without authentication.

## How to Reset and Reseed

```bash
# Reset all databases and reseed
./scripts/reset-all.sh

# Or reset individual apps:
docker compose restart php-app    # PHP auto-reseeds on restart
docker compose restart go-app     # Go auto-reseeds on restart
docker compose restart java-app   # Java auto-reseeds on restart
docker compose restart node-app   # Node auto-reseeds on restart
docker compose restart python-app # Python auto-reseeds on restart
```

## Apps and Themes

### 1. PHP — Community Forum / Guestbook / Profile Portal (`/php/`)
Forum with posts, comments, guestbook, profiles, private messages, notifications, activity logs, admin panel, surveys, and search.

### 2. Go — Support Desk / Ticketing / Ops Dashboard (`/go/`)
Ticketing system with live chat, notifications, admin queue, CSV import, memos, release notes, and saved searches.

### 3. Java — HR Portal / Applicant Tracking (`/java/`)
HR system with applicants, interviews, bios, job postings, announcements, calendar, support tickets, audit logs, onboarding, and wiki.

### 4. Node.js — CMS / Marketing Dashboard (`/node/`)
Content manager with pages, reviews, media gallery, content blocks, campaigns, tags, audit logs, and import viewer.

### 5. Python — Project Management / Notes (`/python/`)
Project manager with projects, notes, kanban board, wiki, reports, contacts, calendar, and settings.

## Vulnerability Summary

- **Total**: 50 stored XSS vulnerabilities
- **Per app**: 10 vulnerabilities each
- **Difficulty**: 20 easy, 20 medium, 10 hard
- **Contexts**: HTML body, title/alt/href attributes, data-* attributes, aria-label, inline scripts, JSON blobs, event handlers, option elements, hidden inputs, and more

See `docs/vulnerability-catalog.md` for the full catalog.

## Safety Notice

This lab is designed for **educational purposes only** in an **isolated local environment**:

- All services bind to localhost only
- No outbound network connections
- No real data or credentials
- No production hardening
- No security protections (intentionally)
- Must never be deployed to cloud or public internet
