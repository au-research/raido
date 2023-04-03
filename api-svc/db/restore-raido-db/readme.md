The env variables are default for running on a local dev machine in a docker 
container, expecting the DB to be running as per the setup in 
[db/readme.md](../readme.md#running-a-local-db-for-development)

To build the image locally run:
`gradlew :api-svc:db:raido-db-restore:dockerBuild` from the project root.

The command below will drop/create the `raido` database, then restore all the 
schemas and types into it that were exported from prod (i.e. `public`, 
`api_svc`, `raid_v1_import` and whatever else is there).

To run the restore, something like this:
```
docker run \
  --env PG_ADMIN_PASSWORD=<password> \
  --env API_USER_PASSWORD=<password> \
  --env PG_DUMP_FILE=pg_dump_2023-03-29_00-53-39.sqlc \
  --volume ../s3/raido-db-demo-import:/raido-db-restore/import-data \
  raido-db-restore
```

Note that when you do this, you may need to update your `api-svc-db.gralde`
config file so that you can run flyway:
```
apiSvcExtraLocations=["classpath:db/env/api_user","classpath:db/env/prod"]
```
Because the `demo` files weren't run in prod, `flywayMigrate` will complain 
about them not having being applied if you leave your local config pointing
at demo.

Also note that the `:v1-ddb-migration:flywayMigrate` task was not run properly
in prod. (basically, I didn't clear out the DB and re-run the migrations after
having changed them).

If you get errors about those migrations, you'll need to run the `flywayRepair`
task from that sub-project.

The volume might have to be absolute.
Had to be absolute on windows, and had to be like `C:\\dir\\dir`

Override env vars as necessary, see [Dockerfile](./src/main/docker/Dockerfile)

You can also override the `CMD` and run the `raido-db-restore-keey-users.sh` 
script to try and stash the `app-user` data to keep api-keys working, but 
it's a flawed and broken approach.  Just append 
`./ raido-db-restore-keey-users.sh` to the `docker run` command to invoke it.