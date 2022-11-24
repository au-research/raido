
## API root file 

* `raido-openapi-3.0.yaml`
  * This is the "root" or "top-level" file for the Raido API.
  * Defines all the concrete URLs for endpoints - actual endpoint
    definitions (parameters, results, etc.) are found in other files that are 
    "linked to" via the `$ref` attribute.
  * Not expected to define any actual data structures.


## Data structures

* `shared.yaml`
  * Contains shared data structures.
  * Not expected to define any endpoints

* `metadata-block.yaml`
  * Contains "block" structures for the metadata schema
  * It's intended / hoped that these can be widely re-used across
  various endpoints - both Raido and RAID
  * Not expected to define any endpoints


## API definitions

* `raid-metadata-schema.yaml`
  * endpoints and structures that relate to global public "raid" metadata API 
  
* `raido-metadata-schema.yaml`
  * endpoints and structures that relate to Raido metadata API

* `public-experimental.yaml`
  * endpoints and structures that relate to various "non-authenticated" API 
  
* `admin-experimental.yaml`
  * endpoints and structures related to "admin" (i.e. "non-raid") API
  * lots of stuff, like the authorization model, sign-up flow, etc.


## Unused 

* `basic-raid-experimental.yaml`
  * miscellaneous stuff for Raido
  * currently empty, probably will just delete it
