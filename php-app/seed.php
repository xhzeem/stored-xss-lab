<?php
// PHP App - Database Seed Script
// Run: php seed.php  or visit /seed.php in browser
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Check if already seeded
$cnt = $pdo->query("SELECT COUNT(*) FROM users")->fetchColumn();
if ($cnt > 0) {
    echo "Database already seeded. Use ?reset=1 to reseed.\n";
    if (!isset($_GET['reset'])) exit;
    $pdo->exec("DELETE FROM notifications");
    $pdo->exec("DELETE FROM activity_logs");
    $pdo->exec("DELETE FROM private_messages");
    $pdo->exec("DELETE FROM guestbook_entries");
    $pdo->exec("DELETE FROM comments");
    $pdo->exec("DELETE FROM posts");
    $pdo->exec("DELETE FROM profiles");
    $pdo->exec("DELETE FROM users");
}

// Seed users (passwords stored in plain text intentionally for lab)
$users = [
    ['user', 'user123', 'user', 'Hi there! I am a regular user interested in web security.'],
    ['mod', 'mod123', 'moderator', 'Forum moderator. Keep discussions clean!'],
    ['admin', 'admin123', 'admin', 'Site administrator. Full access.'],
    ['alice', 'alice123', 'user', 'Security researcher and CTF enthusiast.'],
    ['bob', 'bob123', 'user', 'Junior developer learning about web vulnerabilities.'],
];

$stmt = $pdo->prepare("INSERT INTO users (username, password, role, bio) VALUES (?, ?, ?, ?)");
foreach ($users as $u) {
    $stmt->execute([$u[0], $u[1], $u[2], $u[3]]);
}

// Seed profiles
$pstmt = $pdo->prepare("INSERT INTO profiles (user_id, nickname, signature, website, location) VALUES (?, ?, ?, ?, ?)");
$pstmt->execute([1, 'RegularUser', 'Learning security one step at a time', 'https://example.com/user', 'San Francisco']);
$pstmt->execute([2, 'ModeratorMod', 'Keeping the forum safe', 'https://example.com/mod', 'New York']);
$pstmt->execute([3, 'AdminBoss', 'Running the show', 'https://example.com/admin', 'London']);
$pstmt->execute([4, 'AliceSec', 'Hack the planet!', 'https://example.com/alice', 'Berlin']);
$pstmt->execute([5, 'DevBob', 'console.log("hello world")', 'https://example.com/bob', 'Tokyo']);

// Seed posts
$ppstmt = $pdo->prepare("INSERT INTO posts (user_id, title, content, is_approved) VALUES (?, ?, ?, ?)");
$ppstmt->execute([1, 'Welcome to the Forum', 'This is a great community for learning about web security. Feel free to post your questions!', 1]);
$ppstmt->execute([4, 'CTF Writeup: Web Challenge', 'Here is my writeup for the recent CTF web challenge. The key was finding the injection point in the search parameter.', 1]);
$ppstmt->execute([5, 'Beginner Question about XSS', 'Can someone explain the difference between stored and reflected XSS? I keep getting confused.', 1]);
$ppstmt->execute([2, 'Forum Rules Update', 'Please remember to keep discussions on topic. No spam or offensive content.', 1]);
$ppstmt->execute([3, 'New Feature: Guestbook', 'We just added a guestbook feature. Check it out and leave a message!', 1]);

// Seed comments
$cstmt = $pdo->prepare("INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)");
$cstmt->execute([1, 2, 'Welcome everyone! Great to have new members.']);
$cstmt->execute([1, 4, 'Thanks for the warm welcome!']);
$cstmt->execute([2, 1, 'Great writeup Alice! Very detailed.']);
$cstmt->execute([3, 4, 'Stored XSS persists in the database, reflected XSS is in the URL parameter.']);
$cstmt->execute([3, 1, 'Good question Bob! Alice gave a great answer.']);

// Seed guestbook
$gstmt = $pdo->prepare("INSERT INTO guestbook_entries (visitor_name, visitor_email, message) VALUES (?, ?, ?)");
$gstmt->execute(['Charlie', 'charlie@example.com', 'Great forum! Love the community here.']);
$gstmt->execute(['Diana', 'diana@example.com', 'First time visiting. The resources here are amazing!']);
$gstmt->execute(['Eve', 'eve@example.com', 'Just finished the XSS challenges. Very educational.']);
$gstmt->execute(['Frank', 'frank@example.com', 'Can anyone recommend good security books?']);
$gstmt->execute(['Grace', 'grace@example.com', 'Thanks for creating this training environment!']);

// Seed messages
$mstmt = $pdo->prepare("INSERT INTO private_messages (sender_name, receiver_name, subject, body) VALUES (?, ?, ?, ?)");
$mstmt->execute(['alice', 'user', 'Re: Your Question', 'Hi! I saw your post about XSS. Happy to help explain further.']);
$mstmt->execute(['mod', 'user', 'Welcome Message', 'Welcome to the forum! Please read the rules.']);
$mstmt->execute(['admin', 'mod', 'Admin Task', 'Please review the pending posts in the moderation queue.']);
$mstmt->execute(['bob', 'alice', 'CTF Team', 'Want to join our CTF team for the upcoming event?']);
$mstmt->execute(['user', 'admin', 'Feature Request', 'Could we add a bookmarks feature to save interesting posts?']);

// Seed activity logs
$astmt = $pdo->prepare("INSERT INTO activity_logs (username, action, details, link) VALUES (?, ?, ?, ?)");
$astmt->execute(['user', 'login', 'User logged in from 127.0.0.1', '/php/index.php']);
$astmt->execute(['alice', 'post_created', 'Created post: CTF Writeup', '/php/posts.php?id=2']);
$astmt->execute(['bob', 'comment_added', 'Commented on: Beginner Question about XSS', '/php/posts.php?id=3']);
$astmt->execute(['mod', 'post_approved', 'Approved post: New Feature: Guestbook', '/php/posts.php?id=5']);
$astmt->execute(['admin', 'settings_changed', 'Updated forum settings', '/php/admin.php']);

// Seed notifications
$nstmt = $pdo->prepare("INSERT INTO notifications (username, type, message) VALUES (?, ?, ?)");
$nstmt->execute(['user', 'comment', 'alice commented on your post: Welcome to the Forum']);
$nstmt->execute(['alice', 'reply', 'user replied to your comment on CTF Writeup']);
$nstmt->execute(['mod', 'review', 'New post awaiting moderation: Testing New Features']);
$nstmt->execute(['user', 'message', 'You have a new message from mod']);
$nstmt->execute(['bob', 'mention', 'You were mentioned in a post by alice']);

echo "Database seeded successfully!\n";
echo "Users: user/user123, mod/mod123, admin/admin123, alice/alice123, bob/bob123\n";
