import React from "react";
import { ContainerCard } from "Design/ContainerCard";

export function MetaDataContainer({metadata}: {metadata: any}){
  return <ContainerCard title={"Metadata"}>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
  </ContainerCard>

}

export function formatMetadata(metadata: any): string{
  if( !metadata ){
    return "No metadata";
  }
  
  if( typeof metadata === 'string' ){
    metadata = JSON.parse(metadata);
  }
  
  return JSON.stringify(metadata, nullFieldReplacer, 2);
}

export function nullFieldReplacer(key: any, value: any): any{
  /* "skip" (do not render) fields that do not have a value.
  See https://stackoverflow.com/a/41116529/924597 */
  return value ?? undefined
}

