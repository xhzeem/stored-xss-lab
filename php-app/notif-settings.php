<?php
// PHP App - Notification Settings Page
// Vulnerability: XSS-PHP-10 - Notification type label stored and rendered in aria-label attribute
//   Also rendered in option elements without escaping
// Difficulty: medium | Sink: aria-label attribute + option label | Stored field: label
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Create notification_prefs table
$pdo->exec("CREATE TABLE IF NOT EXISTS notification_prefs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    event_type TEXT NOT NULL,
    label TEXT NOT NULL,
    enabled INTEGER DEFAULT 1,
    channel TEXT DEFAULT 'email',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)");

// Handle add/update preference
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['add_pref'])) {
        $stmt = $pdo->prepare("INSERT INTO notification_prefs (username, event_type, label) VALUES (?, ?, ?)");
        $stmt->execute([
            $_POST['username'] ?? 'user',
            $_POST['event_type'] ?? 'custom',
            $_POST['label'] ?? ''
        ]);
        $flash = 'Preference added!';
    }
    if (isset($_POST['toggle_pref'])) {
        $stmt = $pdo->prepare("UPDATE notification_prefs SET enabled = ? WHERE id = ?");
        $stmt->execute([$_POST['enabled'] ?? 0, $_POST['pref_id']]);
        $flash = 'Preference updated!';
    }
}

// Seed defaults if empty
$cnt = $pdo->query("SELECT COUNT(*) FROM notification_prefs")->fetchColumn();
if ($cnt == 0) {
    $defaults = [
        ['user', 'comment', 'Someone comments on my post'],
        ['user', 'mention', 'I am mentioned in a post'],
        ['user', 'message', 'I receive a private message'],
        ['mod', 'report', 'A post is reported for review'],
        ['admin', 'signup', 'A new user registers'],
    ];
    $stmt = $pdo->prepare("INSERT INTO notification_prefs (username, event_type, label) VALUES (?, ?, ?)");
    foreach ($defaults as $d) $stmt->execute($d);
}

$pageTitle = 'Notification Settings - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Notification Preferences</h1>

<?php
$prefs = $pdo->query("SELECT * FROM notification_prefs ORDER BY username, event_type")->fetchAll();
$users = $pdo->query("SELECT username FROM users")->fetchAll();
?>

<table class="table">
    <thead><tr><th>User</th><th>Event</th><th>Label</th><th>Channel</th><th>Status</th><th>Action</th></tr></thead>
    <tbody>
    <?php foreach ($prefs as $pref): ?>
        <tr>
            <td><?= h($pref['username']) ?></td>
            <td><?= h($pref['event_type']) ?></td>
            <!-- XSS-PHP-10: Preference label rendered in aria-label attribute -->
            <td><span aria-label="Notification: <?= $pref['label'] ?>"><?= h($pref['label']) ?></span></td>
            <td><?= h($pref['channel']) ?></td>
            <td><?= $pref['enabled'] ? 'Enabled' : 'Disabled' ?></td>
            <td>
                <form method="POST" style="display:inline;">
                    <input type="hidden" name="toggle_pref" value="1">
                    <input type="hidden" name="pref_id" value="<?= $pref['id'] ?>">
                    <input type="hidden" name="enabled" value="<?= $pref['enabled'] ? 0 : 1 ?>">
                    <button type="submit" class="btn" style="font-size:0.8em;"><?= $pref['enabled'] ? 'Disable' : 'Enable' ?></button>
                </form>
            </td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>

<div class="card">
    <h3>Add Custom Notification Preference</h3>
    <form method="POST">
        <input type="hidden" name="add_pref" value="1">
        <div class="form-group">
            <label>Username</label>
            <select name="username">
                <?php foreach ($users as $u): ?>
                <option value="<?= h($u['username']) ?>"><?= h($u['username']) ?></option>
                <?php endforeach; ?>
            </select>
        </div>
        <div class="form-group">
            <label>Event Type</label>
            <input type="text" name="event_type" required>
        </div>
        <div class="form-group">
            <label>Label (description)</label>
            <!-- XSS-PHP-10b: Also an input that stores label -->
            <input type="text" name="label" required placeholder="Describe this notification...">
        </div>
        <button type="submit" class="btn">Add Preference</button>
    </form>
</div>

<!-- XSS-PHP-10c: Labels rendered in select option elements -->
<div class="card">
    <h3>Notification Summary</h3>
    <select>
        <?php foreach ($prefs as $pref): ?>
        <!-- Label rendered unsafely in option -->
        <option value="<?= $pref['id'] ?>"><?= $pref['label'] ?></option>
        <?php endforeach; ?>
    </select>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
