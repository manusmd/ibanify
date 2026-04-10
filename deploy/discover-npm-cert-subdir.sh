#!/usr/bin/env sh
set -eu
CONTAINER=${1:?}
DOMAIN=${2:?}
if ! docker info >/dev/null 2>&1; then
  echo "discover: docker not usable for this user (add to docker group or run as root)." >&2
  exit 1
fi
if ! docker inspect "$CONTAINER" >/dev/null 2>&1; then
  echo "discover: container not found: $CONTAINER" >&2
  exit 1
fi
set +e
out=$(docker exec "$CONTAINER" sh -c '
domain="$1"
for f in /data/nginx/proxy_host/*.conf; do
  [ -f "$f" ] || continue
  grep -qF "$domain" "$f" 2>/dev/null || continue
  line=$(grep -m1 "ssl_certificate " "$f" 2>/dev/null | grep -v "^[[:space:]]*#" || true)
  [ -n "$line" ] || continue
  echo "$line" | grep -q fullchain.pem || continue
  cert=$(echo "$line" | sed -e "s/.*ssl_certificate[[:space:]]\{1,\}//" -e "s/;.*//" | tr -d "[:space:]")
  echo "$cert" | sed -e "s|.*/live/||" -e "s|/fullchain.pem||"
  exit 0
done
exit 1
' sh "$DOMAIN")
dex=$?
set -e
if [ "$dex" -ne 0 ] || [ -z "$out" ]; then
  echo "discover: no proxy_host *.conf for $DOMAIN with ssl_certificate .../live/npm-*/fullchain.pem" >&2
  echo "discover: enable SSL on that Proxy Host in NPM, or set Actions variable SSL_CERT_LIVE_SUBDIR." >&2
  exit 1
fi
printf '%s' "$out"
