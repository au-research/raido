
## Running `Api.main()`

If you want to start the app from your IDE, remember to specify JVM options:

`-Duser.timezone=UTC -Dfile.encoding=UTF-8 -Duser.language= -Duser.country= -Duser.variant=`

The properties when running via main method, either from IDE or gradle (as 
opposed to integration or functional test) are loaded from 
`~/.config/raido-v2/api-svc-env.properties`, see
[ApiConfig](/src/main/java/raido/spring/config/ApiConfig.java).

## Running a local DB

Launch local PG container.

This is used by the `:api-svc:v1-ddb-migration` project to migrate DynamoDB 
into, and the api-svc itself to run off of.

```
docker rm --force raido-db; docker run --name raido-db -p 7432:5432 -d --restart unless-stopped -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD= -e POSTGRES_DB=raido postgres

```
This won't actually run, because no password.
* `raid-db` is the container name
* `raido` is the database name

## Configuration

Once you've got a DB

### `~/.config/raido-v2/api-svc.gradle`
* The build needs to know the DB to use for flyway/jooq stuff for the 
  `api-schema`
```groovy
  apiSvcPgPassword="<dbpassword>"
```

`~/.config/raido-v2/api-svc-env.properties`
```properties
redactErrorDetails=false

DatasourceConfig.url=jdbc:postgresql://localhost:7432/raido
DatasourceConfig.username=postgres
DatasourceConfig.password=<dbpassword>

RaidV1Auth.jwtSecret=<JWT secret>
```


### Running integration tests

See [intTest/readme.md](./src/intTest/readme.md)

Should maybe reconsidered - rename to "intTest".
