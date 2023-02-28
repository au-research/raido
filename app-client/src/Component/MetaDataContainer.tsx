import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import {
  ContributorBlock,
  DescriptionBlock,
  instanceOfLegacyMetadataSchemaV1,
  instanceOfRaidoMetadataSchemaV1,
  LegacyMetadataSchemaV1,
  LegacyMetadataSchemaV1FromJSON, OrganisationBlock,
  RaidoMetadataSchemaV1,
  RaidoMetadataSchemaV1FromJSON,
  RaidoMetaschema, SubjectBlock,
  TitleBlock
} from "Generated/Raidv2";

export function MetaDataContainer({metadata}: {metadata: any}){
  return <ContainerCard title={"Metadata"}>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
  </ContainerCard>
}

export function convertMetadata(value: any): 
RaidoMetadataSchemaV1|LegacyMetadataSchemaV1 {
  if( typeof value === 'string' ){
    const parsed = JSON.parse(value);
    
    if( parsed.metadataSchema === RaidoMetaschema.LegacyMetadataSchemaV1 ){
      return LegacyMetadataSchemaV1FromJSON(parsed);
    }
    else if( parsed.metadataSchema === RaidoMetaschema.RaidoMetadataSchemaV1 ){
      return RaidoMetadataSchemaV1FromJSON(parsed);
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
  metadata: RaidoMetadataSchemaV1
): undefined | DescriptionBlock {
  return metadata.descriptions?.
    find(i=> i.type === "Primary Description");
}

export function getFirstLeader(
  metadata: RaidoMetadataSchemaV1
): undefined | ContributorBlock {
  return metadata.contributors.find(i=> {
    return i.positions?.find(j => {
      return j.position === 'Leader';
    });
  });
}

export function getLeadOrganisation(
  metadata: RaidoMetadataSchemaV1
): undefined | OrganisationBlock {
  return metadata.organisations?.find(i=> { // not nullable?
    return i.roles.find(j => {
      return j.role === 'Lead Research Organisation';
    });
  });
}
export function getPrimaryTitle(
  metadata: RaidoMetadataSchemaV1
): TitleBlock {
  let primary = metadata.titles.find(i=> i.type === "Primary Title");
  if( !primary ){
    throw new Error("no primary title could be found on metadata");
  }
  return primary;
}

export function getFirstSubject(
  metadata: RaidoMetadataSchemaV1
): undefined | SubjectBlock {
  if (metadata.subjects?.length) {
    return metadata.subjects[0]
  }
}
