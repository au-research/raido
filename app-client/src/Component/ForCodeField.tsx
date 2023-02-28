import {TextField} from "@mui/material";
import React from "react";
import {TextSpan} from "Component/TextSpan";

/* I'm confused what the case should be 
https://info.orcid.org/brand-guidelines/ */
export const orcidBrand = "ORCID";
  
export type ValueChange =
  { value: string } 
;

export type ValueProblem = undefined | string;

export function ForCodeField({
  id, label, value, onValueChange, valueProblem, disabled, 
}:{
  id: string,
  label: string,
  value?: string,
  onValueChange: (e: ValueChange)=>void,
  valueProblem: ValueProblem,
  disabled?:boolean,
}){
  const [anchorEl, setAnchorEl] = 
    React.useState<HTMLButtonElement | null>(null);

  const popoverOpen = Boolean(anchorEl);
  const popoverId = popoverOpen ? id+'-forcode-popover' : undefined;

  const onPopoverClose = () => {
    setAnchorEl(null);
  };

  const mappedValue = mapInvalidForCodeChars(value ?? "");
  
  const textField = <TextField id={id}
    inputProps={{
      //make the text field line up with the static url a bit better
      style:{paddingTop: ".5em", paddingLeft: ".25em"}
    }}
    variant="filled" autoCorrect="off" autoCapitalize="on"
    size={"small"}
    
    disabled={disabled}
    value={mappedValue}
    onChange={(e) => {
      /* Can't use mappedValue here, it's based on param value, not user input.
       Still have to map in here because we want mapping to turn 'x'->'X' */
      let mappedEventValue = mapInvalidForCodeChars(e.target.value);
      onValueChange({value: forCodePrefixUrl + mappedEventValue});
    }}
    error={!!valueProblem}
  />

  return <fieldset style={{
    display: "flex", alignItems: "center",
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
    }}>
      {valueProblem ? label + " - " + valueProblem : label}
    </legend>

    <TextSpan
      // make the static text disappear on small devices
      sx={{ display: { xs: 'none', sm: 'block' } }}
    >{forCodePrefixUrl}</TextSpan>
    {textField}
  </fieldset>
}

const forCodePrefixUrl = "https://linked.data.gov.au/def/anzsrc-for/2020/";

function insertString(value: string, insertedValue: string, insertedPosition: number): string {
  
  if( value.length < insertedPosition ){
    return value;
  }
  
  return value.substring(0, insertedPosition) + insertedValue + 
    value.substring(insertedPosition);
}

/* 
poorly named, doesn't just map characters any more.
Gets called 2 or 3 times in a single event:
* in event handler - during onChange(), in order to construct the value to 
  notify caller of the change with the "new value"
* in caller - because they call findOrcidProblem() so they can know if there's 
  a validation problem (they may also do their own extra validation)
* in render function - to map it down to the value to display 
*/
function mapInvalidForCodeChars(id: string): string{
  id = id.replace(forCodePrefixUrl, "");

  return id;
}

export function findForCodeProblem(id: string): string|undefined{

  if (id) {
    id = mapInvalidForCodeChars(id);
    let code = id.substring(id.lastIndexOf('/') + 1)

    if (code.match(/[^\d]/)) {
      return "can only include numbers";
    }
  }

  return undefined;
}
