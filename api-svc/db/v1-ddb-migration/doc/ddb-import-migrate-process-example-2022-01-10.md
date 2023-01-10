A full example of what I did to import the latest data from RAID legacy system
into Raido DEMO env.

* follow [ddb-s3-export.md](./ddb-s3-export.md)
  * I put all 4 files in a subdirectory, so the export bucket target was: 
    `sto-raid-dev-manual/2023-01-10` 
* used Cloudberry S3 explorer to copy the files to my machine
* renamed the directory names from <random> to `raid-table` etc.
  * by eyeballing the `manifest-summary.json` file
* moved the directories to `.../raido-v2/s3/ddb-migration-data`
* ran local gradle task `importS3Files` to test that it all worked
* used Cloudberry to upload files to raido-demo account under 
  `demostage-raidodbmigrate-ddbs3exporta4fa17d2-1u2h6pmd5edg7/sto-2023-01-10/`
* changed AWS CDK code in `RaidoDbMigrateCodeBuildStack.ddbS3Import()` to 
  copy s3 files from the `2023-01-10` directory
* pushed the change for CDK to update the codebuild job
  * you could shortcut this by running the CDK `deploy` from your local machine
    or even editing the codebuild project directly by hand (bad idea, but you 
    could do it)
* ran the codebuild project `RaidoDdbS3Import78603AEF-TZbeDh36qMmd` 
  * i.e. signed into AWS console and clicked "start build"
  * this only imports the data from the S3 files into the `v1-ddb-migration`
  schema table in postgres DB
* signed in to Raido, as user associated to `Raido` service point, created
  an API key (must be role `SP_ADMIN`)
  * generated the API token and copied to clipboard
* update the API key in the SecretsManager with the new API token
  * secret: `migrateImportedRaidKeySecre-jWl6QmwZFw5W`
* ran the codebuild project `MigrateImportedRaids774EF71-sCSzdMCctHfe`
  * this takes the raid data in the `v1-ddb-migration` schema and calls the 
  `/migrate-legacy-raid` endpoint (using the API key) to migrate each raid, 
  one at a time
* sign-in to Raido user associated with either NotreDame or RDM service points
  to verify that new raids were imported

