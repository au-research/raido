#!/bin/sh
# should be "appuser" as defined in Dockerfile
echo "running as $(whoami) in $PWD at $(date)" 
ls -Rla $HOME 

# bash has trouble with '.' characters in variables (and thus, env variables), 
# even though env vars with names containing '.' are completely valid at a 
# process/systems level (i.e. it's fine at unix/linux level)
task_node_id=$(printenv 'EnvironmentConfig.nodeId')

echo "NodeId: $task_node_id"

if [[ -z "${task_node_id}" ]]; then
  echo "EnvironmentConfig.nodeId is not set, reading from ECS aws_metadata"
  
  if [[ -z "$ECS_CONTAINER_METADATA_URI_V4" ]]; then
      echo "Error: Not running inside an ECS container, exiting."
      exit 1
  fi

  # this is supposed to be set by ECS when it runs the task
  # must be enabled in the task definition
  aws_metadata=$(curl -s "$ECS_CONTAINER_METADATA_URI_V4")
  aws_task_arn=$(echo "$aws_metadata" | jq -r '.Labels["com.amazonaws.ecs.task-arn"]')
  aws_task_id=$(echo "$aws_task_arn" | awk -F '/' '{print $NF}')

  echo "export $aws_task_id as the task_node_id"
  export task_node_id=$aws_task_id
  
fi


echo "starting api-svc.... "
# added `exec` because the app wasn't receiving the signal to shut down 
# when running `docker stop`, so the Spring app wasn't shutting down gracefully
# https://stackoverflow.com/a/68578067/924597
exec java \
  -DEnvironmentConfig.nodeId=$task_node_id \
  -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
  -Duser.language= -Duser.country= -Duser.variant= \
  -Dlogback.configurationFile=docker-logback.xml \
  -jar raido-api-svc.jar 

# when having problems digging around in the container because the app won't 
# start, have the container just run this
#tail -f /.dockerenv  
