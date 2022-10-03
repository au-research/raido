import React, { ReactNode } from "react";
import { TextSpan } from "Component/TextSpan";
import { Color } from "Design/RaidoTheme";

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
    id: string,
    label: string,
    value: string | undefined,
  }
){
  return <fieldset style={{
    borderRadius: ".3em", borderWidth: "1px",
    borderColor: "lightgrey",
    margin: ".2em", // trbl
    padding: "0 .35em 0 .5em", // trbl
  }}>
    <legend style={{
      color: "lightslategrey",
      fontSize: ".8em",
    }}>{label}</legend>
    <TextSpan id={id} color={"lightslategrey"}>{value}</TextSpan>
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

// expermental
export function InfoFieldGrid({children}:{children: ReactNode}){
  return <div style={{
    display: "grid",
    //gridAutoColumns: "minmax(0, 1fr)",
    gridAutoColumns: "1fr",
    gridAutoFlow: "column",
    gap: "1em",
  }}>
    {children}
  </div>
}

export function InfoFieldColumnContainer({children}:{children: ReactNode}){
  return <div style={{
    columnCount: "2",
  }}>
    {children}
  </div>
}


