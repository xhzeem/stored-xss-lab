#!/bin/bash
# Reset all databases by restarting containers (auto-reseed on startup)
set -e

echo "=== Resetting all XSS Lab databases ==="
docker compose restart php-app go-app java-app node-app python-app
echo "Waiting 15 seconds for services to restart..."
sleep 15
echo "Running health check..."
bash "$(dirname "$0")/check-health.sh"
