
FROM postgres:15.2

MAINTAINER Raido support team "contact@raid.org"

# These must be provided to run the restore
ENV PG_ADMIN_PASSWORD=""
ENV API_USER_PASSWORD=""
ENV PG_DUMP_FILE=""

ENV PG_HOST="host.docker.internal"
ENV PG_PORT="7432"

ENV PG_ADMIN_USER="postgres"

#ENV RAIDO_DB_NAME="raido_test"
ENV RAIDO_DB_NAME="raido"

# can't change these because the restore expects to match
ENV API_USER_NAME="api_user"
ENV API_SCHEMA_NAME="api_svc"
ENV IMPORT_SCHEMA_NAME="raid_v1_import"


WORKDIR /raido-db-restore

COPY . .

RUN chmod +x raido-db-restore.sh
RUN chmod +x raido-db-restore-keep-users.sh


CMD ["./raido-db-restore.sh"]
#CMD ["ls", "-la"]


