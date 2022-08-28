import React from "react";
import { normalisePath } from "Util/Location";
import { useLocationPathname } from "Util/Hook/LocationPathname";
import { SmallContentMain } from "Design/LayoutMain";
import { List, ListItemText, Typography } from "@mui/material";
import { NewWindowLink, supportEmail } from "Component/ExternalLink";
import { HomeLink, RaidoDescription } from "Auth/IntroContainer";
import { RaidoLink } from "Component/RaidoLink";

const log = console;

const pageUrl = "/privacy";

export function getPrivacyPagePath(): string{
  return pageUrl;
}

export function isPrivacyPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function PrivacyPage(){
  const {pathname} = useLocationPathname();
  if( !isPrivacyPagePath(pathname) ){
    return null;
  }
  window.document.title = "Raido - Privacy";  
  return <Content/>;
}

function Content(){
  return <SmallContentMain>
    <RaidoDescription/>
    <List>
      <ListItemText>
        The Raido application fully conforms to 
        the <NewWindowLink href="https://ardc.edu.au/privacy-policy/">
        privacy policy</NewWindowLink> of the ARDC.
      </ListItemText>
      <ListItemText>You can request that your own data be deleted by submitting 
        a request to <a href={`mailto:${supportEmail}`}>{supportEmail}</a>.
        All data associated with your account (especially anything from ID 
        providers like Google, etc.) will be deleted within 7 days.
      </ListItemText>
      <ListItemText>Data from Identity Providers (e.g. AAF, Google)
        is only used to enable sign-in functionality so that users can make 
        use of Raido having to create a new set of email/password credentials. 
      </ListItemText>
    </List>
    <HomeLink/>
  </SmallContentMain>
}
