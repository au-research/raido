Raido uses [Flyway](https://flywaydb.org/documentation/) to manage schema 
migrations.

The migration process is run from within AWS via a `codebuild` project - we
do not run Flyway from within api-svc server process.

Remember that this codebase is publicly visible.  You must not put secrets
or private customer information into the DB migration scripts.  

Take the data migration process for the legacy V1 data as an example:
* the scheme and migration code are committed in the 
  `/api-svc-/db/v1-ddb-migration` project
* the actual data is stored in a private S3 bucket 
* the migration process is run from a `codebuild` instance, which has been 
  granted explicit access to the migration data files


# Structure

* `:api-svc:db`
  * acts as a "container" project of the various DB projects
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


# Configuring to use the DB for flyway migration of local DB

The `:api-svc:db:shared` has these default values:
* `apiSvcPgUrl` = jdbc:postgresql://localhost:7432/raido
* `apiSvcPgUser` = postgres

These defaults will work with the `raido-db` container described above.

You must configure the `apiSvcPgPassword` property to specify the
same password you gave to create the database:

`~/.config/raido-v2/api-svc-db.gradle`:
```
apiSvcPgPassword="the password you specified"
apiSvcExtraLocation="classpath:db/env/demo"
```

The `apiSvcExtraLocation` can be used to load "conditional" schema files
for a given environment.  The above will make sure your local DB has the same
SQL executed as the `demo` environment.

Note that these creds are different from what the actual api-svc uses.
The [spring/readme.md](../spring/readme.md) discusses setting up the config
for the api-svc.

