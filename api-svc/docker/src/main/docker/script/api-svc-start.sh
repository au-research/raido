#!/bin/sh
# should be "appuser" as defined in Dockerfile
echo "running as $(whoami) in $PWD at $(date)" 
ls -Rla $HOME 

# jvm.props is for enviornment debugging/tuning (so you don't have fiddle
# with the docker image just to try a GC setting or something).
  
echo "starting api-svc.... "
# added `exec` because the app wasn't receiving the signal to shut down 
# when running `docker stop`, so the Spring app wasn't shutting down gracefully
# https://stackoverflow.com/a/68578067/924597
exec java $(cat $HOME/.config/raido-v2/api-svc-jvm.properties) \
  -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
  -Duser.language= -Duser.country= -Duser.variant= \
  -Dlogback.configurationFile=docker-logback.xml \
  -jar raido-api-svc.jar 

# when having problems digging around in the container because the app won't 
# start, have the container just run this
#tail -f /.dockerenv  
