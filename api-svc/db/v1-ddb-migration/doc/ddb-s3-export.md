Did an DDB export to S3, downloaded manually.

Export:
* from the DDB AWS console
  * click through table name
  * select / actions / export to s3
* Export details
  * Destination: sto-raid-dev-manual/2023-02-17
  * "This AWS account (005299621378)"
  * Additional settings
    * Export from an earlier point
      * can't export multiple tables at once
      * keep data consistent between tables by picking a specific timestamp
      * {date} / 00:00:00 
    * "Exported file format"
      * Amazon ION 
      * the script code is hardcoded to this format
        * because it's more like normal JSON than DDB format 

Tables to export:
* RAiD-MetadataTable-5X1IHWPICN82
  * put export files files into: s3/ddb-migration-data/metadata-table 
* RAiD-RAiDLiveDB-1SX7NYTSOSUKX-AssociationIndexTable-1EMNYHDPK9NBP
  * s3/ddb-migration-data/association-index-table
* RAiD-RAiDLiveDB-1SX7NYTSOSUKX-RAiDTable-1PO1W2ASWY0OV
  * s3/ddb-migration-data/raid-table
* RAiD-TokenTable-1P6MFZ0WFEETH
  * s3/ddb-migration-data/token-table

As per https://confluence.ardc.edu.au/display/~stolley/Raid+V1+-+DDB+table+analysis

After that, use cloudberry explorer to move each S3 directory to 
/repo/ddb-migration/<table name>