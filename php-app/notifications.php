<?php
// PHP App - Notifications Page
// Vulnerability: XSS-PHP-05 - Notification message embedded in inline JSON script tag
// Difficulty: medium | Sink: JSON inside <script> tag | Stored field: message
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle mark as read
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['mark_read'])) {
    $stmt = $pdo->prepare("UPDATE notifications SET is_read = 1 WHERE id = ?");
    $stmt->execute([$_POST['notification_id']]);
    $flash = 'Marked as read.';
}

$pageTitle = 'Notifications - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Notification Center</h1>

<?php
$notifications = $pdo->query("SELECT * FROM notifications ORDER BY created_at DESC")->fetchAll();
?>

<!-- XSS-PHP-05: Notification messages embedded in JSON blob inside script tag -->
<script>
var notificationData = <?= json_encode($notifications) ?>;
var unreadCount = <?= count(array_filter($notifications, function($n) { return !$n['is_read']; })) ?>;
document.addEventListener('DOMContentLoaded', function() {
    var header = document.querySelector('h1');
    header.textContent = 'Notification Center (' + unreadCount + ' unread)';
});
</script>

<?php foreach ($notifications as $n): ?>
<div class="card" style="border-left: 4px solid <?= $n['is_read'] ? '#ccc' : '#3498db' ?>;">
    <div class="post-meta">
        <span class="badge badge-<?= $n['type'] === 'comment' ? 'easy' : ($n['type'] === 'review' ? 'hard' : 'medium') ?>">
            <?= h($n['type']) ?>
        </span>
        for <strong><?= h($n['username']) ?></strong>
        on <?= h($n['created_at']) ?>
        <?= $n['is_read'] ? '(read)' : '(unread)' ?>
    </div>
    <!-- XSS-PHP-05b: Notification message rendered as HTML in the page body too -->
    <p><?= $n['message'] ?></p>
    <?php if (!$n['is_read']): ?>
    <form method="POST" style="display:inline;">
        <input type="hidden" name="mark_read" value="1">
        <input type="hidden" name="notification_id" value="<?= $n['id'] ?>">
        <button type="submit" class="btn" style="font-size:0.8em;">Mark as Read</button>
    </form>
    <?php endif; ?>
</div>
<?php endforeach; ?>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
