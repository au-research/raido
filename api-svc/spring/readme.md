
## Running `Api.main()`

If you want to start the app from your IDE, remember to specify JVM options:

`-Duser.timezone=UTC -Dfile.encoding=UTF-8 -Duser.language= -Duser.country= -Duser.variant=`

The properties when running via main method, either from IDE or gradle (as 
opposed to integration or functional test) are loaded from 
`~/.config/raido-v2/api-svc-env.properties`, see
[ApiConfig](/src/main/java/raido/spring/config/ApiConfig.java).


## Running a local DB

See [db/readme.md](../db/readme.md)


## Configuration

Once you've got a DB, the the `api-svc` needs to configured.

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
