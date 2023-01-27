
## Running a local DB

See [api-svc/readme.md](../readme.md) for instructions on running a postgres 
container and creating the schemata.


## Configuring `api-svc-env.properties`

Once you've got a DB, the `api-svc` needs to be configured.
Local dev uses a separate file like this because this is how the service is 
run - there's no Gradle files or build involved when running the service in 
a real deployment environment. 

`~/.config/raido/api-svc-env.properties`
```properties
# so you can see errors more easily in network responses
redactErrorDetails=false

# connection details for your local DB
DatasourceConfig.url=jdbc:postgresql://localhost:7432/raido
DatasourceConfig.username=api_user
DatasourceConfig.password=<dbpassword>

# For legacy V1 endpoints
# just set it to any value
# Secret can be found in the shared keepass entry `/raid-v1/JWT secret`
RaidV1Auth.jwtSecret=<JWT secret>

# used for signing and verifying authz tokens
# set it to any sufficiently long string
# comma separated, but you only need one - just don't use a comma in a secret 
RaidV2Auth.jwtSecrets=<authz secrets>

# the secret for your AAF OIDC client
# If you want to use your own client: https://support.aaf.edu.au/support/solutions/articles/19000099312-how-to-connect-your-services-to-the-aaf-
# Secret can be found in the shared keepass entry `/id-services/AAF OIDC Relying Party`
AafOidc.clientSecret=<AAF client secret>

# the secret for your Google OIDC client
# If you want to use your own client: https://developers.google.com/identity/openid-connect/openid-connect
# Secret can be found in the shared keepass entry `/id-services/Google OAuth Client`
GoogleOidc.clientSecret=<Google client secret>

# The secret for the APIDS service (for minting handles).
# If you want to use your own account: https://ardc.edu.au/services/ardc-identifier-services/ardc-handle-service
# Secret can be found in the shared keepass entry 
# `/ardc-services/APIDS demo API secret`
Apids.secret=<APIDS API secret>

```


## Running `Api.main()` from IDE

If you want to start the app from your IDE, remember to specify JVM options:

`-Duser.timezone=UTC -Dfile.encoding=UTF-8 -Duser.language= -Duser.country= -Duser.variant=`

The properties when running via main method, either from IDE or gradle (as
opposed to integration or functional test) are loaded from
`~/.config/raido/api-svc-env.properties`, see
[ApiConfig](./src/main/java/raido/apisvc/spring/config/ApiConfig.java).


## Running `Api.main()` from command line

* `cd /repository/api-svc/spring/build/libs`
* `$JAVA_HOME/bin/java  \
  -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
  -Duser.language= -Duser.country= -Duser.variant= \
  -Dlogback.configurationFile=docker-logback.xml \
  -jar raido-api-svc.jar`
  * this was taken from [api-svc-start.sh](../docker/src/main/docker/script/api-svc-start.sh)

## Running integration tests

See [intTest/readme.md](./src/intTest/readme.md)


