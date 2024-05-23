FROM satosa:8-alpine

USER root

RUN apk upgrade

COPY files/usr/local/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

USER satosa:satosa

