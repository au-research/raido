
# to look up latest version: https://hub.docker.com/_/amazonlinux
FROM amazonlinux:2.0.20221210.0

MAINTAINER Raido support team "contact@raid.org"

# Updating Java version: 
# To list available JDK versions, first run the container, then connect to the 
# container and run `yum --showduplicates list java-17-amazon-corretto-devel`.
# Update the version below and re-run the build command to test.

# skip_missing=false:
# without this, yumjust silently skips and then later on your 
# `java` command or whatever will fail and it won't be obvious what happened

RUN yum update -y \
  && yum --setopt=skip_missing_names_on_install=False install -y \
  # for `java`    
  java-17-amazon-corretto-devel-1:17.0.5+8-1.amzn2.1.x86_64 \
  # zip not installed by default  
  zip \
  && yum clean all 
      

WORKDIR /raido-public-data-export

COPY . .

RUN chmod +x raido-public-data-export.sh

# These must be provided to run the export
ENV API_USER_PASSWORD=""

# These are defaulted for local dev and must be override in a real env 
ENV PG_HOST="host.docker.internal"
ENV PG_PORT="7432"
ENV RAIDO_DB_NAME="raido"
ENV API_USER_NAME="api_user"

ENV NODE_ID="raido-public-data-export"

# The directory this points at should be mounted as a volume so you can get the 
# exported file 
ENV EXPORT_DIR="/raido-public-data-export/export-data"
ENV EXPORT_FILENAME="public-export-all-raids.ndjson"

CMD ["./raido-public-data-export.sh"]


