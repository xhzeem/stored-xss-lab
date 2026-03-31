<?php
// PHP App - Guestbook Page
// Vulnerability: XSS-PHP-02 - Guestbook message rendered as raw HTML
// Difficulty: easy | Sink: innerHTML in guestbook entry | Stored field: message
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle new guestbook entry
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $stmt = $pdo->prepare("INSERT INTO guestbook_entries (visitor_name, visitor_email, message) VALUES (?, ?, ?)");
    $stmt->execute([
        $_POST['visitor_name'] ?? 'Anonymous',
        $_POST['visitor_email'] ?? '',
        $_POST['message'] ?? ''
    ]);
    $flash = 'Thank you for signing the guestbook!';
}

$pageTitle = 'Guestbook - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Guestbook</h1>
<p>Leave a message in our guestbook!</p>

<?php
$entries = $pdo->query("SELECT * FROM guestbook_entries ORDER BY created_at DESC")->fetchAll();
foreach ($entries as $entry):
?>
<div class="card">
    <!-- XSS-PHP-02: Guestbook message rendered as raw HTML -->
    <div class="post-meta">
        From <strong><?= h($entry['visitor_name']) ?></strong>
        <?php if ($entry['visitor_email']): ?>(<?= h($entry['visitor_email']) ?>)<?php endif; ?>
        on <?= h($entry['created_at']) ?>
    </div>
    <div class="guestbook-message"><?= $entry['message'] ?></div>
</div>
<?php endforeach; ?>

<div class="card">
    <h3>Sign the Guestbook</h3>
    <form method="POST">
        <div class="form-group">
            <label>Your Name</label>
            <input type="text" name="visitor_name" required>
        </div>
        <div class="form-group">
            <label>Email (optional)</label>
            <input type="email" name="visitor_email">
        </div>
        <div class="form-group">
            <label>Message</label>
            <textarea name="message" required></textarea>
        </div>
        <button type="submit" class="btn">Sign Guestbook</button>
    </form>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
