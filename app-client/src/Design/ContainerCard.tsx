import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import CardContent from "@mui/material/CardContent";
import * as React from "react";
import {cardHeaderClasses, useMediaQuery} from "@mui/material";
import { CSSProperties } from "react";

export function ContainerCard({ 
  title, action, contentStyle, children
}:{
  title: React.ReactNode,
  action?: React.ReactNode,
  contentStyle?: CSSProperties,
  children: React.ReactNode,
}){
  return <Card>
    <CardHeader title={title} titleTypographyProps={{variant: "h6"}}
      sx={{
        // reduce the spacing around the header, there was too much whitespace
        padding: "0 1em",
        backgroundColor: 'rgba(0,0,0,.1)',
        // if card has no actions header gets short, minHeight keeps it
        // consistent (AboutScreen has one panel with actions and one without)
        minHeight: "3rem",
        [`& .${cardHeaderClasses.action}`]: { 
          // default margin-top of -4px makes the icon sit too high 
          marginTop: 0,
        }
      }}
      action={action}
    />
    <CardContent
      style={{overflowX: "auto", padding: "1em", ...contentStyle}}
    >
      {children}
    </CardContent>
  </Card>
}

export function useSideMargins():boolean{
  return useMediaQuery('(min-width:22em)', {noSsr: true});
}

export function CardMargin({children}: {  children:React.ReactNode }){
  const showSideMargins = useSideMargins();
  return <div style={{
    marginTop: 0,
    marginBottom: "1em",
    marginLeft: showSideMargins ? "1em" : 0,
    minWidth:"22em",
    maxWidth:"25em",
  }}>
    {children}
  </div>
}

