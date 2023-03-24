#!/bin/bash

set -o errexit

echo "inside restore container"
pwd
ls -la

# this is the env var that psql will pick up from 
export PGPASSWORD="${PG_ADMIN_PASSWORD:?PG_ADMIN_PASSWORD environment variable is empty}"

PG_DUMP_FILE="import-data/${PG_DUMP_FILE:?PG_DUMP_FILE environment variable is empty}"

#make sure the file exists
ls -la $PG_DUMP_FILE

#export PGPASSWORD=$PG_ADMIN_PASSWORD:?PG_ADMIN_PASSWORD environment variable is empty

export HOST_PORT="--host=$PG_HOST --port=$PG_PORT"
# PGOPTIONS is weird and I'm not sure this even works
export PGOPTIONS="-v ON_ERROR_STOP=1"
#export PGOPTIONS="-v ON_ERROR_STOP=1 -h $PG_HOST -p $PG_PORT"
#export PGOPTIONS="-v ON_ERROR_STOP=1 --host=$PG_HOST --port=$PG_PORT"
#export PGOPTIONS="--host=$PG_HOST --port=$PG_PORT"
#export PGOPTIONS="-h $PG_HOST -p $PG_PORT"

echo "show PG version on $HOST_PORT"
psql $HOST_PORT --username=$PG_ADMIN_USER --file select-version.sql

echo "kick users off of $RAIDO_DB_NAME"
#cat kick-user.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  --file kick-user.sql

echo "dropping database $RAIDO_DB_NAME"
#cat drop_database.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  --file drop-database.sql

echo "creating user $API_USER_NAME"
#cat create-user.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  -v PG_ADMIN_USER="$PG_ADMIN_USER" \
  -v API_USER_NAME="$API_USER_NAME" \
  -v API_USER_PASSWORD="$API_USER_PASSWORD" \
  --file create-user.sql

echo "creating database $RAIDO_DB_NAME with owner $API_USER_NAME"
#cat drop_database.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  -v RAIDO_DB_NAME="$RAIDO_DB_NAME" \
  -v API_USER_NAME="$API_USER_NAME" \
  --file create-database.sql

echo "creating schema $API_SCHEMA_NAME"
#cat create-schema.sql
psql $HOST_PORT --username=$PG_ADMIN_USER \
  --dbname $RAIDO_DB_NAME \
  -v API_USER_NAME="$API_USER_NAME" \
  -v API_SCHEMA_NAME="$API_SCHEMA_NAME" \
  --file create-schema.sql

echo "restoring into database $RAIDO_DB_NAME"
pg_restore $HOST_PORT \
  --verbose --exit-on-error --single-transaction --no-owner \
  --username=$PG_ADMIN_USER --dbname=$RAIDO_DB_NAME   \
  --schema=$API_SCHEMA_NAME \
  $PG_DUMP_FILE

#pg_restore $HOST_PORT \
#  --clean \
#  --verbose --exit-on-error --single-transaction --no-owner \
#  --username=$PG_ADMIN_USER --dbname=$RAIDO_DB_NAME   \
#  --schema=$API_SCHEMA_NAME \
#  $PG_DUMP_FILE

#export EXCLUDED_TABLES="(app_user|user_authz_request)"
#
##https://stackoverflow.com/a/55844731/924597
## space after exluced tables is import to avoid matching sub-strings in names
#pg_restore -l $PG_DUMP_FILE \
# | grep -vE "TABLE DATA api_svc $EXCLUDED_TABLES " \
# > restore.pgdump.list
#
#cat restore.pgdump.list
#
#echo "restoring into database $RAIDO_DB_NAME"
#pg_restore $HOST_PORT \
#  -L restore.pgdump.list \
#  --verbose --exit-on-error --single-transaction --no-owner \
#  --username=$PG_ADMIN_USER --dbname=$RAIDO_DB_NAME   \
#  --schema=$API_SCHEMA_NAME \
#  $PG_DUMP_FILE
#
##  --clean \
#
##pg_restore -L <(pg_restore -l $PG_DUMP_FILE | grep -v 'TABLE DATA public app_user ') -d db_name_where_to_restore /path/to/db/dump