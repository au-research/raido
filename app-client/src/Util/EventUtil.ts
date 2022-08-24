import {SyntheticEvent} from "react";

export function stopClick(e?: SyntheticEvent<any>){
  if( !e ){
    return;
  }
  e.preventDefault();
  e.stopPropagation();
}

export function delay(ms: number, msg?: string):Promise<never> {
  return new Promise(resolve => {
    if( msg ) {
      console.trace(msg);
    }
    setTimeout(resolve, ms)
  });
}

