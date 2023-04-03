
Project loads files from local filesystem into the `raid_v1_import` schema in 
a PG database, see [db/readme.md](../../db/readme.md)

The files are expected to be exported from DDB to S3, then downloaded to your
local machine.

Uses Groovy because this isn't prod code and I was dealing with JSON blobs,
so groovy was convenient because of prior experience.


## Setup and config

### S3 Export files

[Main.groovy](./src/main/groovy/raid/ddb_migration/Main.groovy) expects the 
S3 export files to be in the relative subdir `s3/ddb-migration-data`.

i.e. if you cloned to `/repo-location/raido`, then the S3 directories are 
expected to be in `/repo-location/s3/ddb-migration-data`.
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

* `checkS3Files`
  * run through files checking for bad data, output errors to file 
* `importS3Files`
  * import all data into `raid_v1_import` tables
  * the "import" is a "merge" operation, so it's re-runnable - it'll just 
  overwrite rows (where row is identified by the Handle primary key)
* `importAllRaids`
  * imports all raids for specifically chosen service points
    * from `raid_v1_import.raid` table, into the `raido.raid` table by 
      calling the `migrate` endpoint
  * before running this you need to configure the "API import key", see 
    section below 
  * takes about 2 - 3 minutes to run on local dev machine (16K RDM raids) 
* `import1Raid`
  * does the same as import, but only for 1 raid for each SP
  * used to quickly check that the migration is working


## Raido API import key

* sign-in to Raido as a raido operator or admin
* add an api key, save it and generate the token
* add the api key to `~/.config/raido/api-svc-db-.gradle`
  * `apiSvcMigrationKey="xxx.yyy.zzz"`

## Git history

Manually imported from /au-research/raido-aws-private #8ca84286.  
Look there for earlier git history.


# V1.0 Production deployment problem

I had made changes to the V3, V4, and V5 sql files, expecting that I would
clean the DB out and re-run the creation from scratch in production.

I made mistake and didn't do the re-creation for the prod deployment.
That was a big mistake, and the changes to the files meant that the 
`flywayMigrate` would fail on the production database (reproced by doing a 
DB refresh from prod onto my own machine via `pg_restore`/`pg_dump`).

To fix that, I manually reverted the changes to the V3-5 files.
It seems I got very lucky with the prod deployment and it (accidentally) 
imported the correct RDM@UQ token. 

After that, I  added the V7 migration to change the primary key for the 
`token` table so that the int-tests would work properly.

I didn't worry about the other two tables (metadata and association_index) 
as they're not used anyway.

----


## DynamoDb stuff

https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Programming.LowLevelAPI.html#Programming.LowLevelAPI.DataTypeDescriptors


Some code to read the json format
https://stackoverflow.com/a/48916925/924597
But AWS got rid of it:
https://github.com/aws/aws-sdk-java-v2/issues/2628
