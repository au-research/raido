This project is for building a container that runs the api-svc as web server.
It also contains gradle tasks for tagging/pushing the container image to ECR. 

# Running container locally


## Configure Raido DB url to work from withing a container

* edit `~/.config/raido/api-svc-env.properties`
  * set `DatasourceConfig.url=jdbc:p6spy:postgresql://host.docker.internal:7432/raido`
    * as per: https://stackoverflow.com/a/71446292/924597


## Build the container image

* run Gradle task `raido:api-svc:docker:ecs:ecsDockerBuild`
* note the image name
  * e.g. "#12 naming to docker.io/library/raido-api-svc:f353110.dirty done"
    * "raido-api-svc:f353110.dirty"

  
## Windows run via IDEA


### Configure IDEA

* Make sure Docker Desktop is running.
* Make sure you have a Raido postgres database running
* Make sure IDEA is configured for Docker on windows
  * / Settings / Build, Execution, Deployment / Docker
  * click the `+` add button to add a docker service
    * give it a name something like `Docker Windows`
    * make sure `Docker for Windows` is selected
  * add a path mapping so the container can find your local config files
    * VM path = `~/.config/raido`
      * this matches the Spring config in 
      [ApiConfig.java](../../spring/src/main/java/raido/apisvc/spring/config/ApiConfig.java)
    * local path = `c:\Users\username\.config\raido`
      * i.e your local dir that contains config files like 
      `api-svc-env.properties` 


### Run container from IDEA

* `Services` tool window
* Expand `Docker Windows` service node, configured above
* Under `/images` find the image that was built by the Gradle task
* Do "Create container"
  * note, this never worked for me


## Windows run via Git Bash

* to see a list of local images
  * `docker images`

* to delete the container: 
  * `docker rm --force sto-test-raido`
* to run the container
  * 
```
docker rm --force sto-test-raido; \
 docker run --name sto-test-raido --publish 8042:8080 --detach --init \
  --volume C:\\Users\\stolley\\.config\\raido:/home/appuser/.config/raido raido-api-svc:587ccd7.dirty
```

* to see logs from the container
  * `docker logs sto-test-raido`
* to hit the version url
  * `curl localhost:8042/v2/public/version`
