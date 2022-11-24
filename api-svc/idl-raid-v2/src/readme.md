# Concepts 

## "Raid" metadata vs "Raido" metadata

### Raido metadata

The Raido metaschema currently consists of two schema:
* "raido-metadata-schema-v1"
* "legacy-metadata-schema-v1"

There will be future "raido" schemas, like a "v3", "v4", etc. and maybe others
(I wasn't initially expecting to need a "legacy" schema, for example).


### Raid metadata

The Raid metaschema currently consists of the following two schema.

#### `PublicRaidMetadataSchemaV1`
All "Raido" metadata, when accessed via the public raid API, get mapped to 
this standard public schema, intended to be as close as possible to the 
published formal metadata definition.
  
* "legacy" metadata access this have placeholder values inserted for
  standard metadata schema fields that the legacy data doesn't actually 
  support.
* a future "raido v2" schema that we define for use by Raido customers,
  will have to have a mapping to this
* the process of mapping "raido" schema to this schema is also known as 
  "cross walking"

#### `PublicClosedMetadataSchemaV1`

Any given raid, whose Access block indicates that it is not "open", either 
because it's "closed" or "embargoed", is mapped to the closed schema.
 

# Files

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
