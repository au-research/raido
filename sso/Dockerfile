FROM quay.io/rockylinux/rockylinux:9.3-minimal

COPY files/etc/yum.repos.d/shibboleth.repo /etc/yum.repos.d/shibboleth.repo

RUN microdnf upgrade -y && microdnf update -y && microdnf install -y curl mod_ssl httpd shibboleth

RUN mkdir /var/www/html/secure

RUN openssl req -newkey rsa:2048 -batch -new -nodes -x509 -days 3650 -keyout /etc/pki/tls/private/localhost.key -out /etc/pki/tls/certs/localhost.crt

COPY files/etc/shibboleth/aaf-metadata-certificate.pem /etc/shibboleth/aaf-metadata-certificate
COPY files/etc/shibboleth/shibboleth2.xml /etc/shibboleth/shibboleth2.xml
COPY files/var/www/html/secure/environment.php /var/www/html/secure/environment.php
COPY files/etc/httpd/conf/httpd.conf /etc/httpd/conf/httpd.conf

WORKDIR /etc/shibboleth

RUN ./keygen.sh -h app.test.raid.org.au -e https://app.test.raid.org.au/shibboleth -f -y 10

RUN chown shibd:shibd /etc/shibboleth/sp-key.pem

RUN chown shibd:shibd /etc/shibboleth/sp-cert.pem

EXPOSE 80

ENTRYPOINT httpd && shibd -Fu shibd

