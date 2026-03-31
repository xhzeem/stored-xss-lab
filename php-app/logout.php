<?php
// PHP App - Logout
require_once __DIR__ . '/config.php';
session_destroy();
header('Location: index.php');
