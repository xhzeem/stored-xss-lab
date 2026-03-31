You are a senior application security engineer and full-stack architect. Build a fully self-contained, intentionally vulnerable **stored XSS training lab** for **local use only**. The lab must be realistic, reproducible, well-structured, and easy to reset.

IMPORTANT SAFETY / SCOPE RULES
- This project is for an isolated local training environment only.
- Bind services to localhost only.
- Do not include any code that sends email, SMS, webhook notifications, or outbound traffic to third-party services.
- Do not deploy to cloud, do not expose to the public internet, and do not include production hardening.
- Do not include malware, persistence, credential theft, browser exfiltration logic, phishing flows, or anything beyond stored XSS training scenarios.
- Use fake/demo data only.
- Keep everything inside Docker Compose and an isolated internal Docker network.
- Include a clear README warning that this is intentionally vulnerable and must never be internet-exposed.

PRIMARY GOAL
Build a multi-app stored XSS lab with:
- **50 stored XSS vulnerabilities total**
- spread across **5 different apps / language stacks**
- each app must have **at least 10 separate pages**
- each page must contain **one intentionally vulnerable stored XSS scenario**
- every vulnerability must be in a **different rendering context or realistic business flow**
- all pages must be functional mini-features, not just toy inputs

TECH STACK REQUIREMENTS
Use these 5 apps behind a single Nginx reverse proxy:
1. PHP app
2. Golang app
3. Java app
4. Node.js app
5. Python app

Recommended frameworks:
- PHP: plain PHP or Laravel-lite style, but prefer simple maintainable PHP 8 with PDO
- Golang: Gin
- Java: Spring Boot + Thymeleaf or JSP
- Node.js: Express + EJS or Handlebars
- Python: Flask + Jinja2

INFRASTRUCTURE REQUIREMENTS
- Use Docker Compose
- Use Nginx as the single reverse proxy
- Route apps under path prefixes:
  - /php/
  - /go/
  - /java/
  - /node/
  - /python/
- Nginx must preserve the path prefixes correctly
- All services must be on one isolated Docker network
- Expose only Nginx to localhost on one port, for example 8080
- Include:
  - docker-compose.yml
  - nginx/nginx.conf
  - per-app Dockerfiles
  - per-app source directories
  - scripts for setup/reset/seed
  - README.md
  - architecture.md
  - vulnerability-catalog.md

DATA / STORAGE REQUIREMENTS
- Use SQLite for each app unless a framework strongly benefits from H2/SQLite equivalent
- Each app must have its own local database file and seed logic
- Seed realistic demo data for all pages
- Add a reset script to wipe and reseed all apps
- Keep schema simple and readable

AUTH / USER FLOW REQUIREMENTS
Each app must include simple fake authentication with seeded accounts:
- regular user: user / user123
- moderator: mod / mod123
- admin: admin / admin123

Use these roles to create realistic stored XSS flows:
- user submits content
- moderator/admin reviews content
- vulnerable page renders stored content later
- some pages should show user-generated content in dashboards, queues, comments, logs, admin panels, previews, notifications, or reports

Do NOT add any real security protections beyond what is needed to make the lab work.
It is fine for auth to be intentionally weak/simple since this is a lab.

CORE LAB DESIGN
Create exactly **50 vulnerable pages**:
- 10 vulnerable pages in PHP
- 10 vulnerable pages in Go
- 10 vulnerable pages in Java
- 10 vulnerable pages in Node.js
- 10 vulnerable pages in Python

Each vulnerable page must be:
- a different URL
- a different mini-feature
- a stored XSS sink
- backed by storage
- seeded with sample data
- linked from that app’s home/dashboard page
- tagged with difficulty: easy / medium / hard
- labeled internally with a vulnerability ID like XSS-PHP-01, XSS-GO-03, etc.

EVERY APP MUST HAVE AT LEAST THESE BASE PAGES
- login page
- dashboard
- navigation page listing all 10 exercises
- profile/settings
- submissions/content list
- admin/moderation queue
- search or reports page
- help/about page
- seed/reset info page
- logout
These can overlap with the 10 vulnerable pages as long as the app still has at least 10 distinct pages total.

VULNERABILITY VARIETY REQUIREMENT
The 50 stored XSS scenarios must cover a broad range of realistic rendering contexts. Distribute them across the apps so there is minimal duplication. Use these kinds of contexts:

HTML / markup contexts
1. Inner HTML in comment body
2. Rich-text “bio” or profile description
3. Forum post body
4. Product review text
5. Ticket reply body
6. Wiki article snippet
7. Announcement content
8. Chat transcript renderer
9. Activity feed item
10. Notification center message body

Attribute contexts
11. title attribute
12. alt attribute
13. placeholder attribute
14. data-* attribute consumed later by JS
15. aria-label attribute
16. option label/value rendering
17. unquoted attribute context
18. single-quoted attribute context
19. double-quoted attribute context
20. hidden input value later reflected unsafely

JavaScript-adjacent contexts
21. Stored value inserted into inline script string
22. Stored value inserted into JS template literal
23. Stored value inserted into JSON blob inside script tag
24. Stored value inserted into event handler attribute
25. Stored value later read from DOM and inserted with innerHTML
26. Stored value rendered into client-side template
27. Stored value used in a script-generated modal/toast
28. Stored value rendered into chart labels/config data
29. Stored value rendered into a table row via JS
30. Stored value passed through unsafe HTML preview component

URL / navigation contexts
31. href attribute in saved bookmarks
32. src attribute in saved media widget
33. iframe src / embed config
34. redirect/return URL shown in admin tools
35. CTA link text + URL pair saved in CMS block

