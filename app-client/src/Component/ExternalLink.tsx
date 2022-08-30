import * as React from "react";
import {OpenInNew} from "@mui/icons-material";

export const supportEmail = "web.services@ardc.edu.au";

export const muiUrl = "https://mui.com";
export const raidoGithubUrl = "https://github.com/au-research/raido-v2"

export function NewWindowLink(props: {
  href: string,
  children: React.ReactNode,
}){
  return <a href={props.href}
    target="_blank" rel="noopener noreferrer"
    style={{whiteSpace: "nowrap"}}
  >
    {props.children}
    <OpenInNew style={{
      scale: "60%",
      // align with text better
      verticalAlign: "text-bottom"
    }}/>
  </a>
}
