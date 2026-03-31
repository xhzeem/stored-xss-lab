<?php
// PHP App - Login Page
// Optional login - does NOT gate any pages
require_once __DIR__ . '/config.php';
$pdo = getPDO();

$error = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';
    $stmt = $pdo->prepare("SELECT * FROM users WHERE username = ? AND password = ?");
    $stmt->execute([$username, $password]);
    $user = $stmt->fetch();
    if ($user) {
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['username'] = $user['username'];
        $_SESSION['role'] = $user['role'];
        header('Location: index.php');
        exit;
    } else {
        $error = 'Invalid credentials.';
    }
}

$pageTitle = 'Login - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<h1>Login</h1>
<?php if ($error): ?><div class="flash flash-error"><?= h($error) ?></div><?php endif; ?>

<div class="card">
    <form method="POST">
        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required>
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit" class="btn">Login</button>
    </form>
    <p style="margin-top:15px;">Demo accounts: user/user123, mod/mod123, admin/admin123</p>
</div>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
