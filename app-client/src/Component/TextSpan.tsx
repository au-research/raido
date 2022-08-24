import * as React from "react";
import {Typography} from "@mui/material";
import {TypographyProps} from "@mui/material/Typography";

export function TextSpan(
  props: TypographyProps
){
  return <Typography {...props} component="span">
    {props.children}
  </Typography>
}


