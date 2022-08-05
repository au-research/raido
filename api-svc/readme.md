
# Running for local development

* setup the postgres DB as described in [db/readme.md](./db/readme.md)
* create the database schemas
  * run `gradlew :api-svc:db:flywayMigrate`
* optionally, import the raid V1 data
  * see [v1-ddb-migration/readme.md](./db/v1-ddb-migration/readme.md)
* see [spring/readme.md](./spring/readme.md) for instructions on running the
  actual server