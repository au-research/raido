-- stuff that didn't work 
pg_restore $HOST_PORT \
--clean \
--verbose --exit-on-error --single-transaction --no-owner \
--username=$PG_ADMIN_USER --dbname=$RAIDO_DB_NAME   \
--schema=$API_SCHEMA_NAME \
$PG_DUMP_FILE

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