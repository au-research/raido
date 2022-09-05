
Project loads files from local filesystem into the `raid_v1_import` schema in 
a PG database, see [db/readme.md](../../db/readme.md)

The files are expected to be exported from DDB to S3, then downloaded to your
local machine.

Uses Groovy because this isn't prod code and I was dealing with JSON blobs,
so groovy was convenient because of prior experience.


## Setup and config

### S3 Export files

[Main.groovy](./src/main/groovy/raid/ddb_migration/Main.groovy) expects the 
S3 export files to be in the relative subdir `./build/ddb-migration-data`.

i.e. if you cloned to `/repo-location/raido-v2`, then the S3 directories are 
expected to be in `/repo-location/build/ddb-migration-data`.
The script expects you to rename the directory exported from AWS to one of
the expected tables names.

e.g. if you export the main `RAID` table and download from the directory from
S3 so that the on-disk structure looks like  
`/download-location/AWSDynamoDB/01658105827587-cb141f56/manifest-files.json`,
you need to rename the directory so that the same file can be found at
`/repo-location/build/ddb-migration-data/raid-table/manifest-files.json`

For how to do an export to get those files, look in 
[ddb-s3-export.md](./doc/ddb-s3-export.md)

### Local DB to import into

Make sure DB already exists, as per [db/readme.md](../../db/readme.md).
Currently, the `importS3Files` task does the import at the user configured
in `api-svc-db.gradle`

DB migrations are in 
[src/main/resources/db/migration](./src/main/resources/db/migration)
 - the versions start with `V2` because `V1` is the flyway `baseline` version.

## Tasks

* checkS3Files
  * run through files checking for bad data, output errors to file 
* importS3Files
  * import all data into tables
  * the "import" is a "merge" operation, so it's re-runnable - it'll just 
  overwrite rows (where row is identified by the Handle primary key)


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
