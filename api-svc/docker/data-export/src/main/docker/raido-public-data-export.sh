#!/bin/bash

set -o errexit -o nounset -o pipefail

echo "inside raido-public-data-export container"
pwd
ls -la

FAKE_PROPS="\
  -DRaidV1Auth.jwtSecret=12345678901234567890123456789012 \
  -DRaidV2AppUserAuth.jwtSecrets=12345678901234567890123456789012 \
  -DAafOidc.clientSecret=12345678901234567890123456789012 \
  -DGoogleOidc.clientSecret=12345678901234567890123456789012 \
  -DOrcidOidc.clientSecret=12345678901234567890123456789012 \
  -DApids.secret=12345678901234567890123456789012 \
  -DRaidV2ApiKeyAuth.jwtSecrets=12345678901234567890123456789012 \
"


POSTGRES_PROPS="\
  -DEnvironmentConfig.envName=AgencyPublicDataExport \
  -DDatasourceConfig.url=jdbc:p6spy:postgresql://$PG_HOST:$PG_PORT/$RAIDO_DB_NAME \
  -DDatasourceConfig.username=$API_USER_NAME \
  -DDatasourceConfig.password=$API_USER_PASSWORD \
"

echo "starting java export process .... "
# added `exec` because the app wasn't receiving the signal to shut down 
# when running `docker stop`, so the Spring app wasn't shutting down gracefully
# https://stackoverflow.com/a/68578067/924597
java \
  $FAKE_PROPS \
  $POSTGRES_PROPS \
  -DAgencyPublicDataExport.allRaidsFilename=$EXPORT_DIR/$EXPORT_FILENAME \
  -DEnvironmentConfig.nodeId=$NODE_ID \
  -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
  -Duser.language= -Duser.country= -Duser.variant= \
  -Dlogback.configurationFile=docker-logback.xml \
  -cp raido-api-svc.jar \
  raido.cmdline.AgencyPublicDataExport 

echo "export process finished "

ls -la

cd $EXPORT_DIR

# I can't remember why I wanted zip instead of gzip?
# AFAIK, gzip compresses better, so why zip?
zip -9 $EXPORT_FILENAME.zip $EXPORT_FILENAME

ls -la