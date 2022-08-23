
# Running for local development

* setup the postgres DB as described in [db/readme.md](./db/readme.md)
* create the database schemas
  * run `gradlew :api-svc:db:flywayMigrate`
  * the migration will create the `api_user`, but the user is disabled 
because it has no password
* connect to your local database and run the following to set a password
  * `alter user api_user password ''` (supply your own password)
* optionally, import the raid V1 data
  * see [v1-ddb-migration/readme.md](./db/v1-ddb-migration/readme.md)
* see [spring/readme.md](./spring/readme.md) for instructions on running the
  actual server, including configuring the password for the `api_user`