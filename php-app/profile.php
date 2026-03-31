<?php
// PHP App - Profile Page
// Vulnerability: XSS-PHP-03 - Profile signature rendered in title attribute
// Difficulty: medium | Sink: title attribute | Stored field: signature
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle profile update
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userId = $_POST['user_id'] ?? 1;
    $stmt = $pdo->prepare("INSERT OR REPLACE INTO profiles (user_id, nickname, signature, website, location) VALUES (?, ?, ?, ?, ?)");
    $stmt->execute([
        $userId,
        $_POST['nickname'] ?? '',
        $_POST['signature'] ?? '',
        $_POST['website'] ?? '',
        $_POST['location'] ?? ''
    ]);
    $flash = 'Profile updated!';
}

$pageTitle = 'Profiles - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>User Profiles</h1>

<?php
$profiles = $pdo->query("SELECT p.*, u.username FROM profiles p JOIN users u ON p.user_id = u.id ORDER BY u.username")->fetchAll();
$users = $pdo->query("SELECT id, username FROM users")->fetchAll();
?>

<div class="grid">
<?php foreach ($profiles as $profile): ?>
    <!-- XSS-PHP-03: Profile signature rendered in title attribute (stored XSS in attribute context) -->
    <div class="card" title="Signature: <?= $profile['signature'] ?>">
        <h3><?= h($profile['username']) ?></h3>
        <p><strong>Nickname:</strong> <?= h($profile['nickname']) ?></p>
        <p><strong>Location:</strong> <?= h($profile['location']) ?></p>
        <p><strong>Website:</strong> <?= h($profile['website']) ?></p>
    </div>
<?php endforeach; ?>
</div>

<div class="card">
    <h3>Edit Profile</h3>
    <form method="POST">
        <div class="form-group">
            <label>User</label>
            <select name="user_id">
                <?php foreach ($users as $u): ?>
                <option value="<?= $u['id'] ?>"><?= h($u['username']) ?></option>
                <?php endforeach; ?>
            </select>
        </div>
        <div class="form-group">
            <label>Nickname</label>
            <input type="text" name="nickname">
        </div>
        <div class="form-group">
            <label>Signature</label>
            <textarea name="signature" placeholder="Your signature..."></textarea>
        </div>
        <div class="form-group">
            <label>Website</label>
            <input type="text" name="website">
        </div>
        <div class="form-group">
            <label>Location</label>
            <input type="text" name="location">
        </div>
        <button type="submit" class="btn">Update Profile</button>
    </form>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
