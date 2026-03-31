import sqlite3
import json
import os
from flask import Flask, render_template, request, redirect, url_for, session, jsonify

app = Flask(__name__, template_folder='templates', static_folder='static')
app.secret_key = 'xss-lab-secret-key-do-not-change'
DB_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'lab.db')


@app.context_processor
def inject_prefix():
    prefix = request.headers.get('X-Forwarded-Prefix', '')
    return dict(prefix=prefix)


def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    conn = get_db()
    cur = conn.cursor()

    cur.executescript('''
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        role TEXT NOT NULL DEFAULT 'user'
    );
    CREATE TABLE IF NOT EXISTS projects (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        owner TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT,
        status TEXT DEFAULT 'active',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        author TEXT NOT NULL,
        project_id INTEGER,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS kanban_cards (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id INTEGER,
        title TEXT NOT NULL,
        note TEXT,
        column_name TEXT DEFAULT 'todo',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS wiki_pages (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id INTEGER,
        title TEXT NOT NULL,
        author TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS reports (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        author TEXT NOT NULL,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        report_type TEXT DEFAULT 'weekly',
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS contacts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT,
        company TEXT,
        notes TEXT,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS calendar_events (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id INTEGER,
        title TEXT NOT NULL,
        description TEXT,
        event_date TEXT,
        organizer TEXT,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    CREATE TABLE IF NOT EXISTS settings (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id INTEGER,
        key TEXT NOT NULL,
        value TEXT,
        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );
    ''')

    # Check if already seeded
    cur.execute('SELECT COUNT(*) FROM users')
    if cur.fetchone()[0] == 0:
        seed_db(cur)

    conn.commit()
    conn.close()


