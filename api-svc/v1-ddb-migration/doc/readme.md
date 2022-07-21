Did an DDB export to S3, downloaded manually.

Export:
* Destination: sto-raid-dev-manual
* This AWS account
* Export from an earlier point
  * can't export multiple tables at once
  * keep data consistent between tables by picking a specific timestamp
* Format: Amazon ION 
  * because it's more like normal JSON than DDB format 

Tables to export:
* RAiD-MetadataTable-5X1IHWPICN82
* RAiD-RAiDLiveDB-1SX7NYTSOSUKX-AssociationIndexTable-1EMNYHDPK9NBP
* RAiD-RAiDLiveDB-1SX7NYTSOSUKX-RAiDTable-1PO1W2ASWY0OV
* RAiD-TokenTable-1P6MFZ0WFEETH

As per https://confluence.ardc.edu.au/display/~stolley/Raid+V1+-+DDB+table+analysis

