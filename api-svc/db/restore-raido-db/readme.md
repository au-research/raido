The env variables are default for running on a local dev machine in a docker 
container, expecting the DB to be running as per the setup in 
[db/readme.md](../readme.md#running-a-local-db-for-development)

To build the image locally run:
`gradlew :api-svc:db:raido-db-restore:dockerBuild` from the project root.
`

To run the restore, something like this:
```
docker run \
  -e PG_ADMIN_PASSWORD=<password> \
  -e PG_DUMP_FILE=pg_dump_2023-03-22_05-19-07.sqlc \
  -v ../s3/raido-db-demo-import:/raido-db-restore/import-data \
  raido-db-restore
```

The volume might have to be absolute.
Had to be absolute on windows, and had to be like `C:\\dir\\dir`

Override env vars as necessary, see [Dockerfile](./src/main/docker/Dockerfile)
