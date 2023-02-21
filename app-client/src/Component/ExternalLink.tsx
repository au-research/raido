import * as React from "react";
import { OpenInNew } from "@mui/icons-material";
import { Link, LinkProps } from "@mui/material";

const supportEmail = "contact@raid.org";

export const muiUrl = "https://mui.com";
export const raidoGithubUrl = "https://github.com/au-research/raido"

export function NewWindowLink({children, ...linkProps}: {
  children: React.ReactNode,
} & LinkProps){
  return <Link {...linkProps}
    target="_blank" rel="noopener noreferrer"
    style={{ whiteSpace: "nowrap", ...linkProps.style}}
  >
    {children}
    <OpenInNew style={{
      scale: "60%",
      // align with text better
      verticalAlign: "text-bottom"
    }}/>
  </Link>
}

export function SupportMailLink(){
  return <a href={`mailto:${supportEmail}`}>{supportEmail}</a>
}