#!/bin/bash

set -o errexit -o nounset -o pipefail

echo "inside restore container"
pwd
ls -la

# this is the env var that psql will pick up from 
export PGPASSWORD="${PG_ADMIN_PASSWORD:?PG_ADMIN_PASSWORD environment variable is empty}"
API_USER_PASSWORD_TEST="${API_USER_PASSWORD:?API_USER_PASSWORD environment variable is empty}"

PG_DUMP_FILE="import-data/${PG_DUMP_FILE:?PG_DUMP_FILE environment variable is empty}"

#make sure the file exists
ls -la $PG_DUMP_FILE

export HOST_PORT="--host=$PG_HOST --port=$PG_PORT"
export FAIL_ON_ERROR="-v ON_ERROR_STOP=1"

echo "show PG version on $HOST_PORT"
psql $HOST_PORT --username=$PG_ADMIN_USER --file select-version.sql

echo "kick users off of $RAIDO_DB_NAME"
#cat kick-user.sql
psql $HOST_PORT $FAIL_ON_ERROR --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  --file kick-user.sql

echo "dropping database $RAIDO_DB_NAME"
#cat drop_database.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  --file drop-database.sql

echo "creating user $API_USER_NAME"
#cat create-user.sql
psql $HOST_PORT $FAIL_ON_ERROR --username=$PG_ADMIN_USER \
  -v PG_ADMIN_USER="$PG_ADMIN_USER" \
  -v API_USER_NAME="$API_USER_NAME" \
  -v API_USER_PASSWORD="$API_USER_PASSWORD" \
  --file create-user.sql

echo "creating database $RAIDO_DB_NAME with owner $API_USER_NAME"
#cat drop_database.sql
psql $HOST_PORT $FAIL_ON_ERROR --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  -v API_USER_NAME="$API_USER_NAME" \
  --file create-database.sql

echo "restoring into database $RAIDO_DB_NAME"
pg_restore $HOST_PORT --exit-on-error \
  --verbose --single-transaction --no-owner \
  --username=$PG_ADMIN_USER --dbname=$RAIDO_DB_NAME   \
  $PG_DUMP_FILE

#not sure if we want this
#--clean \