import { SmallContentMain } from "Design/LayoutMain";
import { Typography } from "@mui/material";
import React from "react";
import { RaidoLink } from "Component/RaidoLink";
import { getUsageTermsPagePath } from "Page/Unauth/UsageTermsPage";
import { getPrivacyPagePath } from "Page/Unauth/PrivacyPage";
import { NewWindowLink } from "Component/ExternalLink";
import { TypographyProps } from "@mui/material/Typography";

export function IntroContainer(){
  return <SmallContentMain center>
    <RaidoDescription/>
  </SmallContentMain>
}

export function RaidoDescription(props: TypographyProps){
  return <Typography paragraph {...props}>
    Raido is the Oceania region implementation of
    the <NewWindowLink href="https://www.raid.org.au/">
    RAiD</NewWindowLink> ISO standard.
  </Typography>
}

export function HomeLink(){
  return <>
    <Typography paragraph>
      <RaidoLink href={getPrivacyPagePath()}>Privacy statement</RaidoLink>
      &emsp;
      &emsp;
      <RaidoLink href={getUsageTermsPagePath()}>Usage terms</RaidoLink>
    </Typography>
    <Typography paddingTop={"1em"} align={"center"}>
      <RaidoLink href="/">Home</RaidoLink>
    </Typography>
  </>

}