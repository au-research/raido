import {styled} from "@mui/system";
import {TableRow} from "@mui/material";
import { TypographyProps } from "@mui/material/Typography";
import { TextSpan } from "Component/TextSpan";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import React from "react";

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

export function mapRoleToDisplay(role: string): string{
  if( role === "SP_ADMIN" ){
    return "Administrator";
  }
  else if( role === "SP_USER" ){
    return "User"
  }
  else if( role === "Operator" ){
    return "Operator"
  }

  return "Unknown";
}

export function DateTimeDisplay({date, ...props}: {
  date?: Date,
} & TypographyProps ){
  return <TextSpan {...props}>
    {formatLocalDateAsIsoShortDateTime(date)}
  </TextSpan>
}