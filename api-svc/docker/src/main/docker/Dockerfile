
FROM amazonlinux:2.0.20220218.1

MAINTAINER Raido support team "web.services@ardc.edu.au"

RUN yum update -y \
  && yum install -y \
  # for `java`    
  java-17-amazon-corretto-devel 1:17.0.4+8-1.amzn2.1 \
  # for `adduser`  
  shadow-utils \
  && yum clean all 
      
# don't need it for running, I was just debugging 
# RUN yum install git -y

RUN useradd appuser

ADD raido-api-svc.jar app/raido-api-svc.jar

ADD script/api-svc-start.sh app/api-svc-start.sh
RUN chmod +x app/api-svc-start.sh

USER appuser

# makes /app directory and container will start there
WORKDIR app

EXPOSE 8080

CMD ["/app/api-svc-start.sh"]

