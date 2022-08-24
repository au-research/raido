import * as React from "react";
import {OpenInNew} from "@mui/icons-material";

export const zincGithubUrl = "https://github.com/shorn/zinc";
export const zincGithubOidcDocUrl = zincGithubUrl + 
  "/blob/main/aws-infra/lambda/doc/cognito-github.md";
export const zincGithubDirectDocUrl = zincGithubUrl + 
  "/blob/main/aws-infra/lambda/doc/direct-github-sign-in.md";

export const muiUrl = "https://mui.com";

export function NewWindowLink(props: {
  href: string,
  children: React.ReactNode,
}){
  return <a href={props.href}
    target="_blank" rel="noopener noreferrer"
    style={{whiteSpace: "nowrap"}}
  >
    {props.children}{" "}
    <OpenInNew style={{
      // align with text better
      verticalAlign: "bottom"
    }}/>
  </a>
}
