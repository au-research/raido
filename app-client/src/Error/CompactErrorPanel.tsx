import * as React from "react"
import {CSSProperties} from "react"

import {Paper} from "@mui/material";
import {isErrorInfo,} from "Error/ErrorUtil";

const log = console;

/** This component is intended to be displayed "inline" on a screen;
 ideally next to or in place of the UI element that triggered the error.
 The link to error screen allows the user to inspect the error in more
 detail without disrupting the layout/state of the originating screen (because
 it's a dialog that's shown "on top" of the screen, instead of replacing the
 screen - which would dismount it).

 The error property is declared optional so that callers can use the component
 without having to surround it with a conditional, if you pass no error
 nothing will be rendered.

 The temptation will arise to re-define the error prop like "ErrorInfo|any"
 so your calling component can declare its state as "ErrorInfo|CustomResult".
 Don't do that - use separate state for "last error" and "last result",
 it gives you more flexibility with regard to deciding what to show when
 instead of having that dictated by what the current content of your
 state variable happens to be.
 If you really want a single piece of state, go define a separate component
 that works as a wrapper HOC using conditional types or something.
 */
export function CompactErrorPanel({
  error,
  border = "redline",
}:{
  error?: any,
  border?: "redline" | "paper" | "h-pad",
}){
  // unwrap to shallow, stable values so useEffect() doesn't fire unexpectedly
  const {problem, message} = unwrapError(error);

  // must go *after* useEffect()
  if( !problem ){
    return null;
  }

  log.debug("compact error panel rendered: ", message, problem);

  let compactPanel = <span id="compactMessage">
    <pre>JSON.stringify(problem)</pre>
  </span>;

  if (border === "redline") {
    compactPanel = <span style={{...RedBorderStyle}}>{compactPanel}</span>
  }
  else if( border === "paper" ){
    compactPanel = <Paper style={{padding: ".5em"}}>{compactPanel}</Paper>;
  }
  else if( border === "h-pad" ){
    compactPanel = <span style={{...HPadBorderStyle}}>{compactPanel}</span>
  }

  return compactPanel;
}

function unwrapError(error?: any):{
  problem?: any,
  message: string
}{
  if( error === undefined || error === null ){
    return {
      problem: undefined,
      message: "no error",
    }
  }
  else if( isErrorInfo(error) ){
    return {
      problem: error.problem ?? "no problem",
      message: error.message ?? "no message",
    }
  }
  else {
    return {
      message: error.message ?? "any error",
      problem: error
    };
  }
}

export const RedBorderStyle: CSSProperties = {
  border: "solid red 1px", borderRadius: ".25em",
  marginBottom: ".25em"
};

export const HPadBorderStyle: CSSProperties = {
  paddingLeft: "1em", paddingRight: "1em"
};

