import { IconButton, Popover, TextField } from "@mui/material";
import React, { useState } from "react";
import { TextSpan } from "Component/TextSpan";
import { OrcidSvgIcon } from "Component/Icon";

export type ValueChange =
  { valid: true, value: string} |
  { valid: false, value: string, problem: string}
;

/*
At the moment, when you use this on a narrow screen, it's the textfield that
gets shorter - should be the static "url".  Probably ought to just make the 
static part responsively disappear when screen gets narrow. 
 */
export function OrcidField({
  id, label, value, onValueChange, disabled, 
}:{
  id: string,
  label: string,
  onValueChange: (e: ValueChange)=>void,
  value?: string,
  disabled?:boolean,
}){
  const [fieldValue, setFieldValue] = useState(
    mapInvalidOrcidChars(value ?? "") );
  const [anchorEl, setAnchorEl] = 
    React.useState<HTMLButtonElement | null>(null);

  const popoverOpen = Boolean(anchorEl);
  const popoverId = popoverOpen ? id+'-orcid-popover' : undefined;
  const onOrcidIconClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const onPopoverClose = () => {
    setAnchorEl(null);
  };

  const orcidProblem = findOrcidProblem(fieldValue);

  const textField = <TextField id={id}
    inputProps={{
      //make the text field line up with the static url a bit better
      style:{paddingTop: ".5em", paddingLeft: ".25em"}
    }}
    variant="filled" autoCorrect="off" autoCapitalize="on"
    size={"small"}
    
    disabled={disabled}
    value={fieldValue}
    onChange={(e) => {
      let filteredValue = mapInvalidOrcidChars(e.target.value);
      setFieldValue(filteredValue);

      const orcidProblem = findOrcidProblem(filteredValue);
      const orcidUrl = orcidPrefixUrl + filteredValue;
      if( orcidProblem ){
        onValueChange({
          valid: false, value: orcidUrl, problem: orcidProblem });
      }
      else {
        onValueChange({valid:true, value: orcidUrl});
      }
    }}
    error={!!orcidProblem}
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
      {orcidProblem ? label + " - " + orcidProblem : label}
    </legend>
    
    <TextSpan>{orcidPrefixUrl}</TextSpan>
    {textField}
    <IconButton onClick={onOrcidIconClick} disabled={disabled} 
      style={{
        /* This is what pushes the icon over to the right hand side. */
        marginLeft: "auto",
        /* dodgy hack to make the icon line up with other input widgets like
        the calendar and dropdown icon.  It makes the button be "oblong shaped"
        (hover over it with mouse) - but I can't figure out how to reposition 
        it. */
        paddingRight: ".1em"          
      }}
    >
      <OrcidSvgIcon/>
    </IconButton>
    <Popover id={popoverId}
      open={popoverOpen}
      anchorEl={anchorEl}
      onClose={onPopoverClose}
      anchorOrigin={{vertical: 'bottom', horizontal: 'left',}}
    >
      <TextSpan>Currently, only ORCiD is supported.</TextSpan>
    </Popover>    
  </fieldset>
}

const orcidPrefixUrl = "https://orcid.org/";

function insertString(value: string, insertedValue: string, insertedPosition: number): string {
  
  if( value.length < insertedPosition ){
    return value;
  }
  
  return value.substring(0, insertedPosition) + insertedValue + 
    value.substring(insertedPosition);
}

export function mapInvalidOrcidChars(id: string): string{
  /* try to make it work if someone copy/pastes a full url like 
   "https://orcid.org/1231-1231-1234-1234" */
  id = id.replace(orcidPrefixUrl, "");
  
  // improve - only the last char can actually be an X 
  id = id.toUpperCase().replace(/[^\dX-]/g, '');

  // reinsert the dashes to break it up into 4 quads
  // this doesn't quite work: consider having a valid value then repeatedly 
  // hitting backspace - you can't delete the dash
  //id = insertString(id, '-', 4);
  //id = insertString(id, '-', 9);
  //id = insertString(id, '-', 14);
  
  return id;
}

export function findOrcidProblem(id: string): string|undefined{
  if( !id ){
    return "must be set"
  }
  if( id.length > 4 && id[4] !== '-' ){
    return "5th character must be a dash"
  }
  if( id.length > 9 && id[9] !== '-' ){
    return "10th character must be a dash"
  }
  if( id.length > 14 && id[14] !== '-' ){
    return "12th character must be a dash"
  }
  if( id.length < 19 ){
    return "too short"
  }
  if( id.length > 19 ){
    return "too long"
  }

  let xDigitPosition = id.indexOf('X');
  if( xDigitPosition > 0 && xDigitPosition < id.length-1 ){
    return "only the last character can be an X"
  }
  
  // add checksum logic?

  return undefined;
}
