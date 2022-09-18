import * as React from "react";
import {
  Chip,
  ChipProps,
  IconButton,
  Popover,
  Typography
} from "@mui/material";
import {stopClick} from "Util/EventUtil";
import {CSSProperties, ReactNode} from "react";
import { Help } from "@mui/icons-material";
import { TextSpan } from "Component/TextSpan";

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
      PaperProps={{
        style:{...helpStyle}
      }}
    >
      {content}
    </Popover>
  </>
}

export function HelpChip(props: ChipProps){
  // span so it can be inside <p> etc.
  return <Chip component="span" variant={"outlined"} size={"small"} {...props}
    style={{marginRight: ".5em", ...props.style}}
  />
}