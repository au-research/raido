import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import {
  instanceOfMetadataSchemaV1,
  MetadataSchemaV1,
  MetadataSchemaV1FromJSON
} from "Generated/Raidv2";

export function MetaDataContainer({metadata}: {metadata: any}){
  return <ContainerCard title={"Metadata"}>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
  </ContainerCard>

}

export function convertMetadataSchemaV1(value: any): MetadataSchemaV1{
  if( typeof value === 'string' ){
    const parsed = JSON.parse(value);
    return MetadataSchemaV1FromJSON(parsed);
  }

  /* openapi "instanceof" functions expect an object can be used with the
  "in" operator. */
  if( instanceOfMetadataSchemaV1(value) ){
    return value as MetadataSchemaV1;
  }

  console.error("expected to be a metadata", value);
  throw new Error("could not convert metadata: " + value);
}

export function formatMetadata(value: any): string{
  if( !value ){
    return "No metadata";
  }

  const metadata = convertMetadataSchemaV1(value);

  return JSON.stringify(metadata, nullFieldReplacer, 2);
}

export function nullFieldReplacer(key: any, value: any): any{
  /* "skip" (do not render) fields that do not have a value.
  See https://stackoverflow.com/a/41116529/924597 */
  return value ?? undefined
}

