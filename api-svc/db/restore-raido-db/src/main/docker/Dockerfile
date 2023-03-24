
FROM postgres:15.2

MAINTAINER Raido support team "contact@raid.org"

ENV PG_HOST="host.docker.internal"
ENV PG_PORT="7432"

ENV PG_ADMIN_USER="postgres"
ENV PG_ADMIN_PASSWORD=""

ENV RAIDO_DB_NAME="raido_test"

# can't change these because the restore expects to match
ENV API_USER_NAME="api_user"
ENV API_SCHEMA_NAME="api_svc"

ENV API_USER_PASSWORD="wobble"

ENV PG_DUMP_FILE=""

WORKDIR /raido-db-restore

COPY . .

RUN chmod +x raido-db-restore.sh


ENTRYPOINT ["./raido-db-restore.sh"]
#CMD ["ls", "-la"]


