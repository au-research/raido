import React from "react";
import { normalisePath } from "Util/Location";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { SmallContentMain } from "Design/LayoutMain";
import { List, ListItemText } from "@mui/material";
import { NewWindowLink } from "Component/ExternalLink";
import { HomeLink, RaidoDescription } from "Auth/IntroContainer";
import { raidoTitle } from "Component/Util";

const log = console;

const pageUrl = "/terms";

export function getUsageTermsPagePath(): string{
  return pageUrl;
}

export function isUsageTermsPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function UsageTermsPage(){
  const {pathname} = useLocationPathname();
  if( !isUsageTermsPagePath(pathname) ){
    return null;
  }
  window.document.title = raidoTitle("Terms & conditions");
  return <Content/>;
}

function Content(){
  return <SmallContentMain>
    <RaidoDescription/>
    <List>
      <ListItemText>
        Please refer to the
        ARDC <NewWindowLink href={"https://ardc.edu.au/terms-conditions/"}>
        Terms & conditions</NewWindowLink> reference page for the list of
        relevant Terms and Conditions for usage of the RAiD Service.
      </ListItemText>
    </List>
    <HomeLink/>
  </SmallContentMain>
}
