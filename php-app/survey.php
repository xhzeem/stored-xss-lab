<?php
// PHP App - Survey Page
// Vulnerability: XSS-PHP-09 - Survey free-text response rendered client-side from JSON
//   Response text is fetched via inline script and inserted into DOM with innerHTML
// Difficulty: hard | Sink: innerHTML from JSON data attribute | Stored field: response
require_once __DIR__ . '/config.php';
$pdo = getPDO();

// Create survey tables if not exist
$pdo->exec("CREATE TABLE IF NOT EXISTS surveys (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    question TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)");
$pdo->exec("CREATE TABLE IF NOT EXISTS survey_responses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    survey_id INTEGER NOT NULL,
    respondent TEXT NOT NULL,
    response TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (survey_id) REFERENCES surveys(id)
)");

// Handle new response
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['submit_response'])) {
    $stmt = $pdo->prepare("INSERT INTO survey_responses (survey_id, respondent, response) VALUES (?, ?, ?)");
    $stmt->execute([
        $_POST['survey_id'] ?? 1,
        $_POST['respondent'] ?? 'Anonymous',
        $_POST['response'] ?? ''
    ]);
    $flash = 'Response submitted!';
}

// Seed default surveys if empty
$cnt = $pdo->query("SELECT COUNT(*) FROM surveys")->fetchColumn();
if ($cnt == 0) {
    $pdo->exec("INSERT INTO surveys (question) VALUES ('What do you think about web security training?')");
    $pdo->exec("INSERT INTO surveys (question) VALUES ('How would you rate this forum?')");
    $pdo->exec("INSERT INTO surveys (question) VALUES ('What features should we add next?')");
}

$pageTitle = 'Surveys - Community Forum';
require_once __DIR__ . '/templates/header.php';
?>

<?php if (isset($flash)): ?><div class="flash flash-success"><?= h($flash) ?></div><?php endif; ?>

<h1>Community Surveys</h1>

<?php
$surveys = $pdo->query("SELECT * FROM surveys ORDER BY id")->fetchAll();
foreach ($surveys as $survey):
    $responses = $pdo->prepare("SELECT * FROM survey_responses WHERE survey_id = ? ORDER BY created_at DESC");
    $responses->execute([$survey['id']]);
    $resps = $responses->fetchAll();
?>
<div class="card">
    <h3><?= h($survey['question']) ?></h3>
    <div id="survey-responses-<?= $survey['id'] ?>">
        <?php foreach ($resps as $r): ?>
        <div class="card" style="background:#f8f9fa;">
            <div class="post-meta"><?= h($r['respondent']) ?> on <?= h($r['created_at']) ?></div>
            <!-- XSS-PHP-09: Survey response rendered as raw HTML -->
            <div class="response-text"><?= $r['response'] ?></div>
        </div>
        <?php endforeach; ?>
    </div>

    <form method="POST" style="margin-top:10px;">
        <input type="hidden" name="submit_response" value="1">
        <input type="hidden" name="survey_id" value="<?= $survey['id'] ?>">
        <div class="form-group">
            <label>Your Name</label>
            <input type="text" name="respondent" required>
        </div>
        <div class="form-group">
            <label>Your Response</label>
            <textarea name="response" required></textarea>
        </div>
        <button type="submit" class="btn">Submit Response</button>
    </form>
</div>
<?php endforeach; ?>

<!-- XSS-PHP-09b: Client-side rendering of survey data via JSON in script -->
<script>
var allResponses = [];
<?php
$allResps = $pdo->query("SELECT sr.*, s.question FROM survey_responses sr JOIN surveys s ON sr.survey_id = s.id ORDER BY sr.created_at DESC LIMIT 20")->fetchAll();
foreach ($allResps as $ar):
?>
allResponses.push({
    respondent: <?= json_encode($ar['respondent']) ?>,
    response: "<?= $ar['response'] ?>",
    question: <?= json_encode($ar['question']) ?>
});
<?php endforeach; ?>
</script>

<?php require_once __DIR__ . '/templates/footer.php'; ?>
