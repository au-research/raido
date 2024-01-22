import React, { ReactNode } from "react";
import { TextSpan } from "Component/TextSpan";

/**
 Initial used mui <TextField/> for this, set to non-editable.  But it had a few
 issues:
 - static width, did not expand or shrink with content size
 - focusable widgets, when you click on fields they would highlight and were
 also in the focus list for tab traversal
 
 Improvements:
 - the styling needs a lot of work and doesn't look good on mobile either
 - the container looks wonky on mobile because stuff is centered, I think 
 this thinkg should be a grid, rather than felxbox?
 
 Maybe re-think the whole approach, but I dunno what a better "info display"
 widget would look like.  
 */
export function InfoField(
  {id, label, value}:{
    id?: string,
    label: string,
    value: string | ReactNode | undefined,
  }
){
  return <fieldset style={{
    borderRadius: ".5em", borderWidth: "1px", 
    // default is groove, doesn't work well with a 1px border
    borderStyle:"solid", 
    borderColor: "lightgrey",
    /* margin and padding are just about keeping the field "tight", this might
    be my developer bias kicking in - this "tightness" might actually be 
    hurting readability. */
    margin: ".2em", 
    padding: "0 .35em 0 .5em", // trbl
  }}>
    <legend style={{
      // I just think it looks nice, no reason.
      fontSize: ".8em",
      /* when the label is longer than the content, this makes the label be 
      just a little bit more one the left side, which looks nicer to me. */
      marginRight: ".5em"
    }}>{label}</legend>
    <TextSpan id={id}>{value || ''}</TextSpan>
  </fieldset>
}


/**
 "List" does seem like the best model of what we're actually trying to do: 
 present a list of info fields.
 */
export function InfoFieldList({children}:{children: ReactNode}){
  return <div style={{
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-between",
    alignContent: "space-between",
  }}>
    {children}
  </div>
}
