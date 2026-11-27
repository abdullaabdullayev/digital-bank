#!/bin/sh
# Creates one database per service on first start of the PostgreSQL container.
# Runs only when the data volume is empty (standard docker-entrypoint-initdb.d behaviour).
set -eu

for db in $(echo "${SERVICE_DATABASES:-}" | tr ',' ' '); do
  echo "Creating database: $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
    -c "CREATE DATABASE \"$db\" OWNER \"$POSTGRES_USER\";"
done
