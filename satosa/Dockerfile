FROM satosa:8-alpine

USER root

RUN apk upgrade

USER satosa:satosa

# COPY files/etc/satosa/plugins/backends/saml2_backend.yaml /etc/satosa/plugins/backends/saml2_backend.yaml
# COPY files/etc/satosa/plugins/frontends/saml2_frontend.yaml /etc/satosa/plugins/frontends/saml2_frontend.yaml
# COPY files/etc/satosa/plugins/frontends/ping_frontend.yaml /etc/satosa/plugins/frontends/ping_frontend.yaml

COPY files/usr/local/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

