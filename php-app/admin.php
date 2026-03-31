<?php
// PHP App - Admin / Moderation Panel
// Vulnerability: XSS-PHP-07 - Post content rendered in data-content attribute
// Difficulty: hard | Sink: data-* attribute | Stored field: content
require_once __DIR__ . '/config.php';
$pdo = getPDO();

$pageTitle = 'Admin Panel - Community Forum';
require_once __DIR__ . '/templates/header.php';

$posts = $pdo->query("SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC")->fetchAll();
$users = $pdo->query("SELECT * FROM users")->fetchAll();
?>

<h1>Admin / Moderation Panel</h1>

<h2>All Posts (Moderation Queue)</h2>
<table class="table">
    <thead>
        <tr><th>ID</th><th>Title</th><th>Author</th><th>Status</th><th>Date</th><th>Preview</th></tr>
    </thead>
    <tbody>
    <?php foreach ($posts as $p): ?>
        <tr>
            <td><?= $p['id'] ?></td>
            <td><?= h($p['title']) ?></td>
            <td><?= h($p['username']) ?></td>
            <td><?= $p['is_approved'] ? 'Approved' : 'Pending' ?></td>
            <td><?= h($p['created_at']) ?></td>
            <!-- XSS-PHP-07: Post content in data-content attribute, later read by JS and inserted with innerHTML -->
            <td><button class="btn preview-btn" data-content="<?= $p['content'] ?>" data-title="<?= h($p['title']) ?>">Preview</button></td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<div id="preview-area" class="card" style="display:none;">
    <h3>Preview: <span id="preview-title"></span></h3>
    <div id="preview-content"></div>
</div>

<h2>User Management</h2>
<table class="table">
    <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Joined</th></tr></thead>
    <tbody>
    <?php foreach ($users as $u): ?>
        <tr>
            <td><?= $u['id'] ?></td>
            <td><?= h($u['username']) ?></td>
            <td><?= h($u['role']) ?></td>
            <td><?= h($u['created_at']) ?></td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<script>
document.querySelectorAll('.preview-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
        var content = this.getAttribute('data-content');
        var title = this.getAttribute('data-title');
        document.getElementById('preview-title').textContent = title;
        document.getElementById('preview-content').innerHTML = content;
        document.getElementById('preview-area').style.display = 'block';
    });
});
</script>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