def seed_db(cur):
    # Users
    users = [
        ('user', 'user123', 'user'),
        ('mod', 'mod123', 'moderator'),
        ('admin', 'admin123', 'admin'),
        ('pm1', 'pm123', 'project_manager'),
        ('dev1', 'dev123', 'developer'),
    ]
    cur.executemany('INSERT INTO users (username, password, role) VALUES (?, ?, ?)', users)

    # Projects
    projects = [
        ('admin', 'Website Redesign', 'Complete overhaul of the company website with new branding and improved UX.', 'active'),
        ('pm1', 'Mobile App v2', 'Next generation mobile application with offline support and push notifications.', 'active'),
        ('admin', 'API Gateway', 'Centralized API gateway for all microservices with rate limiting and auth.', 'active'),
        ('dev1', 'Data Pipeline', 'ETL pipeline for processing raw analytics data into actionable reports.', 'planning'),
        ('pm1', 'Customer Portal', 'Self-service portal for customers to manage accounts and submit tickets.', 'active'),
        ('admin', 'Internal Dashboard', 'Real-time monitoring dashboard for DevOps and infrastructure teams.', 'completed'),
    ]
    cur.executemany('INSERT INTO projects (owner, name, description, status) VALUES (?, ?, ?, ?)', projects)

    # Notes
    notes = [
        ('admin', 1, 'Sprint Retrospective', 'We need to improve our code review turnaround time. Currently averaging 3 days per PR.', ),
        ('dev1', 2, 'API Deprecation Notice', 'The old /v1/users endpoint will be removed on March 1st. All clients must migrate to /v2/users.', ),
        ('pm1', 3, 'Deployment Checklist', '1. Run migrations\n2. Clear cache\n3. Update DNS records\n4. Smoke test critical paths', ),
        ('mod', 1, 'Design Feedback', 'The new color scheme looks great but the contrast ratio on buttons needs improvement for accessibility.', ),
        ('dev1', 4, 'Performance Notes', 'Database query optimization reduced page load time from 2.3s to 0.8s on the dashboard.', ),
        ('admin', 5, 'Security Audit Results', 'Found 3 medium severity issues in the auth module. Fix prioritized for next sprint.', ),
        ('pm1', 6, 'Release Notes v3.2', 'New features: dark mode, export to PDF, bulk actions. Bug fixes: 14 issues resolved.', ),
    ]
    for n in notes:
        cur.execute('INSERT INTO notes (author, project_id, title, content) VALUES (?, ?, ?, ?)', n)

    # Kanban cards
    kanban = [
        (1, 'Setup CI/CD Pipeline', 'Configure GitHub Actions for automated testing and deployment', 'done'),
        (2, 'Design Login Screen', 'Create mockups for the new authentication flow with biometric support', 'in-progress'),
        (3, 'Write API Docs', 'Document all REST endpoints using OpenAPI/Swagger specification', 'todo'),
        (4, 'Database Migration', 'Migrate from MySQL 5.7 to PostgreSQL 15 with zero downtime', 'in-progress'),
        (5, 'User Acceptance Testing', 'Coordinate with stakeholders for UAT sessions next week', 'todo'),
        (6, 'Load Testing', 'Run k6 load tests against staging environment, target 10k concurrent users', 'todo'),
        (1, 'Accessibility Audit', 'Ensure WCAG 2.1 AA compliance across all pages', 'in-progress'),
    ]
    for k in kanban:
        cur.execute('INSERT INTO kanban_cards (project_id, title, note, column_name) VALUES (?, ?, ?, ?)', k)

    # Wiki pages
    wiki = [
        (1, 'Getting Started', 'admin', 'Welcome to the project wiki. This guide covers initial setup, development environment configuration, and contribution guidelines.'),
        (2, 'Architecture Overview', 'dev1', 'The system follows a microservices architecture with 12 services communicating via gRPC. The main components are the API Gateway, Auth Service, and Data Service.'),
        (3, 'Deployment Guide', 'pm1', 'Production deployments happen every Tuesday and Thursday. Follow the deployment checklist in the notes section before releasing.'),
        (4, 'Troubleshooting', 'mod', 'Common issues and their solutions. If your issue is not listed here, contact the on-call engineer via PagerDuty.'),
        (5, 'API Reference', 'dev1', 'Complete API reference documentation. All endpoints require authentication via Bearer token. Rate limiting is set to 100 requests per minute.'),
        (6, 'Coding Standards', 'admin', 'All code must pass ESLint with our custom config. Use TypeScript for new features. Write unit tests for all business logic.'),
    ]
    for w in wiki:
        cur.execute('INSERT INTO wiki_pages (project_id, title, author, content) VALUES (?, ?, ?, ?)', w)

    # Reports
    reports = [
        ('pm1', 'Q1 Sprint Summary', 'Completed 47 out of 52 planned story points. Velocity trending upward. Key blockers resolved.', 'weekly'),
        ('admin', 'Infrastructure Costs', 'Monthly cloud spend: $4,230. Down 12% from last month due to reserved instance optimization.', 'monthly'),
        ('dev1', 'Bug Triage Report', 'Open bugs: 23 critical, 45 medium, 89 low. Mean time to resolution improved by 18%.', 'weekly'),
        ('mod', 'User Feedback Analysis', 'NPS score: 72 (up from 68). Top requests: dark mode, mobile app, better search.', 'monthly'),
        ('admin', 'Security Incident Report', 'Minor incident on Feb 15th. DDoS attempt mitigated by Cloudflare. No data breach.', 'incident'),
        ('pm1', 'Release Readiness', 'All acceptance criteria met for v3.2. Performance benchmarks passed. Go/No-Go meeting scheduled.', 'weekly'),
    ]
    for r in reports:
        cur.execute('INSERT INTO reports (author, title, content, report_type) VALUES (?, ?, ?, ?)', r)

    # Contacts
    contacts = [
        ('Alice Johnson', 'alice@acmecorp.com', 'Acme Corp', 'Primary vendor contact for cloud infrastructure services.'),
        ('Bob Martinez', 'bob@techstartup.io', 'TechStartup.io', 'Potential integration partner. Follow up on API compatibility.'),
        ('Carol Chen', 'carol@bigcorp.com', 'BigCorp Inc', 'Enterprise client. Handle with care. Very particular about SLAs.'),
        ('David Kim', 'david@designstudio.co', 'Design Studio', 'UI/UX contractor. Excellent work on the last redesign project.'),
        ('Eva Petrova', 'eva@securityfirm.eu', 'Security Firm EU', 'External security auditor. Annual engagement starts in Q2.'),
        ('Frank Wilson', 'frank@datacompany.com', 'DataCo Analytics', 'Data analytics vendor. Evaluating their platform for our pipeline.'),
    ]
    for c in contacts:
        cur.execute('INSERT INTO contacts (name, email, company, notes) VALUES (?, ?, ?, ?)', c)

    # Calendar events
    cal = [
        (1, 'Sprint Planning', 'Plan stories and tasks for Sprint 14. Review backlog priorities.', '2026-04-01', 'pm1'),
        (2, 'Design Review', 'Review new mockups with the design team. Focus on mobile responsiveness.', '2026-04-03', 'admin'),
        (3, 'Deployment Window', 'Scheduled production deployment for API v2.3. Maintenance window: 2-4 AM UTC.', '2026-04-05', 'dev1'),
        (4, 'Stakeholder Demo', 'Demo completed features to executive team. Prepare slide deck and demo environment.', '2026-04-08', 'pm1'),
        (5, 'Security Training', 'Mandatory security awareness training for all engineering staff.', '2026-04-10', 'admin'),
        (6, 'Team Retro', 'Sprint 13 retrospective. Discuss what went well and areas for improvement.', '2026-04-12', 'mod'),
    ]
    for e in cal:
        cur.execute('INSERT INTO calendar_events (project_id, title, description, event_date, organizer) VALUES (?, ?, ?, ?, ?)', e)

    # Settings
    settings = [
        (1, 'theme', 'dark'),
        (2, 'notifications_enabled', 'true'),
        (3, 'default_branch', 'main'),
        (4, 'timezone', 'UTC'),
        (5, 'language', 'en'),
        (1, 'auto_deploy', 'false'),
        (2, 'review_required', 'true'),
    ]
    for s in settings:
        cur.execute('INSERT INTO settings (project_id, key, value) VALUES (?, ?, ?)', s)


