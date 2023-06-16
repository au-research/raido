import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import {
  AlternateIdentifierBlock,
  ContributorBlock,
  DescriptionBlock,
  instanceOfLegacyMetadataSchemaV1,
  instanceOfRaidoMetadataSchemaV1,
  LegacyMetadataSchemaV1,
  LegacyMetadataSchemaV1FromJSON,
  OrganisationBlock,
  PublicRaidMetadataSchemaV1,
  RaidoMetadataSchemaV1,
  RaidoMetadataSchemaV2,
  RaidoMetadataSchemaV1FromJSON,
  RaidoMetaschema,
  RelatedObjectBlock,
  RelatedRaidBlock,
  SpatialCoverageBlock,
  SubjectBlock,
  TitleBlock,
  TraditionalKnowledgeLabelBlock,
  RaidoMetaschemaV2,
  RaidoMetadataSchemaV2ToJSON,
  RaidoMetadataSchemaV2FromJSON
} from "Generated/Raidv2";
import {parse} from "json5";

export function MetaDataContainer({metadata}: {metadata: any}){
  return <ContainerCard title={"Metadata"}>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
  </ContainerCard>
}

export function convertMetadata(value: any): RaidoMetadataSchemaV1|RaidoMetadataSchemaV2|LegacyMetadataSchemaV1 {
  if( typeof value === 'string' ){
    const parsed = JSON.parse(value);

    if( parsed.metadataSchema === RaidoMetaschema.LegacyMetadataSchemaV1 ){
      return LegacyMetadataSchemaV1FromJSON(parsed);
    }
    else if( parsed.metadataSchema === RaidoMetaschema.RaidoMetadataSchemaV1 ){
      return RaidoMetadataSchemaV1FromJSON(parsed);
    }
    else if( parsed.metadataSchema === RaidoMetaschemaV2.RaidoMetadataSchemaV2 ){
      return RaidoMetadataSchemaV2FromJSON(parsed);
    }
  }

  /* openapi "instanceof" functions expect an object can be used with the
  "in" operator. */
  if( instanceOfRaidoMetadataSchemaV1(value) ){
    return value as RaidoMetadataSchemaV1;
  }

  if( instanceOfLegacyMetadataSchemaV1(value) ){
    return value as LegacyMetadataSchemaV1;
  }

  console.error("expected to be a metadata", value);
  throw new Error("could not convert metadata: " + value);
}

export function formatMetadata(value: any): string{
  if( !value ){
    return "No metadata loaded";
  }

  const metadata = convertMetadata(value);

  return JSON.stringify(metadata, nullFieldReplacer, 2);
}

export function nullFieldReplacer(key: any, value: any): any{
  /* "skip" (do not render) fields that do not have a value.
  See https://stackoverflow.com/a/41116529/924597 */
  return value ?? undefined
}

export function getFirstPrimaryDescription(
  metadata: RaidoMetadataSchemaV2
): undefined | DescriptionBlock {
  return metadata.descriptions?.
    find(i=> i.type === "Primary Description");
}

export function getFirstLeader(
  metadata: RaidoMetadataSchemaV2
): undefined | ContributorBlock {
  return metadata.contributors.find(i=> {
    return i.positions?.find(j => {
      return j.position === 'Leader';
    });
  });
}

export function getLeadOrganisation(
  metadata: RaidoMetadataSchemaV2
): undefined | OrganisationBlock {
  return metadata.organisations?.find(i=> { // not nullable?
    return i.roles.find(j => {
      return j.role === 'Lead Research Organisation';
    });
  });
}
export function getPrimaryTitle(
  metadata: RaidoMetadataSchemaV2|PublicRaidMetadataSchemaV1
): TitleBlock {
  let primary = metadata.titles.find(i=> i.type === "Primary Title");
  if( !primary ){
    throw new Error("no primary title could be found on metadata");
  }
  return primary;
}

export function getFirstSubject(
  metadata: RaidoMetadataSchemaV2
): undefined | SubjectBlock {
  if (metadata.subjects?.length) {
    return metadata.subjects[0]
  }
}

export function getFirstRelatedRaid(
  metadata: RaidoMetadataSchemaV2
): undefined | RelatedRaidBlock {
  if (metadata.relatedRaids?.length) {
    return metadata.relatedRaids[0]
  }
}

export function getFirstRelatedObject(
  metadata: RaidoMetadataSchemaV2
): undefined | RelatedObjectBlock {
  if (metadata.relatedObjects?.length) {
    return metadata.relatedObjects[0]
  }
}

export function getFirstAlternateIdentifier(
  metadata: RaidoMetadataSchemaV2
): undefined | AlternateIdentifierBlock {
  if (metadata.alternateIdentifiers?.length) {
    return metadata.alternateIdentifiers[0]
  }
}

export function getFirstSpatialCoverage(
  metadata: RaidoMetadataSchemaV2
): undefined | SpatialCoverageBlock {
  if (metadata.spatialCoverages?.length) {
    return metadata.spatialCoverages[0]
  }
}

export function getFirstTraditionalKnowledgeLabel(
  metadata: RaidoMetadataSchemaV2
): undefined | TraditionalKnowledgeLabelBlock {
  if (metadata.traditionalKnowledgeLabels?.length) {
    return metadata.traditionalKnowledgeLabels[0]
  }
}