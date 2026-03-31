const express = require('express');
const path = require('path');
const fs = require('fs');
const session = require('express-session');
const Database = require('better-sqlite3');

const app = express();
const PORT = 3000;

// View engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Body parsing
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Session (for optional login display only - does NOT gate any page)
app.use(session({
  secret: 'xss-lab-secret-key',
  resave: false,
  saveUninitialized: true
}));

// Static files
app.use(express.static(path.join(__dirname, 'public')));

// Forwarded prefix (for reverse proxy path rewriting)
app.use((req, res, next) => {
  res.locals.prefix = req.headers['x-forwarded-prefix'] || '';
  next();
});

// Database
const dbPath = process.env.DB_PATH || path.join(__dirname, 'data.db');
const dbDir = path.dirname(dbPath);
if (!fs.existsSync(dbDir)) fs.mkdirSync(dbDir, { recursive: true });
const db = new Database(dbPath);
db.pragma('journal_mode = WAL');

// Initialize schema
db.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'user'
  );
  CREATE TABLE IF NOT EXISTS pages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    author TEXT NOT NULL,
    title TEXT NOT NULL,
    slug TEXT NOT NULL,
    content TEXT NOT NULL,
    status TEXT DEFAULT 'draft',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    author TEXT NOT NULL,
    product TEXT NOT NULL,
    rating INTEGER DEFAULT 5,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS media (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uploaded_by TEXT NOT NULL,
    filename TEXT NOT NULL,
    alt_text TEXT,
    caption TEXT,
    url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS content_blocks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    block_type TEXT DEFAULT 'text',
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS campaigns (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    channel TEXT DEFAULT 'email',
    snippet TEXT NOT NULL,
    status TEXT DEFAULT 'draft',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    actor TEXT NOT NULL,
    action TEXT NOT NULL,
    details TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
  CREATE TABLE IF NOT EXISTS imports (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    filename TEXT NOT NULL,
    row_data TEXT NOT NULL,
    imported_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );
`);

// Seed data if empty (per-table to avoid skipping when only some tables are populated)
function seed() {
  // Users
  if (db.prepare('SELECT COUNT(*) as c FROM users').get().c === 0) {
    const insertUser = db.prepare('INSERT INTO users (username, password, role) VALUES (?, ?, ?)');
    insertUser.run('user', 'user123', 'user');
    insertUser.run('mod', 'mod123', 'moderator');
    insertUser.run('admin', 'admin123', 'admin');
    insertUser.run('editor1', 'edit123', 'editor');
    insertUser.run('editor2', 'edit123', 'editor');
  }

  // Pages
  if (db.prepare('SELECT COUNT(*) as c FROM pages').get().c === 0) {
    const insertPage = db.prepare('INSERT INTO pages (author, title, slug, content, status) VALUES (?, ?, ?, ?, ?)');
    insertPage.run('editor1', 'Welcome to Our Site', 'welcome', '<h1>Welcome</h1><p>We are glad to have you here. Explore our products and services.</p>', 'published');
    insertPage.run('editor1', 'About Us', 'about', '<h1>About Us</h1><p>We are a leading company in the tech industry since 2010.</p>', 'published');
    insertPage.run('editor2', 'Privacy Policy', 'privacy', '<h1>Privacy Policy</h1><p>Your privacy is important to us. We collect minimal data.</p>', 'published');
    insertPage.run('editor2', 'Terms of Service', 'terms', '<h1>Terms of Service</h1><p>By using this site you agree to our terms.</p>', 'published');
    insertPage.run('admin', 'Contact Us', 'contact', '<h1>Contact</h1><p>Email us at info@example.com or call 555-0100.</p>', 'published');
    insertPage.run('editor1', 'Blog Post: Launch Day', 'launch-day', '<h1>Launch Day</h1><p>Today marks our official launch. Stay tuned for updates!</p>', 'published');
    insertPage.run('editor1', 'Spring Sale', 'spring-sale', '<h1>Spring Sale</h1><p>Enjoy up to 50% off on selected items this spring season.</p>', 'published');
    insertPage.run('admin', 'Launch 2024', 'launch-2024', '<h1>Launch 2024</h1><p>Our 2024 product launch campaign. Discover what is new.</p>', 'published');
    insertPage.run('editor2', 'Holiday 2024', 'holiday-2024', '<h1>Holiday 2024</h1><p>Holiday specials and gift ideas for the 2024 season.</p>', 'published');
    insertPage.run('editor1', 'Invite a Friend', 'invite', '<h1>Invite a Friend</h1><p>Refer a friend and both of you get 20% off your next purchase.</p>', 'published');
    insertPage.run('admin', 'Webinar March 2024', 'webinar-mar-2024', '<h1>Webinar March 2024</h1><p>Join our live webinar on March 15, 2024 to learn about our platform.</p>', 'published');
    insertPage.run('editor1', 'Flash Sale', 'flash-sale', '<h1>Flash Sale</h1><p>Limited time flash sale — deals end tonight!</p>', 'published');
  }

  // Reviews
  if (db.prepare('SELECT COUNT(*) as c FROM reviews').get().c === 0) {
    const insertReview = db.prepare('INSERT INTO reviews (author, product, rating, content) VALUES (?, ?, ?, ?)');
    insertReview.run('Alice', 'Widget Pro', 5, 'Absolutely love this product! Best purchase I have made this year.');
    insertReview.run('Bob', 'Gadget X', 4, 'Great gadget, works as expected. Battery life could be better.');
    insertReview.run('Charlie', 'Widget Pro', 3, 'Decent product but shipping took too long.');
    insertReview.run('Diana', 'Service Plan', 5, 'Customer support was amazing. Highly recommend!');
    insertReview.run('Eve', 'Gadget X', 2, 'Not what I expected. The build quality feels cheap.');
    insertReview.run('Frank', 'Widget Lite', 4, 'Good value for money. Does everything I need.');
  }

  // Media
  if (db.prepare('SELECT COUNT(*) as c FROM media').get().c === 0) {
    const insertMedia = db.prepare('INSERT INTO media (uploaded_by, filename, alt_text, caption, url) VALUES (?, ?, ?, ?, ?)');
    insertMedia.run('editor1', 'hero-banner.jpg', 'Hero banner showing our product lineup', 'Our latest products', '/images/hero-banner.jpg');
    insertMedia.run('editor1', 'team-photo.jpg', 'Photo of the development team', 'Meet the team', '/images/team-photo.jpg');
    insertMedia.run('editor2', 'product-shot.png', 'Widget Pro product photo on white background', 'Widget Pro', '/images/product-shot.png');
    insertMedia.run('admin', 'logo.svg', 'Company logo', 'Brand logo', '/images/logo.svg');
    insertMedia.run('editor2', 'office-tour.jpg', 'Inside view of our main office', 'Our headquarters', '/images/office-tour.jpg');
    insertMedia.run('editor1', 'chart-q4.png', 'Q4 sales growth chart', 'Q4 Results', '/images/chart-q4.png');
  }

  // Content blocks
  if (db.prepare('SELECT COUNT(*) as c FROM content_blocks').get().c === 0) {
    const insertBlock = db.prepare('INSERT INTO content_blocks (name, block_type, content) VALUES (?, ?, ?)');
    insertBlock.run('Header CTA', 'html', '<div class="cta"><h2>Get Started Today</h2><a href="/signup">Sign Up Free</a></div>');
    insertBlock.run('Footer Links', 'text', 'Home | About | Contact | Privacy | Terms');
    insertBlock.run('Hero Text', 'text', 'Build something amazing with our platform. Try it free for 30 days.');
    insertBlock.run('Testimonial', 'html', '<blockquote><p>This platform changed our business completely.</p></blockquote>');
    insertBlock.run('Sidebar Promo', 'html', '<div class="promo"><p>Special offer: 50% off all plans this month!</p></div>');
    insertBlock.run('Newsletter Box', 'text', 'Subscribe to our newsletter for weekly updates and tips.');
  }

  // Campaigns
  if (db.prepare('SELECT COUNT(*) as c FROM campaigns').get().c === 0) {
    const insertCampaign = db.prepare('INSERT INTO campaigns (name, channel, snippet, status) VALUES (?, ?, ?, ?)');
    insertCampaign.run('Spring Sale', 'email', '/pages/spring-sale', 'active');
    insertCampaign.run('Product Launch', 'social', '/pages/launch-2024', 'completed');
    insertCampaign.run('Holiday Special', 'email', '/pages/holiday-2024', 'draft');
    insertCampaign.run('Referral Program', 'email', '/pages/welcome', 'active');
    insertCampaign.run('Webinar Invite', 'email', '/pages/webinar-mar-2024', 'completed');
    insertCampaign.run('Flash Sale', 'sms', '/pages/about', 'draft');
  }

  // Tags
  if (db.prepare('SELECT COUNT(*) as c FROM tags').get().c === 0) {
    const insertTag = db.prepare('INSERT INTO tags (name, description) VALUES (?, ?)');
    insertTag.run('featured', 'Featured content shown on homepage');
    insertTag.run('technology', 'Technology related posts and pages');
    insertTag.run('marketing', 'Marketing materials and campaigns');
    insertTag.run('product', 'Product pages and reviews');
    insertTag.run('announcement', 'Official announcements and news');
    insertTag.run('tutorial', 'How-to guides and tutorials');
  }

  // Audit logs
  if (db.prepare('SELECT COUNT(*) as c FROM audit_logs').get().c === 0) {
    const insertAudit = db.prepare('INSERT INTO audit_logs (actor, action, details) VALUES (?, ?, ?)');
    insertAudit.run('admin', 'user_created', 'Created user editor1 with role editor');
    insertAudit.run('editor1', 'page_created', 'Created page: Welcome to Our Site');
    insertAudit.run('editor2', 'media_uploaded', 'Uploaded product-shot.png');
    insertAudit.run('admin', 'campaign_updated', 'Set Spring Sale campaign to active');
    insertAudit.run('editor1', 'page_published', 'Published page: About Us');
    insertAudit.run('mod', 'review_approved', 'Approved review by Alice on Widget Pro');
  }

  // Imports
  if (db.prepare('SELECT COUNT(*) as c FROM imports').get().c === 0) {
    const insertImport = db.prepare('INSERT INTO imports (filename, row_data) VALUES (?, ?)');
    insertImport.run('products.csv', 'Widget Pro,99.99,Electronics,In Stock');
    insertImport.run('products.csv', 'Gadget X,149.99,Electronics,In Stock');
    insertImport.run('users-export.csv', 'editor1,Editor One,editor1@example.com');
    insertImport.run('users-export.csv', 'editor2,Editor Two,editor2@example.com');
    insertImport.run('blog-posts.csv', 'Launch Day,editor1,2024-01-15,published');
    insertImport.run('reviews-bulk.csv', 'Alice,Widget Pro,5,Great product');
  }
}

seed();

// Health endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok', uptime: process.uptime() });
});

// Root - Dashboard (XSS-NODE-10: snippet in href)
app.get('/', (req, res) => {
  const pages = db.prepare('SELECT * FROM pages ORDER BY created_at DESC LIMIT 5').all();
  const reviews = db.prepare('SELECT * FROM reviews ORDER BY created_at DESC LIMIT 5').all();
  const campaigns = db.prepare('SELECT * FROM campaigns ORDER BY created_at DESC LIMIT 5').all();
  const media = db.prepare('SELECT * FROM media ORDER BY created_at DESC LIMIT 5').all();
  const tags = db.prepare('SELECT * FROM tags ORDER BY created_at DESC LIMIT 5').all();
  const audit = db.prepare('SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT 5').all();
  res.render('index', { pages, reviews, campaigns, media, tags, audit, user: req.session.user || null });
});

// Login (optional, does NOT gate anything)
app.get('/login', (req, res) => {
  res.render('login', { error: null, user: req.session.user || null });
});

app.post('/login', (req, res) => {
  const { username, password } = req.body;
  const user = db.prepare('SELECT * FROM users WHERE username = ? AND password = ?').get(username, password);
  if (user) {
    req.session.user = user;
    res.redirect((req.headers['x-forwarded-prefix'] || '') + '/');
  } else {
    res.render('login', { error: 'Invalid credentials', user: null });
  }
});

app.get('/logout', (req, res) => {
  req.session.destroy();
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/');
});

// Pages list (XSS-NODE-09: title in option label)
app.get('/pages', (req, res) => {
  const pages = db.prepare('SELECT * FROM pages ORDER BY created_at DESC').all();
  res.render('pages', { pages, user: req.session.user || null });
});

app.post('/pages', (req, res) => {
  const { author, title, slug, content, status } = req.body;
  db.prepare('INSERT INTO pages (author, title, slug, content, status) VALUES (?, ?, ?, ?, ?)').run(author || 'anonymous', title, slug, content, status || 'draft');
  db.prepare('INSERT INTO audit_logs (actor, action, details) VALUES (?, ?, ?)').run(author || 'anonymous', 'page_created', 'Created page: ' + title);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/pages');
});

// Single page view (XSS-NODE-01: content with <%-)
app.get('/pages/:slug', (req, res) => {
  const page = db.prepare('SELECT * FROM pages WHERE slug = ?').get(req.params.slug);
  if (!page) return res.status(404).send('Page not found');
  res.render('page', { page, user: req.session.user || null });
});

// Reviews (XSS-NODE-02: content with <%-)
app.get('/reviews', (req, res) => {
  const reviews = db.prepare('SELECT * FROM reviews ORDER BY created_at DESC').all();
  res.render('reviews', { reviews, user: req.session.user || null });
});

app.post('/reviews', (req, res) => {
  const { author, product, rating, content } = req.body;
  db.prepare('INSERT INTO reviews (author, product, rating, content) VALUES (?, ?, ?, ?)').run(author, product, parseInt(rating) || 5, content);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/reviews');
});

// Media (XSS-NODE-03: alt_text in <img alt>)
app.get('/media', (req, res) => {
  const media = db.prepare('SELECT * FROM media ORDER BY created_at DESC').all();
  res.render('media', { media, user: req.session.user || null });
});

app.post('/media', (req, res) => {
  const { uploaded_by, filename, alt_text, caption, url } = req.body;
  db.prepare('INSERT INTO media (uploaded_by, filename, alt_text, caption, url) VALUES (?, ?, ?, ?, ?)').run(uploaded_by, filename, alt_text, caption, url);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/media');
});

// Content Blocks (XSS-NODE-04: content in script JSON)
app.get('/blocks', (req, res) => {
  const blocks = db.prepare('SELECT * FROM content_blocks ORDER BY created_at DESC').all();
  res.render('blocks', { blocks, user: req.session.user || null });
});

app.post('/blocks', (req, res) => {
  const { name, block_type, content } = req.body;
  db.prepare('INSERT INTO content_blocks (name, block_type, content) VALUES (?, ?, ?)').run(name, block_type || 'text', content);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/blocks');
});

// Campaigns (XSS-NODE-05: snippet in onclick)
app.get('/campaigns', (req, res) => {
  const campaigns = db.prepare('SELECT * FROM campaigns ORDER BY created_at DESC').all();
  res.render('campaigns', { campaigns, user: req.session.user || null });
});

app.post('/campaigns', (req, res) => {
  const { name, channel, snippet, status } = req.body;
  db.prepare('INSERT INTO campaigns (name, channel, snippet, status) VALUES (?, ?, ?, ?)').run(name, channel || 'email', snippet, status || 'draft');
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/campaigns');
});

// Tags (XSS-NODE-06: name in hidden input value)
app.get('/tags', (req, res) => {
  const tags = db.prepare('SELECT * FROM tags ORDER BY created_at DESC').all();
  res.render('tags', { tags, user: req.session.user || null });
});

app.post('/tags', (req, res) => {
  const { name, description } = req.body;
  db.prepare('INSERT INTO tags (name, description) VALUES (?, ?)').run(name, description);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/tags');
});

// Audit (XSS-NODE-07: details in data-attr then innerHTML)
app.get('/audit', (req, res) => {
  const logs = db.prepare('SELECT * FROM audit_logs ORDER BY created_at DESC').all();
  res.render('audit', { logs, user: req.session.user || null });
});

// Import (XSS-NODE-08: row_data with <%-)
app.get('/import', (req, res) => {
  const imports = db.prepare('SELECT * FROM imports ORDER BY imported_at DESC').all();
  res.render('import', { imports, user: req.session.user || null });
});

app.post('/import', (req, res) => {
  const { filename, row_data } = req.body;
  db.prepare('INSERT INTO imports (filename, row_data) VALUES (?, ?)').run(filename, row_data);
  res.redirect((req.headers['x-forwarded-prefix'] || '') + '/import');
});

// Help
app.get('/help', (req, res) => {
  res.render('help', { user: req.session.user || null });
});

// Campaign snippet routes — these URLs are stored in campaigns.snippet and
// rendered as links on the dashboard; they resolve to pages by slug.
app.get('/promotions/:slug', (req, res) => {
  const page = db.prepare('SELECT * FROM pages WHERE slug = ?').get(req.params.slug);
  if (!page) return res.status(404).send('Page not found');
  res.render('page', { page, user: req.session.user || null });
});

app.get('/campaigns/:slug', (req, res) => {
  const page = db.prepare('SELECT * FROM pages WHERE slug = ?').get(req.params.slug);
  if (!page) return res.status(404).send('Page not found');
  res.render('page', { page, user: req.session.user || null });
});

app.get('/referral/:slug', (req, res) => {
  const page = db.prepare('SELECT * FROM pages WHERE slug = ?').get(req.params.slug);
  if (!page) return res.status(404).send('Page not found');
  res.render('page', { page, user: req.session.user || null });
});

app.get('/events/:slug', (req, res) => {
  const page = db.prepare('SELECT * FROM pages WHERE slug = ?').get(req.params.slug);
  if (!page) return res.status(404).send('Page not found');
  res.render('page', { page, user: req.session.user || null });
});

app.listen(PORT, () => {
  console.log(`CMS Dashboard running on http://0.0.0.0:${PORT}`);
});
