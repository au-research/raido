import { styled } from "@mui/system";
import { List, ListItem, TableRow } from "@mui/material";
import { TypographyProps } from "@mui/material/Typography";
import { TextSpan } from "Component/TextSpan";
import {
  formatLocalDateAsIso,
  formatLocalDateAsIsoShortDateTime
} from "Util/DateUtil";
import React from "react";
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

export function DateTimeDisplay({date, ...props}: {
  date?: Date,
} & TypographyProps ){
  return <TextSpan {...props}>
    {formatLocalDateAsIsoShortDateTime(date)}
  </TextSpan>
}

export function DateDisplay({
  date,
  ...props
}: {
  date?: Date | string;
} & TypographyProps) {
  let value = "";

  // if input is a date, then format it as an ISO string
  if (date && date instanceof Date) {
    value = formatLocalDateAsIso(date);
  }

  // if input is a string, and can be parsed as a date, do so and format it as an ISO string
  if (date && new Date(date) instanceof Date) {
    value = formatLocalDateAsIso(new Date(date));
  }

  return (
    <TextSpan noWrap {...props}>
      {value}
    </TextSpan>
  );
}

export function BooleanDisplay({value, ...props}: {
  value: boolean,
} & TypographyProps ){
  return <TextSpan {...props}>
    {value ? "Yes" : "No"}
  </TextSpan>
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

