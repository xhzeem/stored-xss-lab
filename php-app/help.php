<?php
// PHP App - Help / About Page
require_once __DIR__ . '/config.php';
$pageTitle = 'Help - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<h1>Help & About</h1>

<div class="card">
    <h2>About This Forum</h2>
    <p>This is a community forum for discussing web security topics, CTF challenges, and vulnerability research.</p>
    <p>Features include:</p>
    <ul>
        <li>Forum posts with comments</li>
        <li>Public guestbook</li>
        <li>User profiles</li>
        <li>Private messaging</li>
        <li>Notification center</li>
        <li>Activity logging</li>
        <li>Search functionality</li>
        <li>Surveys and polls</li>
        <li>Notification preferences</li>
        <li>Admin moderation panel</li>
    </ul>
</div>

<div class="card">
    <h2>Demo Accounts</h2>
    <table class="table">
        <tr><th>Username</th><th>Password</th><th>Role</th></tr>
        <tr><td>user</td><td>user123</td><td>User</td></tr>
        <tr><td>mod</td><td>mod123</td><td>Moderator</td></tr>
        <tr><td>admin</td><td>admin123</td><td>Admin</td></tr>
    </table>
</div>

<div class="card">
    <h2>Navigation</h2>
    <ul>
        <li><a href="index.php">Dashboard</a> - Main overview</li>
        <li><a href="posts.php">Posts</a> - Forum discussions</li>
        <li><a href="guestbook.php">Guestbook</a> - Public messages</li>
        <li><a href="profile.php">Profiles</a> - User profiles</li>
        <li><a href="messages.php">Messages</a> - Private inbox</li>
        <li><a href="notifications.php">Notifications</a> - Alert center</li>
        <li><a href="activity.php">Activity</a> - Action log</li>
        <li><a href="admin.php">Admin</a> - Moderation panel</li>
        <li><a href="search.php">Search</a> - Find content</li>
        <li><a href="survey.php">Surveys</a> - Community surveys</li>
        <li><a href="notif-settings.php">Notif Settings</a> - Notification preferences</li>
        <li><a href="seed.php">Seed Info</a> - Database reset</li>
    </ul>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
