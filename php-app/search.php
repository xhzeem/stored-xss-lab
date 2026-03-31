<?php
// PHP App - Search Page
// Vulnerability: XSS-PHP-08 - Search query stored and rendered in hidden input value
//   then reflected back in results via value attribute
// Difficulty: hard | Sink: hidden input value + value reflection | Stored field: last search query in session
require_once __DIR__ . '/config.php';
$pdo = getPDO();

$results = [];
$searchQuery = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST' || isset($_GET['q'])) {
    $searchQuery = $_POST['q'] ?? $_GET['q'] ?? '';
    // Store last search in session (simulating "saved searches" feature)
    $_SESSION['last_search'] = $searchQuery;

    // Search posts
    $stmt = $pdo->prepare("SELECT 'post' as type, id, title, content FROM posts WHERE title LIKE ? OR content LIKE ?");
    $term = '%' . $searchQuery . '%';
    $stmt->execute([$term, $term]);
    $results = array_merge($results, $stmt->fetchAll());

    // Search guestbook
    $stmt = $pdo->prepare("SELECT 'guestbook' as type, id, visitor_name as title, message as content FROM guestbook_entries WHERE message LIKE ? OR visitor_name LIKE ?");
    $stmt->execute([$term, $term]);
    $results = array_merge($results, $stmt->fetchAll());

    // Search comments
    $stmt = $pdo->prepare("SELECT 'comment' as type, id, '' as title, content FROM comments WHERE content LIKE ?");
    $stmt->execute([$term]);
    $results = array_merge($results, $stmt->fetchAll());
}

$pageTitle = 'Search - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<h1>Search</h1>

<div class="card">
    <form method="POST">
        <div class="form-group">
            <label>Search Posts, Comments & Guestbook</label>
            <!-- XSS-PHP-08: Search query reflected in value attribute without escaping -->
            <input type="text" name="q" value="<?= $_SESSION['last_search'] ?? '' ?>" placeholder="Enter search terms...">
        </div>
        <button type="submit" class="btn">Search</button>
    </form>
</div>

<?php if ($searchQuery): ?>
<h2>Results for: <?= h($searchQuery) ?></h2>
<p>Found <?= count($results) ?> result(s)</p>

<?php foreach ($results as $r): ?>
<div class="card">
    <span class="badge badge-<?= $r['type'] === 'post' ? 'easy' : 'medium' ?>"><?= h($r['type']) ?></span>
    <?php if ($r['title']): ?><strong><?= h($r['title']) ?></strong><?php endif; ?>
    <p><?= h(substr($r['content'], 0, 200)) ?></p>
</div>
<?php endforeach; ?>
<?php endif; ?>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
