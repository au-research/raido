import * as React from "react";
import {IconButton, Popover, Typography} from "@mui/material";
import {stopClick} from "Util/EventUtil";
import {CSSProperties, ReactNode} from "react";
import { Help } from "@mui/icons-material";

export const helpStyle: CSSProperties = {
  padding: "1em",
  // was ugly on desktop because it spanned the whole window
  maxWidth: "30em",
};

export function HelpPopover({content, icon}: {
  content: JSX.Element,
  icon?: ReactNode,
}){
  const [open, setOpen] = React.useState(false);
  const [anchor, setAnchor] = React.useState(undefined as
    undefined | HTMLElement);

  return <>
    <IconButton color="inherit" onClick={(e) => {
      stopClick(e);
      setOpen(true);
      setAnchor(e.currentTarget);
    }}>
      {icon ?? <Help style={{
        verticalAlign: "middle",
        //color: helpIconColor,
      }}/>}
    </IconButton>
    <Popover
      id="help-popover"
      open={open}
      anchorEl={anchor}
      onClose={() => {
        setOpen(false);
        setAnchor(undefined);
      }}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'center',
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: 'center',
      }}
    >
      <Typography style={{...helpStyle}}>
        {content}
      </Typography>
    </Popover>
  </>
}