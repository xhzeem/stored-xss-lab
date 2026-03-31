package main

import (
	"database/sql"
	"html/template"
	"log"
	"net/http"
	"strings"
	"time"

	"github.com/gin-contrib/sessions"
	"github.com/gin-contrib/sessions/cookie"
	"github.com/gin-gonic/gin"
	_ "modernc.org/sqlite"
)

var db *sql.DB

// PageData holds common template data
type PageData struct {
	Title       string
	CurrentUser string
	LoggedIn    bool
	Flash       string
	Extra       map[string]interface{}
}

func initDB() {
	var err error
	db, err = sql.Open("sqlite", "./lab.db")
	if err != nil {
		log.Fatal(err)
	}
	db.SetMaxOpenConns(1)

	createTables()
	seedData()
}

func createTables() {
	stmts := []string{
		`CREATE TABLE IF NOT EXISTS users (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			username TEXT UNIQUE NOT NULL,
			password TEXT NOT NULL,
			role TEXT NOT NULL DEFAULT 'user'
		)`,
		`CREATE TABLE IF NOT EXISTS tickets (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			user_id INTEGER,
			subject TEXT NOT NULL,
			description TEXT NOT NULL,
			status TEXT DEFAULT 'open',
			priority TEXT DEFAULT 'normal',
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS ticket_replies (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			ticket_id INTEGER NOT NULL,
			user_id INTEGER,
			content TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS chat_messages (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			username TEXT NOT NULL,
			message TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS notifications (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			username TEXT NOT NULL,
			type TEXT NOT NULL,
			message TEXT NOT NULL,
			is_read INTEGER DEFAULT 0,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS csv_imports (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			filename TEXT NOT NULL,
			row_data TEXT NOT NULL,
			imported_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS memos (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			author TEXT NOT NULL,
			title TEXT NOT NULL,
			content TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS releases (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			version TEXT NOT NULL,
			notes TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS saved_searches (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			username TEXT NOT NULL,
			name TEXT NOT NULL,
			query TEXT NOT NULL,
			created_at DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
	}
	for _, s := range stmts {
		if _, err := db.Exec(s); err != nil {
			log.Fatal("create table error: ", err)
		}
	}
}

func seedData() {
	var count int
	db.QueryRow("SELECT COUNT(*) FROM users").Scan(&count)
	if count > 0 {
		return
	}

	log.Println("Seeding database...")

	// Users
	users := []struct{ u, p, r string }{
		{"user", "user123", "user"},
		{"mod", "mod123", "moderator"},
		{"admin", "admin123", "admin"},
		{"support1", "pass123", "support"},
		{"support2", "pass123", "support"},
	}
	for _, u := range users {
		db.Exec("INSERT INTO users (username,password,role) VALUES (?,?,?)", u.u, u.p, u.r)
	}

	// Tickets
	tickets := []struct {
		uid                     int
		subj, desc, status, pri string
	}{
		{1, "Cannot access VPN", "I'm getting a timeout error when trying to connect to the corporate VPN from my home office. I've tried restarting my router but it still fails.", "open", "high"},
		{1, "Email not syncing on mobile", "My Outlook stopped syncing emails on my iPhone since yesterday morning. Calendar and contacts work fine.", "open", "normal"},
		{2, "Request for new monitor", "I need a 27-inch monitor for the design work. My current 22-inch is too small for the CAD software.", "pending", "low"},
		{3, "Printer offline in Building B", "The shared printer on the 3rd floor of Building B shows as offline. Multiple users are affected.", "open", "normal"},
		{4, "Software license expired", "Our Adobe Creative Cloud license expired today and several designers cannot open their projects.", "open", "high"},
		{5, "Password reset not working", "I requested a password reset 30 minutes ago but never received the email. Checked spam folder too.", "closed", "normal"},
	}
	for _, t := range tickets {
		db.Exec("INSERT INTO tickets (user_id,subject,description,status,priority) VALUES (?,?,?,?,?)",
			t.uid, t.subj, t.desc, t.status, t.pri)
	}

	// Ticket replies
	replies := []struct {
		tid, uid int
		content  string
	}{
		{1, 4, "Have you tried switching between WiFi and ethernet? Sometimes the ISP blocks VPN ports."},
		{1, 5, "Please check if your firewall is allowing UDP port 1194. That's the default OpenVPN port."},
		{2, 4, "Please try removing and re-adding your email account in Outlook settings."},
		{4, 5, "I've contacted the printer vendor. They will send a technician tomorrow morning."},
		{5, 3, "The license has been renewed. Please restart your Creative Cloud app to pick up the new license."},
		{6, 4, "The password reset email should have been sent. Let me manually reset it for you."},
	}
	for _, r := range replies {
		db.Exec("INSERT INTO ticket_replies (ticket_id,user_id,content) VALUES (?,?,?)", r.tid, r.uid, r.content)
	}

	// Chat messages
	chats := []struct{ user, msg string }{
		{"support1", "Good morning team! Ready to tackle tickets."},
		{"admin", "Reminder: server maintenance tonight at 11 PM EST."},
		{"support2", "Has anyone seen the new VPN config document?"},
		{"mod", "I've updated the FAQ page with the latest info."},
		{"support1", "The printer issue in Building B is resolved now."},
		{"admin", "Please remember to update your credentials before the audit."},
		{"support2", "Ticket #5 needs urgent attention - license expired."},
	}
	for _, c := range chats {
		db.Exec("INSERT INTO chat_messages (username,message) VALUES (?,?)", c.user, c.msg)
	}

	// Notifications
	notifs := []struct{ user, ntype, msg string }{
		{"user", "ticket_update", "Your ticket #1 has been replied to by support1."},
		{"user", "ticket_closed", "Ticket #6 has been marked as resolved."},
		{"mod", "escalation", "Ticket #5 has been escalated due to high priority."},
		{"admin", "system", "Server maintenance scheduled for tonight at 11 PM."},
		{"support1", "assignment", "You have been assigned to ticket #4."},
		{"support2", "mention", "admin mentioned you in the chat regarding audit prep."},
		{"user", "reminder", "Please submit your weekly timesheet by Friday."},
	}
	for _, n := range notifs {
		db.Exec("INSERT INTO notifications (username,type,message) VALUES (?,?,?)", n.user, n.ntype, n.msg)
	}

	// CSV imports
	csvRows := []struct{ fn, row string }{
		{"employees.csv", "John Doe,Engineering,jdoe@corp.com,555-0101"},
		{"employees.csv", "Jane Smith,Marketing,jsmith@corp.com,555-0102"},
		{"employees.csv", "Bob Wilson,Sales,bwilson@corp.com,555-0103"},
		{"inventory.csv", "Laptop,ThinkPad X1,1200,IT-001"},
		{"inventory.csv", "Monitor,Dell U2723QE,650,IT-002"},
		{"inventory.csv", "Keyboard,Logitech MX Keys,120,IT-003"},
	}
	for _, c := range csvRows {
		db.Exec("INSERT INTO csv_imports (filename,row_data) VALUES (?,?)", c.fn, c.row)
	}

	// Memos
	memos := []struct{ author, title, content string }{
		{"admin", "Q4 Budget Review", "Please review the Q4 IT budget allocations and submit feedback by end of week. Key areas: hardware, software licenses, and cloud services."},
		{"admin", "New Hire Onboarding", "Updated onboarding checklist for IT support staff. All new hires must complete security awareness training within their first week."},
		{"mod", "Policy Update: Remote Work", "Effective next month, all remote workers must use the corporate VPN for accessing internal resources. Personal devices need MDM enrollment."},
		{"support1", "Printer Maintenance Schedule", "Building A printers: maintenance every Tuesday. Building B: every Thursday. Please plan your printing accordingly."},
		{"admin", "Security Audit Preparation", "Annual security audit is in 3 weeks. Ensure all access logs are up to date and unused accounts are disabled."},
		{"support2", "Common Issues This Week", "Top issues: VPN timeouts, Outlook sync failures, and printer offline errors. Check the FAQ before creating new tickets."},
	}
	for _, m := range memos {
		db.Exec("INSERT INTO memos (author,title,content) VALUES (?,?,?)", m.author, m.title, m.content)
	}

	// Releases
	releases := []struct{ ver, notes string }{
		{"v2.5.0", "Added CSV bulk import feature for employee onboarding. Improved ticket search with filters. Fixed email notification delays."},
		{"v2.4.1", "Security patch: updated TLS configuration. Fixed session timeout handling. Performance improvements for dashboard loading."},
		{"v2.4.0", "New chat module for real-time team communication. Added notification center with read/unread tracking. Redesigned admin panel."},
		{"v2.3.2", "Bug fixes: ticket reply timestamps, mobile responsive layout issues, and PDF export formatting."},
		{"v2.3.0", "Introduced saved searches feature. Added priority levels to tickets. New memo board for internal communications."},
		{"v2.2.0", "Initial release of the support desk platform. Core features: ticket management, user roles, and basic reporting."},
	}
	for _, r := range releases {
		db.Exec("INSERT INTO releases (version,notes) VALUES (?,?)", r.ver, r.notes)
	}

	// Saved searches
	searches := []struct{ user, name, query string }{
		{"admin", "All Open Tickets", "status:open"},
		{"admin", "High Priority", "priority:high status:open"},
		{"support1", "My Assigned Tickets", "assigned:support1"},
		{"support2", "VPN Related", "subject:vpn"},
		{"mod", "Pending Review", "status:pending"},
		{"user", "My Tickets", "user:user"},
		{"admin", "Recent Closures", "status:closed sort:date"},
		{"support1", "Printer Issues", "subject:printer"},
	}
	for _, s := range searches {
		db.Exec("INSERT INTO saved_searches (username,name,query) VALUES (?,?,?)", s.user, s.name, s.query)
	}

	log.Println("Database seeded successfully.")
}

func getPrefix(c *gin.Context) string {
	return c.GetHeader("X-Forwarded-Prefix")
}

// escapeJS escapes a string for safe embedding in a Go template script context.
// This prevents XSS via the description field in inline <script> blocks.
func escapeJS(s string) string {
	s = strings.ReplaceAll(s, `\`, `\\`)
	s = strings.ReplaceAll(s, `'`, `\'`)
	s = strings.ReplaceAll(s, "\n", `\n`)
	s = strings.ReplaceAll(s, "\r", `\r`)
	return s
}

func main() {
	initDB()

	r := gin.Default()

	// Session store (for optional login display only, NOT for auth gating)
	store := cookie.NewStore([]byte("xss-lab-secret-key-do-not-use-in-prod"))
	r.Use(sessions.Sessions("session", store))

	r.SetFuncMap(template.FuncMap{
		"safeHTML": func(s string) template.HTML { return template.HTML(s) },
		"escapeJS": escapeJS,
		"now":      func() string { return time.Now().Format("2006-01-02 15:04:05") },
	})

	r.LoadHTMLGlob("templates/*.html")
	r.Static("/static", "templates/static")

	// Routes
	r.GET("/", handleIndex)
	r.GET("/health", handleHealth)
	r.GET("/login", handleLoginGET)
	r.POST("/login", handleLoginPOST)
	r.GET("/logout", handleLogout)
	r.GET("/tickets", handleTicketsGET)
	r.POST("/tickets", handleTicketsPOST)
	r.GET("/ticket/:id", handleTicketDetail)
	r.GET("/chat", handleChatGET)
	r.POST("/chat", handleChatPOST)
	r.GET("/notifications", handleNotificationsGET)
	r.GET("/admin", handleAdminGET)
	r.GET("/csv-import", handleCSVImportGET)
	r.POST("/csv-import", handleCSVImportPOST)
	r.GET("/memos", handleMemosGET)
	r.POST("/memos", handleMemosPOST)
	r.GET("/releases", handleReleasesGET)
	r.POST("/releases", handleReleasesPOST)
	r.GET("/search", handleSearchGET)
	r.POST("/search", handleSearchPOST)
	r.GET("/help", handleHelp)

	log.Println("Stored XSS Lab running on :8080")
	r.Run(":8080")
}

// --- Handlers ---

func handleHealth(c *gin.Context) {
	c.JSON(200, gin.H{"status": "ok"})
}

func handleIndex(c *gin.Context) {
	/*
	 * XSS-GO-10: Dashboard
	 * Difficulty: medium
	 * Sink: <option> element
	 * Stored field: type (notifications table)
	 * Vector: notification type rendered unescaped in <option> elements
	 */
	sess := sessions.Default(c)
	user := sess.Get("username")

	type Row struct {
		Username, Type, Message, CreatedAt string
	}
	var notifs []Row
	rows, _ := db.Query("SELECT username, type, message, created_at FROM notifications ORDER BY created_at DESC LIMIT 10")
	defer rows.Close()
	for rows.Next() {
		var n Row
		rows.Scan(&n.Username, &n.Type, &n.Message, &n.CreatedAt)
		notifs = append(notifs, n)
	}

	var ticketCount, openCount, chatCount int
	db.QueryRow("SELECT COUNT(*) FROM tickets").Scan(&ticketCount)
	db.QueryRow("SELECT COUNT(*) FROM tickets WHERE status='open'").Scan(&openCount)
	db.QueryRow("SELECT COUNT(*) FROM chat_messages").Scan(&chatCount)

	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "index.html", gin.H{
		"Title":         "Dashboard",
		"Prefix":        getPrefix(c),
		"CurrentUser":   currentUser,
		"LoggedIn":      loggedIn,
		"TicketCount":   ticketCount,
		"OpenCount":     openCount,
		"ChatCount":     chatCount,
		"Notifications": notifs,
	})
}

