import {ErrorInfo} from "Error/ErrorUtil";
import { ButtonProps, Fab } from "@mui/material";
import React, {CSSProperties} from "react";
import Button from "@mui/material/Button";
import LinearProgress from "@mui/material/LinearProgress";
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import { useNavigation } from "Design/NavigationProvider";
import { Add } from "@mui/icons-material";


export const primaryButtonProps: ButtonProps = {
  variant: "contained", color: "primary",
  style: {textTransform: "none"},
};

export const secondaryButtonProps: ButtonProps = {
  variant: "outlined", color: "primary",
  style: {textTransform: "none"},
};

export const primaryLinearStyle: CSSProperties = {
  /* this "overlays" the progress bar without taking up vertical space,
  so that the content doesn't "jump" when toggling the progress state  */
  position: "absolute",

  /* make it small and a bit up from bottom, using px to avoid any weird
  rounding / fuzziness issues. */
  height: "2px", bottom: "5px",

  // center the progress bar
  width: "80%",
};

export function PrimaryButton({
  isLoading, error, children, ...buttonProps
}: {
  isLoading?: boolean, error?: ErrorInfo
} & ButtonProps){
  return <>
    <Button {...primaryButtonProps} {...buttonProps}
      style={{...primaryButtonProps.style, ...buttonProps.style}}
    >
      {children}
      { isLoading &&
        <LinearProgress style={{...primaryLinearStyle}}/>
      }
    </Button>
    <CompactErrorPanel error={error} border={"h-pad"}/>
  </>
}

export function PrimaryActionButton({
  isLoading, error, context, children, ...buttonProps
}: {
  /** context is a user targeted description of the action the button is doing,
   used in error handling at the moment, could also be used in UI feedback for
   the loading state and a "post-success" toast or something. */
  context: string,
  isLoading?: boolean,
  error: unknown
} & ButtonProps){
  return <>
    <Button {...primaryButtonProps} {...buttonProps}
      style={{...primaryButtonProps.style, ...buttonProps.style}}
    >
      {children}
      { isLoading &&
        <LinearProgress style={{...primaryLinearStyle}}/>
      }
    </Button>
    { error &&
      <CompactErrorPanel border={"h-pad"}
        error={{message: `error while ${context}`, problem: error}}/>
    } 
  </>
}

export function SecondaryButton({isLoading, error, children, ...buttonProps}:
  { isLoading?: boolean, error?: ErrorInfo } & ButtonProps
){
  return <>
    <Button {...secondaryButtonProps} {...buttonProps}
      style={{...secondaryButtonProps.style, ...buttonProps.style}}
    >
      {children}
      { isLoading &&
        <LinearProgress style={{...primaryLinearStyle}}/>
      }
    </Button>
    <CompactErrorPanel error={error} border={"h-pad"}/>
  </>
}

export function RaidoAddFab({href}: {href: string}){
  const nav = useNavigation();
  return <Fab href={href} color="primary"
    onClick={e=>{
      nav.navigateTo(href, e);
    }}
    size="small"
  ><Add/></Fab>
}