@app.route('/')
def index():
    conn = get_db()
    projects = conn.execute('SELECT * FROM projects ORDER BY created_at DESC LIMIT 6').fetchall()
    recent_notes = conn.execute('SELECT * FROM notes ORDER BY created_at DESC LIMIT 5').fetchall()
    conn.close()
    return render_template('index.html', projects=projects, recent_notes=recent_notes)


@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        username = request.form.get('username', '')
        password = request.form.get('password', '')
        conn = get_db()
        user = conn.execute('SELECT * FROM users WHERE username = ? AND password = ?',
                            (username, password)).fetchone()
        conn.close()
        if user:
            session['username'] = user['username']
            session['role'] = user['role']
            return redirect(url_for('index'))
        else:
            error = 'Invalid credentials'
    return render_template('login.html', error=error)


@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('index'))


@app.route('/projects', methods=['GET', 'POST'])
def projects():
    conn = get_db()
    if request.method == 'POST':
        name = request.form.get('name', '')
        description = request.form.get('description', '')
        owner = session.get('username', 'anonymous')
        conn.execute('INSERT INTO projects (owner, name, description) VALUES (?, ?, ?)',
                     (owner, name, description))
        conn.commit()
    all_projects = conn.execute('SELECT * FROM projects ORDER BY created_at DESC').fetchall()
    conn.close()
    return render_template('projects.html', projects=all_projects)


@app.route('/project/<int:project_id>')
def project(project_id):
    conn = get_db()
    proj = conn.execute('SELECT * FROM projects WHERE id = ?', (project_id,)).fetchone()
    notes = conn.execute('SELECT * FROM notes WHERE project_id = ? ORDER BY created_at DESC',
                         (project_id,)).fetchall()
    conn.close()
    return render_template('project.html', project=proj, notes=notes)


@app.route('/notes', methods=['GET', 'POST'])
def notes():
    conn = get_db()
    if request.method == 'POST':
        author = session.get('username', 'anonymous')
        title = request.form.get('title', '')
        content = request.form.get('content', '')
        project_id = request.form.get('project_id', None)
        conn.execute('INSERT INTO notes (author, title, content, project_id) VALUES (?, ?, ?, ?)',
                     (author, title, content, project_id))
        conn.commit()
    all_notes = conn.execute('SELECT * FROM notes ORDER BY created_at DESC').fetchall()
    projects = conn.execute('SELECT id, name FROM projects').fetchall()
    conn.close()
    return render_template('notes.html', notes=all_notes, projects=projects)


@app.route('/kanban', methods=['GET', 'POST'])
def kanban():
    conn = get_db()
    if request.method == 'POST':
        title = request.form.get('title', '')
        note = request.form.get('note', '')
        project_id = request.form.get('project_id', None)
        column_name = request.form.get('column_name', 'todo')
        conn.execute('INSERT INTO kanban_cards (project_id, title, note, column_name) VALUES (?, ?, ?, ?)',
                     (project_id, title, note, column_name))
        conn.commit()
    cards = conn.execute('SELECT * FROM kanban_cards ORDER BY created_at DESC').fetchall()
    projects = conn.execute('SELECT id, name FROM projects').fetchall()
    conn.close()
    return render_template('kanban.html', cards=cards, projects=projects)


