<?php
// PHP App - Dashboard / Home Page
require_once __DIR__ . '/config.php';
$pdo = getPDO();

$pageTitle = 'Dashboard - Community Forum';
require_once __DIR__ . '/templates/header.php';

$recentPosts = $pdo->query("SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC LIMIT 5")->fetchAll();
$recentGuestbook = $pdo->query("SELECT * FROM guestbook_entries ORDER BY created_at DESC LIMIT 3")->fetchAll();
$stats = [
    'users' => $pdo->query("SELECT COUNT(*) FROM users")->fetchColumn(),
    'posts' => $pdo->query("SELECT COUNT(*) FROM posts")->fetchColumn(),
    'comments' => $pdo->query("SELECT COUNT(*) FROM comments")->fetchColumn(),
    'guestbook' => $pdo->query("SELECT COUNT(*) FROM guestbook_entries")->fetchColumn(),
];
?>

<h1>Community Forum Dashboard</h1>

<div class="grid">
    <div class="card stat-card"><div class="number"><?= $stats['users'] ?></div><div>Users</div></div>
    <div class="card stat-card"><div class="number"><?= $stats['posts'] ?></div><div>Posts</div></div>
    <div class="card stat-card"><div class="number"><?= $stats['comments'] ?></div><div>Comments</div></div>
    <div class="card stat-card"><div class="number"><?= $stats['guestbook'] ?></div><div>Guestbook</div></div>
</div>

<h2>Recent Posts</h2>
<?php foreach ($recentPosts as $post): ?>
<div class="card">
    <h3><a href="posts.php?id=<?= $post['id'] ?>"><?= h($post['title']) ?></a></h3>
    <div class="post-meta">By <?= h($post['username']) ?> on <?= h($post['created_at']) ?></div>
    <p><?= h(substr($post['content'], 0, 150)) ?>...</p>
</div>
<?php endforeach; ?>

<h2>Recent Guestbook Entries</h2>
<?php foreach ($recentGuestbook as $entry): ?>
<div class="card">
    <div class="post-meta">From <?= h($entry['visitor_name']) ?> on <?= h($entry['created_at']) ?></div>
    <p><?= h($entry['message']) ?></p>
</div>
<?php endforeach; ?>

<h2>Quick Links</h2>
<div class="card">
    <ul>
        <li><a href="posts.php">All Posts</a></li>
        <li><a href="guestbook.php">Guestbook</a></li>
        <li><a href="messages.php">Messages</a></li>
        <li><a href="notifications.php">Notifications</a></li>
        <li><a href="activity.php">Activity Log</a></li>
        <li><a href="search.php">Search</a></li>
        <li><a href="survey.php">Surveys</a></li>
        <li><a href="notif-settings.php">Notification Settings</a></li>
        <li><a href="admin.php">Admin Panel</a></li>
        <li><a href="help.php">Help</a></li>
        <li><a href="seed.php">Seed / Reset Info</a></li>
    </ul>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
