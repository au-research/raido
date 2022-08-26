import { SmallContentMain } from "Design/LayoutMain";
import { Typography } from "@mui/material";
import React from "react";
import { RaidoLink } from "Component/RaidoLink";
import { getUsageTermsPagePath } from "Page/UsageTermsPage";
import { getPrivacyPagePath } from "Page/PrivacyPage";
import { NewWindowLink } from "Component/ExternalLink";

export function IntroContainer(){
  return <SmallContentMain center>
    <RaidoDescription/>
    <Typography paragraph>
      <RaidoLink href={getPrivacyPagePath()}>Privacy statement</RaidoLink>
      &emsp;
      &emsp;
      <RaidoLink href={getUsageTermsPagePath()}>Usage terms</RaidoLink>
    </Typography>

  </SmallContentMain>
}

export function RaidoDescription(){
  return <Typography paragraph>
    Raido is the Oceania region implementation of
    the <NewWindowLink href="https://www.raid.org.au/">
    RAiD</NewWindowLink> ISO standard.
  </Typography>
}

export function HomeLink(){
  return <Typography paddingTop={"1em"} align={"center"}>
    <RaidoLink href="/">Home</RaidoLink>
  </Typography>

}