@app.route('/wiki', methods=['GET', 'POST'])
def wiki():
    conn = get_db()
    if request.method == 'POST':
        title = request.form.get('title', '')
        content = request.form.get('content', '')
        author = session.get('username', 'anonymous')
        project_id = request.form.get('project_id', None)
        conn.execute('INSERT INTO wiki_pages (project_id, title, author, content) VALUES (?, ?, ?, ?)',
                     (project_id, title, author, content))
        conn.commit()
    pages = conn.execute('SELECT * FROM wiki_pages ORDER BY created_at DESC').fetchall()
    projects = conn.execute('SELECT id, name FROM projects').fetchall()
    conn.close()
    return render_template('wiki.html', pages=pages, projects=projects, json_data=json.dumps([dict(p) for p in pages]))


@app.route('/reports', methods=['GET', 'POST'])
def reports():
    conn = get_db()
    if request.method == 'POST':
        title = request.form.get('title', '')
        content = request.form.get('content', '')
        report_type = request.form.get('report_type', 'weekly')
        author = session.get('username', 'anonymous')
        conn.execute('INSERT INTO reports (author, title, content, report_type) VALUES (?, ?, ?, ?)',
                     (author, title, content, report_type))
        conn.commit()
    all_reports = conn.execute('SELECT * FROM reports ORDER BY created_at DESC').fetchall()
    conn.close()
    return render_template('reports.html', reports=all_reports)


@app.route('/contacts', methods=['GET', 'POST'])
def contacts():
    conn = get_db()
    if request.method == 'POST':
        name = request.form.get('name', '')
        email = request.form.get('email', '')
        company = request.form.get('company', '')
        notes = request.form.get('notes', '')
        conn.execute('INSERT INTO contacts (name, email, company, notes) VALUES (?, ?, ?, ?)',
                     (name, email, company, notes))
        conn.commit()
    all_contacts = conn.execute('SELECT * FROM contacts ORDER BY created_at DESC').fetchall()
    conn.close()
    return render_template('contacts.html', contacts=all_contacts)


@app.route('/calendar', methods=['GET', 'POST'])
def calendar():
    conn = get_db()
    if request.method == 'POST':
        title = request.form.get('title', '')
        description = request.form.get('description', '')
        event_date = request.form.get('event_date', '')
        organizer = session.get('username', 'anonymous')
        project_id = request.form.get('project_id', None)
        conn.execute('INSERT INTO calendar_events (project_id, title, description, event_date, organizer) VALUES (?, ?, ?, ?, ?)',
                     (project_id, title, description, event_date, organizer))
        conn.commit()
    events = conn.execute('SELECT * FROM calendar_events ORDER BY event_date ASC').fetchall()
    projects = conn.execute('SELECT id, name FROM projects').fetchall()
    conn.close()
    return render_template('calendar.html', events=events, projects=projects)


@app.route('/settings', methods=['GET', 'POST'])
def settings():
    conn = get_db()
    if request.method == 'POST':
        project_id = request.form.get('project_id', None)
        key = request.form.get('key', '')
        value = request.form.get('value', '')
        existing = conn.execute('SELECT id FROM settings WHERE project_id = ? AND key = ?',
                                (project_id, key)).fetchone()
        if existing:
            conn.execute('UPDATE settings SET value = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?',
                         (value, existing['id']))
        else:
            conn.execute('INSERT INTO settings (project_id, key, value) VALUES (?, ?, ?)',
                         (project_id, key, value))
        conn.commit()
    all_settings = conn.execute('SELECT * FROM settings ORDER BY project_id, key').fetchall()
    projects = conn.execute('SELECT id, name FROM projects').fetchall()
    conn.close()
    return render_template('settings.html', settings=all_settings, projects=projects)


@app.route('/help')
def help_page():
    return render_template('help.html')


@app.route('/health')
def health():
    return jsonify({'status': 'ok', 'service': 'stored-xss-lab-python'})


if __name__ == '__main__':
    init_db()
    app.run(host='0.0.0.0', port=5000, debug=True)
