import { SmallContentMain } from "Design/LayoutMain";
import { Stack, StackProps, Typography } from "@mui/material";
import React from "react";
import { RaidoLink } from "Component/RaidoLink";
import { NewWindowLink } from "Component/ExternalLink";
import { getPrivacyPagePath } from "Page/Unauth/PrivacyPage";
import { getUsageTermsPagePath } from "Page/Unauth/UsageTermsPage";
import { getAboutRaidPagePath } from "Page/Unauth/AboutRaidPage";

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

export function RaidoDescription(props: StackProps){
  return <Stack {...props}>
    <Typography paragraph>
      This is the Oceania region implementation of
      the <RaidoLink href={getAboutRaidPagePath()}>
      RAiD</RaidoLink> ISO standard.
    </Typography>
    <Typography paragraph>
      Maintained by the <NewWindowLink href="https://ardc.edu.au/">ARDC
      </NewWindowLink>.
    </Typography>
  </Stack>
}

export function HomeLink(){
  return <>
    <Typography paddingTop={"1em"} align={"center"}>
      <RaidoLink href="/">Home</RaidoLink>
    </Typography>
  </>

}