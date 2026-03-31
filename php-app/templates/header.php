<?php
// PHP App - Header Template
$prefix = getBaseUrl();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= $pageTitle ?? 'Community Forum' ?></title>
    <link rel="stylesheet" href="<?= $prefix ?>/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand"><a href="<?= $prefix ?>/index.php">PHP Forum</a></div>
    <div class="nav-links">
        <a href="<?= $prefix ?>/index.php">Dashboard</a>
        <a href="<?= $prefix ?>/posts.php">Posts</a>
        <a href="<?= $prefix ?>/guestbook.php">Guestbook</a>
        <a href="<?= $prefix ?>/messages.php">Messages</a>
        <a href="<?= $prefix ?>/notifications.php">Notifications</a>
        <a href="<?= $prefix ?>/activity.php">Activity</a>
        <a href="<?= $prefix ?>/search.php">Search</a>
        <a href="<?= $prefix ?>/survey.php">Surveys</a>
        <a href="<?= $prefix ?>/notif-settings.php">Notif Settings</a>
        <a href="<?= $prefix ?>/admin.php">Admin</a>
        <a href="<?= $prefix ?>/help.php">Help</a>
    </div>
</nav>
<div class="container">
