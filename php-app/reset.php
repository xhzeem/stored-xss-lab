<?php
require_once __DIR__ . '/config.php';
$pdo = getPDO();

$pdo->exec("DELETE FROM notifications");
$pdo->exec("DELETE FROM activity_logs");
$pdo->exec("DELETE FROM private_messages");
$pdo->exec("DELETE FROM guestbook_entries");
$pdo->exec("DELETE FROM comments");
$pdo->exec("DELETE FROM posts");
$pdo->exec("DELETE FROM profiles");
$pdo->exec("DELETE FROM users");

header('Location: seed.php');
