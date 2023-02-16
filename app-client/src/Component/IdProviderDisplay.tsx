import { AuthzTokenPayload } from "Shared/ApiTypes";
import { TypographyProps } from "@mui/material/Typography";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { Config } from "Config";
import { orcidBrand } from "Component/OrcidField";

export type IdProvider = "AAF" | "Google" | typeof orcidBrand | "Raido API" | "Unknown";

export function IdProviderDisplay({payload, ...props}: {
  payload: AuthzTokenPayload
} & TypographyProps){
  return <TextSpan {...props}>{mapProviderName(payload)}</TextSpan>
}

function mapProviderName(payload: AuthzTokenPayload): string{
  const displayName = mapClientIdToIdProvider(payload.clientId);

  if( displayName === "Unknown" ){
    console.log("payload for unknown idp clientId", payload);
  }

  return displayName
}

export function mapClientIdToIdProvider(clientId: string): IdProvider{
  // clientId for this is dodgy, I think we need to add provider to payload 
  if( clientId === Config.aaf.clientId ){
    return "AAF";
  }
  else if( clientId === Config.google.clientId ){
    return "Google";
  }
  else if( clientId === Config.orcid.clientId ){
    return orcidBrand;
  }
  else if( clientId === "RAIDO_API" ){
    return "Raido API";
  }
  else {
    console.log("unknown idp clientId", clientId);
    return "Unknown";
  }
}