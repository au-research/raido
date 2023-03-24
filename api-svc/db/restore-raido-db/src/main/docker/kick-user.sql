select :'RAIDO_DB_NAME' as "db";

SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = :'RAIDO_DB_NAME';