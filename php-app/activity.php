<?php
// PHP App - Activity Log Page
// Vulnerability: XSS-PHP-06 - Activity link field rendered in href attribute
// Difficulty: medium | Sink: href attribute | Stored field: link
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle add activity
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $stmt = $pdo->prepare("INSERT INTO activity_logs (username, action, details, link) VALUES (?, ?, ?, ?)");
    $stmt->execute([
        $_POST['username'] ?? 'user',
        $_POST['action'] ?? 'custom',
        $_POST['details'] ?? '',
        $_POST['link'] ?? ''
    ]);
    $flash = 'Activity logged!';
}

$pageTitle = 'Activity Log - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Activity Log</h1>

<?php
$activities = $pdo->query("SELECT * FROM activity_logs ORDER BY created_at DESC")->fetchAll();
$users = $pdo->query("SELECT username FROM users")->fetchAll();
?>

<?php foreach ($activities as $act): ?>
<div class="card">
    <div class="post-meta">
        <strong><?= h($act['username']) ?></strong> performed <em><?= h($act['action']) ?></em>
        on <?= h($act['created_at']) ?>
    </div>
    <p><?= h($act['details']) ?></p>
    <?php if ($act['link']): ?>
        <!-- XSS-PHP-06: Activity link rendered in href (stored XSS in URL/attribute context) -->
        <a href="<?= $act['link'] ?>">View Details</a>
    <?php endif; ?>
</div>
<?php endforeach; ?>

<div class="card">
    <h3>Log Custom Activity</h3>
    <form method="POST">
        <div class="form-group">
            <label>Username</label>
            <select name="username">
                <?php foreach ($users as $u): ?>
                <option value="<?= h($u['username']) ?>"><?= h($u['username']) ?></option>
                <?php endforeach; ?>
            </select>
        </div>
        <div class="form-group">
            <label>Action</label>
            <input type="text" name="action" value="custom" required>
        </div>
        <div class="form-group">
            <label>Details</label>
            <textarea name="details" required></textarea>
        </div>
        <div class="form-group">
            <label>Link (URL)</label>
            <input type="text" name="link" placeholder="Optional link">
        </div>
        <button type="submit" class="btn">Log Activity</button>
    </form>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
