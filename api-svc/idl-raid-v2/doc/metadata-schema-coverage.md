
# Broad things 

## Schema normalisation 

Due to the inconsistencies in the current schema and API implementation, we 
need to update some of the already implemented field names, see
[metadata-block-field-naming.md](https://github.com/au-research/raid-metadata/blob/main/doc/metadata-block-field-naming.md)

This will be done as a new schema definition: `PublicRaidMetadataSchemaV2` 
will be published as a non-backward-compatible new schema.

There will be new endpoints added in the authenticated API for dealing with the
new schema.

The "landing page" will be updated to return `PublicRaidMetadataSchemaV2` data,
via an internal mapping process for dealing with `PublicRaidMetadataSchemaV1` 
raids.

## Validation of raid data via integrations

For all the blocks marked "complete" where the values point to things like 
ORCID, ROR, etc.  We have not implemented and validation of the input data
that involves integrating with those services.

For example, the incoming ORCID urls in a raid are validated to be conformant
to the format and check-digit level - but may still point at an ORCID that does
not exist.

## Missing language fields

Some blocks haven't implemented the language fields, they will be added ASAP
(backwards compatible change).


---

# Metadata Blocks 

## 1. Identifier - Complete

Complete.

## 2. / 3. Date  - Complete

Complete.

## 4. Title - Complete

Complete, except missing language fields.

## 5. Description - Complete

Complete, except missing language fields.

## 6. Contributor - Complete

Complete, except missing language fields.

## 7. Organisation - Complete

Complete, except missing language fields.

## 8. RelatedObject - Complete

Implemented for DOI. Does not validate object exists.

## 9. AlternateIdentifier - Complete

Complete.

## 10. AlternateURL - Complete

Complete.

## 11. RelatedRaid - Complete

Complete. Needs extra validation for other Raid agencies.

## 12. Subject - Complete

In progress.

## 13. TraditionalKnowledgeLabel - Not started

Not started.

## 14. SpatialCoverage - Not started

Not started.

## 15. Access - Complete

Complete, except missing language fields.







