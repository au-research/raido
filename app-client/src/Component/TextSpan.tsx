import * as React from "react";
import {Typography} from "@mui/material";
import {TypographyProps} from "@mui/material/Typography";

export function TextSpan(
  props: TypographyProps
){
  return <Typography  component="span" {...props}>
    {props.children}
  </Typography>
}


