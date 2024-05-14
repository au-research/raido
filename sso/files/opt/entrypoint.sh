#!/usr/bin/env bash

# link the correct shibboleth2.test.xml depending on environment

rm /etc/shibboleth/shibboleth2.xml

if [ "$ENVIRONMENT_NAME" = "prod" ]; then
  ln -s /etc/shibboleth/shibboleth2.prod.xml /etc/shibboleth/shibboleth2.xml
else
  ln -s /etc/shibboleth/shibboleth2.test.xml /etc/shibboleth/shibboleth2.xml
fi

echo "$SSO_SIGNING_KEY"

if [ -z "$SSO_SIGNING_KEY" ]; then
  echo "ERROR: Signing key not set";
  exit 1;
else
  echo "$SSO_SIGNING_KEY" > /etc/shibboleth/sp-signing-key.pem;
  chmod 400 /etc/shibboleth/sp-signing-key.pem;
  chown shibd:shibd /etc/shibboleth/sp-signing-key.pem
fi

if [ -z "$SSO_SIGNING_CERT" ]; then
  echo "ERROR: Signing cert not set";
  exit 1;
else
  echo "$SSO_SIGNING_CERT" > /etc/shibboleth/sp-signing-cert.pem;
  chmod 400 /etc/shibboleth/sp-signing-cert.pem;
  chown shibd:shibd /etc/shibboleth/sp-signing-cert.pem;
fi

if [ -z "$SSO_ENCRYPTION_KEY" ]; then
  echo "ERROR: Encrypt key not set";
  exit 1;
else
  echo "$SSO_ENCRYPTION_KEY" > /etc/shibboleth/sp-encrypt-key.pem;
  chmod 400 /etc/shibboleth/sp-encrypt-key.pem;
  chown shibd:shibd /etc/shibboleth/sp-encrypt-key.pem;
fi

if [ -z "$SSO_ENCRYPTION_CERT" ]; then
  echo "ERROR: Encrypt cert not set";
  exit 1;
else
  echo "$SSO_ENCRYPTION_CERT" > /etc/shibboleth/sp-encrypt-cert.pem
fi

httpd && shibd -Fu shibd