func handleLoginGET(c *gin.Context) {
	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}
	c.HTML(http.StatusOK, "login.html", gin.H{
		"Title": "Login", "Prefix": getPrefix(c), "CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleLoginPOST(c *gin.Context) {
	username := c.PostForm("username")
	password := c.PostForm("password")

	var dbPass string
	err := db.QueryRow("SELECT password FROM users WHERE username=?", username).Scan(&dbPass)
	if err == nil && dbPass == password {
		sess := sessions.Default(c)
		sess.Set("username", username)
		sess.Save()
		c.Redirect(http.StatusFound, "/")
	} else {
		c.HTML(http.StatusOK, "login.html", gin.H{
			"Title": "Login", "Prefix": getPrefix(c), "Flash": "Invalid credentials",
		})
	}
}

func handleLogout(c *gin.Context) {
	sess := sessions.Default(c)
	sess.Clear()
	sess.Save()
	c.Redirect(http.StatusFound, "/")
}

func handleTicketsGET(c *gin.Context) {
	type Ticket struct {
		ID, Subject, Description, Status, Priority, CreatedAt string
	}
	var tickets []Ticket
	rows, _ := db.Query("SELECT id, subject, description, status, priority, created_at FROM tickets ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var t Ticket
		rows.Scan(&t.ID, &t.Subject, &t.Description, &t.Status, &t.Priority, &t.CreatedAt)
		tickets = append(tickets, t)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "tickets.html", gin.H{
		"Title": "Tickets", "Prefix": getPrefix(c), "Tickets": tickets,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleTicketsPOST(c *gin.Context) {
	subject := c.PostForm("subject")
	description := c.PostForm("description")
	priority := c.PostForm("priority")
	if priority == "" {
		priority = "normal"
	}
	db.Exec("INSERT INTO tickets (subject,description,status,priority) VALUES (?,?,?,?)",
		subject, description, "open", priority)
	c.Redirect(http.StatusFound, "/tickets")
}

func handleTicketDetail(c *gin.Context) {
	/*
	 * XSS-GO-09: Ticket Detail - Inline Script
	 * Difficulty: hard
	 * Sink: inline <script> string
	 * Stored field: description (tickets table)
	 * Vector: ticket description inserted into JS string via escapeJS
	 */
	id := c.Param("id")

	type Ticket struct {
		ID, UserID, Subject, Description, Status, Priority, CreatedAt string
	}
	var ticket Ticket
	err := db.QueryRow("SELECT id, COALESCE(user_id,0), subject, description, status, priority, created_at FROM tickets WHERE id=?", id).
		Scan(&ticket.ID, &ticket.UserID, &ticket.Subject, &ticket.Description, &ticket.Status, &ticket.Priority, &ticket.CreatedAt)
	if err != nil {
		c.String(404, "Ticket not found")
		return
	}

	type Reply struct {
		ID, TicketID, UserID, Content, CreatedAt string
		ContentHTML                              template.HTML
	}
	var replies []Reply
	rows, _ := db.Query("SELECT id, ticket_id, COALESCE(user_id,0), content, created_at FROM ticket_replies WHERE ticket_id=? ORDER BY created_at", id)
	defer rows.Close()
	for rows.Next() {
		var r Reply
		rows.Scan(&r.ID, &r.TicketID, &r.UserID, &r.Content, &r.CreatedAt)
		r.ContentHTML = template.HTML(r.Content)
		replies = append(replies, r)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "ticket.html", gin.H{
		"Title": "Ticket #" + id, "Prefix": getPrefix(c), "Ticket": ticket, "Replies": replies,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
		"EscapedDesc": escapeJS(ticket.Description),
	})
}

func handleChatGET(c *gin.Context) {
	/*
	 * XSS-GO-02: Chat
	 * Difficulty: easy
	 * Sink: innerHTML (raw HTML body)
	 * Stored field: message (chat_messages table)
	 * Vector: chat message rendered as raw HTML via template.HTML
	 */
	type Msg struct {
		ID, Username, Message, CreatedAt string
		MessageHTML                      template.HTML
	}
	var messages []Msg
	rows, _ := db.Query("SELECT id, username, message, created_at FROM chat_messages ORDER BY created_at")
	defer rows.Close()
	for rows.Next() {
		var m Msg
		rows.Scan(&m.ID, &m.Username, &m.Message, &m.CreatedAt)
		m.MessageHTML = template.HTML(m.Message)
		messages = append(messages, m)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "chat.html", gin.H{
		"Title": "Live Chat", "Prefix": getPrefix(c), "Messages": messages,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleChatPOST(c *gin.Context) {
	username := c.PostForm("username")
	message := c.PostForm("message")
	if username == "" {
		username = "anonymous"
	}
	db.Exec("INSERT INTO chat_messages (username,message) VALUES (?,?)", username, message)
	c.Redirect(http.StatusFound, "/chat")
}

func handleNotificationsGET(c *gin.Context) {
	/*
	 * XSS-GO-03: Notifications
	 * Difficulty: medium
	 * Sink: title attribute
	 * Stored field: message (notifications table)
	 * Vector: notification message rendered inside title="..." attribute
	 */
	type Notif struct {
		ID, Username, Type, Message, CreatedAt string
		IsRead                                 int
	}
	var notifs []Notif
	rows, _ := db.Query("SELECT id, username, type, message, is_read, created_at FROM notifications ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var n Notif
		rows.Scan(&n.ID, &n.Username, &n.Type, &n.Message, &n.IsRead, &n.CreatedAt)
		notifs = append(notifs, n)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "notifications.html", gin.H{
		"Title": "Notifications", "Prefix": getPrefix(c), "Notifications": notifs,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleAdminGET(c *gin.Context) {
	/*
	 * XSS-GO-04: Admin Queue
	 * Difficulty: medium
	 * Sink: data-subject attribute + JavaScript eval
	 * Stored field: subject (tickets table)
	 * Vector: ticket subject rendered in data-subject, then read by JS
	 */
	type Ticket struct {
		ID, Subject, Description, Status, Priority, CreatedAt string
	}
	var tickets []Ticket
	rows, _ := db.Query("SELECT id, subject, description, status, priority, created_at FROM tickets ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var t Ticket
		rows.Scan(&t.ID, &t.Subject, &t.Description, &t.Status, &t.Priority, &t.CreatedAt)
		tickets = append(tickets, t)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "admin.html", gin.H{
		"Title": "Admin Queue", "Prefix": getPrefix(c), "Tickets": tickets,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleCSVImportGET(c *gin.Context) {
	/*
	 * XSS-GO-05: CSV Import
	 * Difficulty: hard
	 * Sink: HTML body
	 * Stored field: row_data (csv_imports table)
	 * Vector: imported CSV cell rendered as raw HTML in results
	 */
	type Import struct {
		ID, Filename, RowData, ImportedAt string
		RowHTML                           template.HTML
	}
	var imports []Import
	rows, _ := db.Query("SELECT id, filename, row_data, imported_at FROM csv_imports ORDER BY imported_at DESC")
	defer rows.Close()
	for rows.Next() {
		var imp Import
		rows.Scan(&imp.ID, &imp.Filename, &imp.RowData, &imp.ImportedAt)
		imp.RowHTML = template.HTML(imp.RowData)
		imports = append(imports, imp)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "csv-import.html", gin.H{
		"Title": "CSV Import", "Prefix": getPrefix(c), "Imports": imports,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleCSVImportPOST(c *gin.Context) {
	filename := c.PostForm("filename")
	csvdata := c.PostForm("csvdata")
	if filename == "" {
		filename = "upload.csv"
	}
	lines := strings.Split(csvdata, "\n")
	for _, line := range lines {
		line = strings.TrimSpace(line)
		if line != "" {
			db.Exec("INSERT INTO csv_imports (filename,row_data) VALUES (?,?)", filename, line)
		}
	}
	c.Redirect(http.StatusFound, "/csv-import")
}

func handleMemosGET(c *gin.Context) {
	/*
	 * XSS-GO-06: Memos
	 * Difficulty: medium
	 * Sink: innerHTML (raw HTML body)
	 * Stored field: content (memos table)
	 * Vector: memo content rendered as raw HTML via template.HTML
	 */
	type Memo struct {
		ID, Author, Title, Content, CreatedAt string
		ContentHTML                           template.HTML
	}
	var memos []Memo
	rows, _ := db.Query("SELECT id, author, title, content, created_at FROM memos ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var m Memo
		rows.Scan(&m.ID, &m.Author, &m.Title, &m.Content, &m.CreatedAt)
		m.ContentHTML = template.HTML(m.Content)
		memos = append(memos, m)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "memos.html", gin.H{
		"Title": "Memos", "Prefix": getPrefix(c), "Memos": memos,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleMemosPOST(c *gin.Context) {
	author := c.PostForm("author")
	title := c.PostForm("title")
	content := c.PostForm("content")
	if author == "" {
		author = "anonymous"
	}
	db.Exec("INSERT INTO memos (author,title,content) VALUES (?,?,?)", author, title, content)
	c.Redirect(http.StatusFound, "/memos")
}

func handleReleasesGET(c *gin.Context) {
	/*
	 * XSS-GO-07: Release Notes
	 * Difficulty: hard
	 * Sink: HTML preview
	 * Stored field: notes (releases table)
	 * Vector: release notes rendered as raw HTML in preview pane
	 */
	type Release struct {
		ID, Version, Notes, CreatedAt string
		NotesHTML                     template.HTML
	}
	var releases []Release
	rows, _ := db.Query("SELECT id, version, notes, created_at FROM releases ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var r Release
		rows.Scan(&r.ID, &r.Version, &r.Notes, &r.CreatedAt)
		r.NotesHTML = template.HTML(r.Notes)
		releases = append(releases, r)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "releases.html", gin.H{
		"Title": "Releases", "Prefix": getPrefix(c), "Releases": releases,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleReleasesPOST(c *gin.Context) {
	version := c.PostForm("version")
	notes := c.PostForm("notes")
	db.Exec("INSERT INTO releases (version,notes) VALUES (?,?)", version, notes)
	c.Redirect(http.StatusFound, "/releases")
}

func handleSearchGET(c *gin.Context) {
	/*
	 * XSS-GO-08: Saved Searches
	 * Difficulty: medium
	 * Sink: aria-label attribute
	 * Stored field: name (saved_searches table)
	 * Vector: saved search name rendered in aria-label="..."
	 */
	type Search struct {
		ID, Username, Name, Query, CreatedAt string
	}
	var searches []Search
	rows, _ := db.Query("SELECT id, username, name, query, created_at FROM saved_searches ORDER BY created_at DESC")
	defer rows.Close()
	for rows.Next() {
		var s Search
		rows.Scan(&s.ID, &s.Username, &s.Name, &s.Query, &s.CreatedAt)
		searches = append(searches, s)
	}

	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}

	c.HTML(http.StatusOK, "search.html", gin.H{
		"Title": "Saved Searches", "Prefix": getPrefix(c), "Searches": searches,
		"CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}

func handleSearchPOST(c *gin.Context) {
	username := c.PostForm("username")
	name := c.PostForm("name")
	query := c.PostForm("query")
	if username == "" {
		username = "anonymous"
	}
	db.Exec("INSERT INTO saved_searches (username,name,query) VALUES (?,?,?)", username, name, query)
	c.Redirect(http.StatusFound, "/search")
}

func handleHelp(c *gin.Context) {
	sess := sessions.Default(c)
	user := sess.Get("username")
	loggedIn := user != nil && user.(string) != ""
	currentUser := ""
	if loggedIn {
		currentUser = user.(string)
	}
	c.HTML(http.StatusOK, "help.html", gin.H{
		"Title": "Help", "Prefix": getPrefix(c), "CurrentUser": currentUser, "LoggedIn": loggedIn,
	})
}
