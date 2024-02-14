FROM quay.io/keycloak/keycloak:latest as builder

WORKDIR /opt/keycloak

COPY certs/test-raid-org-au.pem .
COPY certs/demo-raid-org-au.pem .
COPY certs/prod-raid-org-au.pem .

RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore
RUN keytool -import -trustcacerts -alias test.raid.org.au -file test-raid-org-au.pem -keystore conf/server.keystore
RUN keytool -import -trustcacerts -alias demo.raid.org.au -file test-raid-org-au.pem -keystore conf/server.keystore
RUN keytool -import -trustcacerts -alias prod.raid.org.au -file test-raid-org-au.pem -keystore conf/server.keystore

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/


WORKDIR /opt/keycloak

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]