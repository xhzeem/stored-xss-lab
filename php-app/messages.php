<?php
// PHP App - Messages Page
// Vulnerability: XSS-PHP-04 - Message subject rendered in title attribute of list item
// Difficulty: medium | Sink: title attribute | Stored field: subject
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle send message
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $stmt = $pdo->prepare("INSERT INTO private_messages (sender_name, receiver_name, subject, body) VALUES (?, ?, ?, ?)");
    $stmt->execute([
        $_POST['sender'] ?? 'user',
        $_POST['receiver'] ?? 'admin',
        $_POST['subject'] ?? '',
        $_POST['body'] ?? ''
    ]);
    $flash = 'Message sent!';
}

$pageTitle = 'Messages - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Messages</h1>

<?php
$messages = $pdo->query("SELECT * FROM private_messages ORDER BY created_at DESC")->fetchAll();
$users = $pdo->query("SELECT username FROM users")->fetchAll();
?>

<h2>Inbox</h2>
<?php foreach ($messages as $msg): ?>
    <!-- XSS-PHP-04: Message subject rendered in title attribute (stored XSS in attribute context) -->
    <div class="card" title="Subject: <?= $msg['subject'] ?>">
        <div class="post-meta">
            From <strong><?= h($msg['sender_name']) ?></strong> to <strong><?= h($msg['receiver_name']) ?></strong>
            on <?= h($msg['created_at']) ?>
        </div>
        <h3><?= h($msg['subject']) ?></h3>
        <p><?= h($msg['body']) ?></p>
    </div>
<?php endforeach; ?>

<div class="card">
    <h3>Send Message</h3>
    <form method="POST">
        <div class="form-group">
            <label>From</label>
            <select name="sender">
                <?php foreach ($users as $u): ?>
                <option value="<?= h($u['username']) ?>"><?= h($u['username']) ?></option>
                <?php endforeach; ?>
            </select>
        </div>
        <div class="form-group">
            <label>To</label>
            <select name="receiver">
                <?php foreach ($users as $u): ?>
                <option value="<?= h($u['username']) ?>"><?= h($u['username']) ?></option>
                <?php endforeach; ?>
            </select>
        </div>
        <div class="form-group">
            <label>Subject</label>
            <input type="text" name="subject" required>
        </div>
        <div class="form-group">
            <label>Body</label>
            <textarea name="body" required></textarea>
        </div>
        <button type="submit" class="btn">Send</button>
    </form>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
