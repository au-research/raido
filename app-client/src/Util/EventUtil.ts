import {SyntheticEvent} from "react";

export function stopClick(e?: SyntheticEvent<any>){
  if( !e ){
    return;
  }
  e.preventDefault();
  e.stopPropagation();
}