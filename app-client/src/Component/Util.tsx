import {styled} from "@mui/system";
import { List, ListItem, TableRow } from "@mui/material";
import { TypographyProps } from "@mui/material/Typography";
import { TextSpan } from "Component/TextSpan";
import {
  formatLocalDateAsIso,
  formatLocalDateAsIsoShortDateTime
} from "Util/DateUtil";
import React from "react";
import { AuthzTokenPayload } from "Shared/ApiTypes";
import { Config } from "Config";
import { ValidationFailure } from "Generated/Raidv2";

export const AlternatingTableRow = styled(TableRow)(({theme}) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover,
  }
}));

export function raidoTitle(pageName: string){
  return `RAiD - ${pageName}`;
}

export function RoleDisplay({role, ...props}: {
  role: string
} & TypographyProps ){
  return <TextSpan {...props}>{mapRoleToDisplay(role)}</TextSpan>
}

export function IdProviderDisplay({payload, ...props}: {
  payload: AuthzTokenPayload
} & TypographyProps ){
  return <TextSpan {...props}>{mapProviderName(payload)}</TextSpan>
}

export function mapRoleToDisplay(role: string): string{
  if( role === "SP_ADMIN" ){
    return "Administrator";
  }
  else if( role === "SP_USER" ){
    return "User"
  }
  else if( role === "OPERATOR" ){
    return "Operator"
  }
  console.log("unknown role", role);
  return "Unknown";
}

export function DateTimeDisplay({date, ...props}: {
  date?: Date,
} & TypographyProps ){
  return <TextSpan {...props}>
    {formatLocalDateAsIsoShortDateTime(date)}
  </TextSpan>
}

export function DateDisplay({date, ...props}: {
  date?: Date,
} & TypographyProps ){
  return <TextSpan noWrap {...props}>
    {formatLocalDateAsIso(date)}
  </TextSpan>
}

export function BooleanDisplay({value, ...props}: {
  value: boolean,
} & TypographyProps ){
  return <TextSpan {...props}>
    {value ? "Yes" : "No"}
  </TextSpan>
}

function mapProviderName(payload: AuthzTokenPayload): string {
  const displayName = mapClientIdToDisplay(payload.clientId);
  
  if( displayName === unknownClientId ){
    console.log("payload for unknown idp clientId", payload);
  }
  
  return displayName
}

const unknownClientId = "Unknown";

export function mapClientIdToDisplay(clientId: string): 
string | typeof unknownClientId {
  // clientId for this is dody, I think we need to add provider to payload 
  if( clientId === Config.aaf.clientId ){
    return "AAF";
  }
  else if( clientId === Config.google.clientId ){
    return "Google";
  }
  else if( clientId === Config.orcid.clientId ){
    return "ORCiD";
  }
  else if( clientId === "RAIDO_API" ){
    return "Raido API";
  }
  else {
    console.log("unknown idp clientId", clientId);
    return unknownClientId;
  }
}

export function ValidationFailureDisplay({failures}: {
  failures: ValidationFailure[]
}){
  return <List>{
    failures.map(i => <ListItem key={i.fieldId}>
      <TextSpan color={"error"}>{i.fieldId} - {i.message}</TextSpan>
    </ListItem>)
  }</List>
}
