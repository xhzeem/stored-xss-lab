<?php
// PHP App - Posts Page
// Vulnerability: XSS-PHP-01 - Post content rendered as raw HTML
// Difficulty: easy | Sink: innerHTML in post body | Stored field: content
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Handle new post creation
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action']) && $_POST['action'] === 'create') {
    $stmt = $pdo->prepare("INSERT INTO posts (user_id, title, content) VALUES (?, ?, ?)");
    $stmt->execute([
        $_POST['user_id'] ?? 1,
        $_POST['title'] ?? '',
        $_POST['content'] ?? ''
    ]);
    $flash = 'Post created successfully!';
}

// Handle new comment
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action']) && $_POST['action'] === 'comment') {
    $stmt = $pdo->prepare("INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)");
    $stmt->execute([
        $_POST['post_id'] ?? 1,
        $_POST['user_id'] ?? 1,
        $_POST['comment'] ?? ''
    ]);
    $flash = 'Comment added!';
}

$users = $pdo->query("SELECT id, username FROM users")->fetchAll();

// Single post view
if (isset($_GET['id'])) {
    $stmt = $pdo->prepare("SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?");
    $stmt->execute([$_GET['id']]);
    $post = $stmt->fetch();

    if ($post) {
        $cstmt = $pdo->prepare("SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at");
        $cstmt->execute([$post['id']]);
        $comments = $cstmt->fetchAll();
    }
}

$pageTitle = 'Posts - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<?php if (isset($post)): ?>
    <!-- XSS-PHP-01: Post content rendered without escaping (stored XSS in HTML context) -->
    <div class="card">
        <h1><?= h($post['title']) ?></h1>
        <div class="post-meta">By <?= h($post['username']) ?> on <?= h($post['created_at']) ?></div>
        <div class="post-body"><?= $post['content'] ?></div>
    </div>

    <h2>Comments</h2>
    <?php foreach ($comments as $c): ?>
    <div class="card">
        <div class="post-meta"><?= h($c['username']) ?> on <?= h($c['created_at']) ?></div>
        <p><?= h($c['content']) ?></p>
    </div>
    <?php endforeach; ?>

    <div class="card">
        <h3>Add Comment</h3>
        <form method="POST">
            <input type="hidden" name="action" value="comment">
            <input type="hidden" name="post_id" value="<?= $post['id'] ?>">
            <input type="hidden" name="user_id" value="1">
            <div class="form-group">
                <label>Comment</label>
                <textarea name="comment" required></textarea>
            </div>
            <button type="submit" class="btn">Submit Comment</button>
        </form>
    </div>

    <p><a href="posts.php">&larr; Back to all posts</a></p>

<?php else: ?>
    <h1>Forum Posts</h1>
    <?php
    $posts = $pdo->query("SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC")->fetchAll();
    foreach ($posts as $p):
    ?>
    <div class="card">
        <h3><a href="posts.php?id=<?= $p['id'] ?>"><?= h($p['title']) ?></a></h3>
        <div class="post-meta">By <?= h($p['username']) ?> on <?= h($p['created_at']) ?></div>
        <p><?= h(substr($p['content'], 0, 200)) ?>...</p>
    </div>
    <?php endforeach; ?>

    <div class="card">
        <h3>Create New Post</h3>
        <form method="POST">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="user_id" value="1">
            <div class="form-group">
                <label>Title</label>
                <input type="text" name="title" required>
            </div>
            <div class="form-group">
                <label>Content</label>
                <textarea name="content" required></textarea>
            </div>
            <button type="submit" class="btn">Create Post</button>
        </form>
    </div>
<?php endif; ?>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
