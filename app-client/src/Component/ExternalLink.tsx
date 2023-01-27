import * as React from "react";
import { OpenInNew } from "@mui/icons-material";
import { Link } from "@mui/material";

export const supportEmail = "web.services@ardc.edu.au";

export const muiUrl = "https://mui.com";
export const raidoGithubUrl = "https://github.com/au-research/raido"

export function NewWindowLink({href, children}: {
  href: string,
  children: React.ReactNode,
}){
  return <Link href={href}
    target="_blank" rel="noopener noreferrer"
    style={{whiteSpace: "nowrap"}}
  >
    {children}
    <OpenInNew style={{
      scale: "60%",
      // align with text better
      verticalAlign: "text-bottom"
    }}/>
  </Link>
}
