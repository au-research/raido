#!/bin/sh
# should be "appuser" as defined in Dockerfile
echo "running as $(whoami) in $PWD at $(date)" 
ls -Rla $HOME 

# jvm.props is for enviornment debugging/tuning (so you don't have fiddle
# with the docker image just to try a GC setting or something).
  
echo "starting api-svc.... "
java $(cat $HOME/.config/raido-v2/api-svc-jvm.properties) \
  -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
  -Duser.language= -Duser.country= -Duser.variant= \
  -jar raido-api-svc.jar 

# when having problems digging around in the container because the app won't 
# start, have the container just run this
#tail -f /.dockerenv  
