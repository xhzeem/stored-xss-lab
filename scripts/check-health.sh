#!/bin/bash
# Health check - verifies all services are reachable
set -e

PORT="${1:-4444}"

echo "=== XSS Lab Health Check (port $PORT) ==="
FAIL=0

check() {
    local name=$1
    local url=$2
    status=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "$url" 2>/dev/null || echo "000")
    if [ "$status" = "200" ]; then
        echo "[OK]   $name ($url) -> $status"
    else
        echo "[FAIL] $name ($url) -> $status"
        FAIL=1
    fi
}

check "Nginx Landing"    "http://localhost:$PORT/"
check "Nginx Health"     "http://localhost:$PORT/health"
check "PHP App"          "http://localhost:$PORT/php/index.php"
check "PHP Health"       "http://localhost:$PORT/php/health.php"
check "Go App"           "http://localhost:$PORT/go/"
check "Go Health"        "http://localhost:$PORT/go/health"
check "Java App"         "http://localhost:$PORT/java/"
check "Java Health"      "http://localhost:$PORT/java/health"
check "Node App"         "http://localhost:$PORT/node/"
check "Node Health"      "http://localhost:$PORT/node/health"
check "Python App"       "http://localhost:$PORT/python/"
check "Python Health"    "http://localhost:$PORT/python/health"

echo ""
if [ $FAIL -eq 0 ]; then
    echo "All services healthy."
else
    echo "Some services failed."
fi
exit $FAIL
