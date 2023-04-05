import * as React from "react";
import { OpenInNew } from "@mui/icons-material";
import { Link, LinkProps } from "@mui/material";

const supportEmail = "contact@raid.org";

export const muiUrl = "https://mui.com";
export const raidoGithubUrl = "https://github.com/au-research/raido"
export const rorUrl = "https://ror.org";
export const doiUrl = "https://www.doi.org";

/* IMPROVE: long-term this'll need to be configured, so that we can land on 
sandbox urls (though maybe by then we'll have proper backend integrations so 
this won't be needed any more). */
export const orcidUrl = "https://orcid.org"


export const fieldOfResearchUrl = 
  "https://linked.data.gov.au/def/anzsrc-for/2020";

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
  return <a target="_blank" rel="noopener noreferrer"
    href={`mailto:${supportEmail}`}
  >
    {supportEmail}
  </a>
}