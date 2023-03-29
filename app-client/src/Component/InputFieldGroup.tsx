import React, { ReactNode } from "react";

export function InputFieldGroup({label, children}:{
  label: string, children: ReactNode
}){

  return <fieldset style={{
    display: "grid", gridGap: "1em",
    // default is groove, doesn't work well with a 1px border
    borderStyle:"solid",
    borderRadius: ".5em", borderWidth: "1px",
    borderColor: "lightgrey",

    /* when the first widget is an MUI InputField with a label, the fieldset
    legend and the input label were a little too crowded. */
    paddingTop: ".5em",
  }}>
    <legend style={{
      // I just think it looks nice, no reason.
      fontSize: ".8em",
      /* when the label is longer than the content, this makes the label be 
      just a little bit more one the left side, which looks nicer to me. */
      marginRight: ".5em"
    }}>
      {label}
    </legend>
    {children}
  </fieldset>
}