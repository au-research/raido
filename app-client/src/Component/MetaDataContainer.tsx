import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import {
  DescriptionBlock,
  instanceOfRaidoMetadataSchemaV1,
  RaidoMetadataSchemaV1,
  RaidoMetadataSchemaV1FromJSON,
  TitleBlock
} from "Generated/Raidv2";

export function MetaDataContainer({metadata}: {metadata: any}){
  return <ContainerCard title={"Metadata"}>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
  </ContainerCard>

}

export function convertRaidoMetadataSchemaV1(value: any): RaidoMetadataSchemaV1{
  if( typeof value === 'string' ){
    const parsed = JSON.parse(value);
    console.log("is string", parsed);
    return RaidoMetadataSchemaV1FromJSON(parsed);
  }

  /* openapi "instanceof" functions expect an object can be used with the
  "in" operator. */
  if( instanceOfRaidoMetadataSchemaV1(value) ){
    console.log("is instance");
    return value as RaidoMetadataSchemaV1;
  }

  console.error("expected to be a metadata", value);
  throw new Error("could not convert metadata: " + value);
}

export function formatMetadata(value: any): string{
  if( !value ){
    return "No metadata";
  }

  const metadata = convertRaidoMetadataSchemaV1(value);

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

export function getPrimaryTitle(
  metadata: RaidoMetadataSchemaV1
): TitleBlock {
  let primary = metadata.titles.find(i=> i.type === "Primary Title");
  if( !primary ){
    throw new Error("no primary title could be found on metadata");
  }
  return primary;
}