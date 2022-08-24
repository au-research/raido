import React from "react";
import Typography from "@mui/material/Typography";
import {LinearProgress} from "@mui/material";
import {SmallContentMain} from "Design/LayoutMain";

export function SmallPageSpinner({message}: {
  message: string | React.ReactNode
}){
  return <SmallContentMain center>
    <Typography paragraph>
      {message}
    </Typography>
    <CompactLinearProgress isLoading style={{width: "100%"}}/>
  </SmallContentMain>
}

export function CompactLinearProgress(props: {
  isLoading?: boolean,
  color?: "primary"|"secondary"
  style?: React.CSSProperties,
}){
  const {isLoading} = props;
  const style: React.CSSProperties = {...props.style, height: "2px"};

  /* placeholder that stops the screen content underneath the progress bar
  from "jumping" as indicator is toggled.
  Should change this to the overlay style from button, one day.
   */
  if( !isLoading ){
    return <div style={style} />;
  }

  return <LinearProgress variant="indeterminate"
    color={props.color} style={style} />
}