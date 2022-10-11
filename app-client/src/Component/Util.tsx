import {styled} from "@mui/system";
import {TableRow} from "@mui/material";
import { TypographyProps } from "@mui/material/Typography";
import { TextSpan } from "Component/TextSpan";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import React from "react";
import { AuthzTokenPayload } from "Shared/ApiTypes";
import { Config } from "Config";

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

function mapProviderName(payload: AuthzTokenPayload): string {
  if( payload.clientId === Config.aaf.clientId ){
    return "AAF";
  }
  else if( payload.clientId === Config.google.clientId ){
    return "Google";
  }
  else if( payload.clientId === "RAIDO_API" ){
    return "Raido API";
  }
  else {
    console.log("unknown idp clientId", payload);
    return "Unknown";
  }
}