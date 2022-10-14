import { TextField, TextFieldProps } from "@mui/material";
import { formatLocalDateAsIso } from "Util/DateUtil";
import React, { useState } from "react";

export function DateTextField({value, onChange, ...props}: {
  value?: Date,
  onChange: (newDate: Date)=>void;
} & TextFieldProps
){
  const {disabled} = props;
  const [textValue, setTextValue] = useState(formatLocalDateAsIso(value));
  
  return <TextField variant="outlined"
    {...props}
    disabled={disabled}
    value={textValue}
    onChange={e=>{
      
      //parseServerDate()
      
      onChange(e);
    }}
  />
  
}
