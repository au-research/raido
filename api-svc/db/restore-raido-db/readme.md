The env variables are default for running on a local dev machine in a docker 
container, expecting the DB to be running as per the setup in 
[db/readme.md](../readme.md#running-a-local-db-for-development)

To build the image locally run:
`gradlew :api-svc:db:restore-raido-db:dockerBuild` from the project root.
`

To run the import:
`docker run -e PG_ADMIN_PASSWORD=<password> raido-db-restore`

Override env vars as necessary, see [Dockerfile](./src/main/docker/Dockerfile)
