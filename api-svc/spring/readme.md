
## Running `Api.main()`

If you want to start the app from your IDE, remember to specify JVM options:

`-Duser.timezone=UTC -Dfile.encoding=UTF-8 -Duser.language= -Duser.country= -Duser.variant=`

The properties when running via main method, either from IDE or gradle (as 
opposed to integration or functional test) are loaded from 
`~/.config/raido-v2/api-svc-env.properties`, see
[ApiConfig](/src/main/java/raido/spring/config/ApiConfig.java).


## Running a local DB

See [api-svc/readme.md](../readme.md) for instructions on running a postgres 
container and creating the schemata.


## Configuration

Once you've got a DB, the the `api-svc` needs to configured.
Local dev uses a separate file like this because this is how the serivce is 
run - there's no Gradle files or build involved when running the service in 
a real deployment environment. 

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