UI / admin / operational contexts
36. Support ticket subject shown in admin queue
37. Username shown in moderation panel
38. Audit log entry message
39. Imported CSV cell displayed in admin import results
40. Contact form submission viewer
41. Applicant resume summary viewer
42. Guestbook entry
43. Survey free-text response viewer
44. Calendar event description
45. Kanban card note
46. Internal memo board
47. Release notes editor preview
48. Marketing campaign snippet
49. Saved search name shown in shared dashboards
50. Tag/category label rendered in multiple places

Make the scenarios realistic, but do not merely clone the same pattern 50 times.

IMPORTANT IMPLEMENTATION RULES
- These vulnerabilities must be **stored XSS**, not reflected-only.
- The unsafe behavior must come from storing user input and rendering it later.
- Some scenarios should require viewing as another role, e.g. admin/moderator.
- Some scenarios should trigger from list views, some from detail views, some from dashboards, and some from previews.
- Some scenarios should require a two-step workflow, e.g. submit -> approve -> render.
- Use intentionally unsafe rendering APIs/templates appropriate to each stack.
- Do not auto-sanitize all user content.
- Avoid generic “echo raw input everywhere”; build believable features.

DIFFICULTY LAYERS
Split the 50 pages roughly into:
- 20 easy
- 20 medium
- 10 hard

Difficulty examples:
- easy: obvious unsafe rendering in comments or posts
- medium: buried in admin queue, preview, widget, or attribute context
- hard: multi-step review flow, odd rendering context, JSON/script embedding, delayed admin-only sink

APP-SPECIFIC THEMES
Give each app a distinct product/theme so the lab feels varied:

PHP app theme:
- Community forum / guestbook / profile portal

Go app theme:
- Support desk / ticketing / ops dashboard

Java app theme:
- Internal HR / applicant tracking / employee portal

Node.js app theme:
- CMS / marketing dashboard / content manager

Python app theme:
- Project management / notes / reporting suite

For each app, create cohesive UI, navigation, and data models around its theme.

PAGE REQUIREMENTS PER APP
Each of the 10 vulnerable pages in an app must:
- have a form to create or edit stored content
- save to DB
- have a list/detail/admin page that later renders the stored content unsafely
- have realistic labels and validation
- include enough surrounding UI to look like a small real application
- be accessible from the app dashboard
- include a hidden instructor-only metadata comment in source code with:
  - vulnerability ID
  - difficulty
  - sink type
  - stored field name
  - render location
Do not expose the full solution to the learner in the visible UI.

SEED DATA REQUIREMENTS
- Preload each app with fake users, fake content, and realistic records
- At least 5 sample records per vulnerable page
- Include both benign-looking records and records that learners can replace/edit
- Ensure the app is usable immediately after `docker compose up`

ADMIN REVIEW BOT REQUIREMENT
Implement a simple internal “review simulation” mechanism without external browsers/services:
- For pages that need admin/moderator review flow, simulate that by having seeded moderator/admin pages that display submitted content directly when visited
- Do not build a real headless browser bot
- Just make realistic admin review pages and moderation queues

FRONTEND REQUIREMENTS
- Keep styling simple and lightweight
- Use plain CSS or Bootstrap from a local copy only
- No CDN dependencies
- No outbound asset fetches
- Keep UI functional, readable, and consistent

NGINX REQUIREMENTS
- Reverse proxy all 5 apps under path prefixes
- Handle static assets cleanly
- Preserve headers commonly needed by frameworks
- Include comments explaining each upstream and path mapping
- Ensure apps generate URLs that work under subpaths

README REQUIREMENTS
Write a strong README that includes:
- project purpose
- safety warning
- stack overview
- directory structure
- how to build and run
- demo credentials
- how to reset and reseed
- list of apps and themes
- note that this is intentionally vulnerable and must stay local-only

ARCHITECTURE DOCUMENT REQUIREMENTS
Create architecture.md covering:
- high-level design
- service map
- routing map
- database strategy
- auth model
- role model
- vulnerability distribution
- reset/seed workflow

VULNERABILITY CATALOG REQUIREMENTS
Create vulnerability-catalog.md with a table containing:
- Vulnerability ID
- App
- URL
- Theme/feature
- Stored field
- Render sink/context
- Difficulty
- Trigger role
- Notes
Do NOT include working payloads in this catalog.
Do include enough detail for an instructor to understand each scenario.

TESTING / QA REQUIREMENTS
- Add smoke tests or health endpoints for each app
- Add a top-level script that checks all services are reachable
- Make sure all 50 pages exist and are linked
- Verify all pages function after initial seed
- Verify reset script rebuilds sample data

PROJECT STRUCTURE
Use a clean structure like:
- /docker-compose.yml
- /nginx/
- /php-app/
- /go-app/
- /java-app/
- /node-app/
- /python-app/
- /scripts/
- /docs/

QUALITY BAR
- Code should be clean and runnable
- Use comments where helpful
- Avoid placeholders like “TODO”
- Do not output pseudo-code
- Generate the actual implementation files
- Make it buildable in one pass
- Prefer simple dependable code over cleverness

DELIVERABLE FORMAT
Output the complete project as a multi-file codebase, showing every file with its full path and contents.
For each file, use this exact format:

===== FILE: path/to/file.ext =====
<full file contents>

At the very end, include:
1. final run instructions
2. default credentials
3. list of all 50 vulnerable routes grouped by app

SUCCESS CRITERIA
The final result is successful only if:
- there are exactly 5 apps
- there are exactly 50 stored XSS vulnerabilities
- each app has at least 10 pages
- Nginx fronts all apps
- the whole thing runs locally with Docker Compose
- the lab is realistic, resettable, and documented
- no external services are required
- no working exploit payloads are documented in README or catalog

Now generate the complete project.
