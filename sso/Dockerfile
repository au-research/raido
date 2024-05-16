FROM quay.io/rockylinux/rockylinux:9.3-minimal

COPY files/etc/yum.repos.d/shibboleth.repo /etc/yum.repos.d/shibboleth.repo

RUN microdnf upgrade -y && microdnf update -y && microdnf install -y curl mod_ssl httpd shibboleth

RUN mkdir /var/www/html/secure

RUN openssl req -newkey rsa:2048 -batch -new -nodes -x509 -days 3650 -keyout /etc/pki/tls/private/localhost.key -out /etc/pki/tls/certs/localhost.crt

COPY files/etc/shibboleth/aaf-metadata-certificate.pem /etc/shibboleth/aaf-metadata-certificate.pem
COPY files/etc/shibboleth/shibboleth2.test.xml /etc/shibboleth/shibboleth2.test.xml
COPY files/etc/shibboleth/shibboleth2.prod.xml /etc/shibboleth/shibboleth2.prod.xml
COPY files/etc/shibboleth/shibd.logger /etc/shibboleth/shibd.logger
COPY files/var/www/html/secure/environment.php /var/www/html/secure/environment.php
COPY files/etc/httpd/conf/httpd.conf /etc/httpd/conf/httpd.conf
COPY files/opt/entrypoint.sh /opt/entrypoint.sh

RUN ln -s /proc/self/fd/1 /var/log/httpd/ssl_access_log
RUN ln -s /proc/self/fd/1 /var/log/httpd/ssl_request_log
RUN ln -s /proc/self/fd/2 /var/log/httpd/ssl_error_log

WORKDIR /etc/shibboleth

RUN ./keygen.sh -h app.test.raid.org.au -e https://app.test.raid.org.au/shibboleth -f -y 10

RUN chown -R shibd:shibd /etc/shibboleth/
RUN chown -R shibd:shibd /var/cache/shibboleth/

EXPOSE 80
EXPOSE 443

ENTRYPOINT ["/opt/entrypoint.sh"]

