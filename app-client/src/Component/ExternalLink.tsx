import * as React from "react";
import { OpenInNew } from "@mui/icons-material";
import { Link, LinkProps } from "@mui/material";

const supportEmail = "contact@raid.org";

export const muiUrl = "https://mui.com";
export const raidoGithubUrl = "https://github.com/au-research/raido"

/* IMPROVE: long-term this'll need to be configured, so that we can land on 
sandbox urls (though maybe by then we'll have proper backend integratins so this
won't be needed any more). */
export const orcidUrl = "https://orcid.org"

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