import React, {EventHandler, SyntheticEvent} from "react";
import {stopClick} from "Util/EventUtil";
import {IconButton, keyframes} from "@mui/material";
import {Refresh} from "@mui/icons-material";

const spin = keyframes({
  // these used to have `scale(1)` too, but not sure why
  from: {transform: 'rotate(0deg);'},
  to: {transform: 'rotate(360deg);'},
});

export function RefreshIconButton(props:{
  disabled?: boolean,
  refreshing?: boolean,
  onClick?: EventHandler<SyntheticEvent>,
}){
  const handleOnClick = (event: SyntheticEvent)=>{
    stopClick(event);
    props.onClick?.(event);
  };

  let isDisabled = props.disabled;
  if( props.disabled === undefined || props.disabled == null ){
    isDisabled = props.refreshing;
  }
  
  return <IconButton href="#" size="large" 
    disabled={isDisabled} onClick={handleOnClick} 
  >
    <Refresh color={"primary"} sx={{
      animation: props.refreshing ? `${spin} 2s infinite linear` : undefined
    }}/>
  </IconButton>;
}
