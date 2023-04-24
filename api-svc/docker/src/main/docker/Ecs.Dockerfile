
# to look up latest version: https://hub.docker.com/_/amazonlinux
FROM amazonlinux:2.0.20221210.0

MAINTAINER Raido support team "contact@raid.org"

# Updating Java version: 
# To list available JDK versions, first run the container, then connect to the 
# continaer and run `yum --showduplicates list java-17-amazon-corretto-devel`.
# Update the version below and re-run the build command to test.

# skip_missing=false:
# without this, yumjust silently skips and then later on your 
# `java` command or whatever will fail and it won't be obvious what happened

RUN yum update -y \
  && yum --setopt=skip_missing_names_on_install=False install -y \
  # for `java`    
  java-17-amazon-corretto-devel-1:17.0.5+8-1.amzn2.1.x86_64 \
  # for `adduser`  
  shadow-utils \
  # for getting the task-arn from the ECS metadata 
  jq \
  && yum clean all 
      
# don't need it for running, I was just debugging 
# RUN yum install git -y

RUN useradd appuser

COPY raido-api-svc.jar app/raido-api-svc.jar

COPY script/ecs-api-svc-start.sh app/ecs-api-svc-start.sh
RUN chmod +x app/ecs-api-svc-start.sh

USER appuser

# makes /app directory and container will start there
WORKDIR app

EXPOSE 8080

# set this if you want to run the container outside of ECS
ENV EnvironmentConfig.nodeId=""

# not sure if should use ENTRYPOINT / CMD.  Internet says use ENTRYPOINT.
ENTRYPOINT ["/app/ecs-api-svc-start.sh"]

