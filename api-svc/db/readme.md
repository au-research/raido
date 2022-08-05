# Structure

* `:api-svc:db`
  * acts as a "container" of the various DB projects, so for example if you
    run gradle task `:api-svc:db/flywayMigrate` it actually runs the
    `flywayMigrate` of all sub-projects (so you get both `raido` and
    `raid_v1_import` schemas migrated at the same time)
  * `:api-svc:db:shared`
    * empty project, it exists to collect stuff that needs to be shared between
      the `db` subprojects
    * specifically, the `apiSvcPg` properties and the loading of those from
      the `~/.config/raido-v2/api-svc-db.gradle` config file
  * `:api-svc:db:raido`
    * contains the flyway and jooq stuff for the `raido` schema
  * `:api-svc:db:v1-ddb-migration`
    * contains the flyway and jooq stuff for the `raid_v1_import` schema


# Running a local DB for development

Launch local Postgres instance in a docker container.

```
docker rm --force raido-db; docker run --name raido-db -p 7432:5432 -d --restart unless-stopped -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD= -e POSTGRES_DB=raido postgres
```

* `raido-db` is the docker container name
* `raido` is the postgres database name

This won't actually run, because no password.  Copy/paste and add a password,
then run the command to get a `raido` database.

The DB at that point will be completely empty (no schema structure or data).


# Configuring to use the DB for local dev

The `:api-svc:db:shared` has these default values:
* `apiSvcPgUrl` = jdbc:postgresql://localhost:7432/raido
* `apiSvcPgUser` = postgres

These defaults will work with the `raido-db` container described above.

You must configure the `apiSvcPgPassword` property to specify the
same password you gave to create the database:

`~/.config/raido-v2/api-svc.gradle`:
```
apiSvcPgPassword="the password you specified"
```


