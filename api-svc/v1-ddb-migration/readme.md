

## Setup and config

Launch local PG container to migrate to.
```
docker rm ddb-v1-schema; docker run --name ddb-v1-schema -p 7432:5432 -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD= -e POSTGRES_DB=raid-v1  postgres
```
This won't actually run, because no password.

Give the above command a password then edit 
`~/.config/raido-v2/v1-ddb-migration.gradle`:
```groovy
  raidV1PgPassword="xxx"
```

DB migrations are in 
[src/main/resources/db/migration](./src/main/resources/db/migration)
 - the versions start with `V2` because `V1` is the flyway `baseline` version.

On a Windows 10 machine with Docker Desktop, the postgres 
## Tasks

* checkS3Files
  * run through files checking for bad data, output errors to file 
* importS3Files
  * import all data into tables
  * the "import" is a "merge" operation, so it's re-runnable - it'll just 
  overwrite rows (where row is identified by the Handle primary key)


## Local development

Windows devs usually use docker desktop for running containers.


### IDE usage 

If you're using your IDE to run Gradle tasks, the build script set stuff
up for you (timezone, file-encoding, DB url, etc.)

Though you still need to configure credentials like DB password.


#### Generic JVM stuff (TimeZone/FileEncoding, etc.)

Remember to set your IDE up to force UTC, UTF-8 - see the gradle build for 
any other default system properties.

#### Database stuff

Need to configure sysProps like url, user, password.

Again, build script does it for you (`raidV1Pg` props). 


## Git history

Manually imported from /au-research/raido-v2-aws-private #8ca84286.  
Look there for earlier git history.

Imported because I plan to use the v1 schema directly at first.  Not sure when
I'm going to do the formal "v1-schema" to "v2-schema" migration.

----


## DynamoDb stuff

https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.LowLevelAPI.html#Programming.LowLevelAPI.DataTypeDescriptors


Some code to read the json format
https://stackoverflow.com/a/48916925/924597
But AWS got rid of it:
https://github.com/aws/aws-sdk-java-v2/issues/2628
