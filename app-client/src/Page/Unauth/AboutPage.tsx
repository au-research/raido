import React from "react";
import { normalisePath } from "Util/Location";
import { SmallContentMain } from "Design/LayoutMain";
import { RaidoDescription } from "Auth/IntroContainer";
import { ContainerCard } from "Design/ContainerCard";
import { Config, unknownCommitId } from "Config";
import { parseDateFromEpoch } from "Util/DateUtil";
import { TextSpan } from "Component/TextSpan";
import { NavTransition } from "Design/NavigationProvider";
import { NewWindowLink, raidoGithubUrl } from "Component/ExternalLink";
import { raidoTitle } from "Component/Util";

const log = console;

const pageUrl = "/about";

export function getAboutPagePath(): string{
  return pageUrl;
}

export function isAboutPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function AboutPage(){
  return <NavTransition isPath={isAboutPagePath} title={raidoTitle("About")}>
    <Content/>
  </NavTransition>
}

function Content(){
  return <SmallContentMain>
    <RaidoDescription paddingBottom={"2em"}/>
    <ClientPanel />
  </SmallContentMain>

}

function ClientPanel(){

  let buildDate = parseDateFromEpoch(Config.buildDate);
  log.debug("Config.buildDate", {
    configBuildDate: Config.buildDate,
    parsedBuildDate: buildDate
  });

  let localDateString: string;
  let localTimeString: string;
  let buildDateString: string;

  if( !buildDate || isNaN(buildDate.getTime()) ){
    log.warn("appBuildDate could not be parsed", buildDate);
    localDateString = "unknown";
    localTimeString = "unknown";
    buildDateString = Config.buildDate.toString();
  }
  else {
    localDateString = buildDate.toDateString();
    localTimeString = buildDate.toLocaleTimeString();
    buildDateString = buildDate.toISOString();
  }

  return <ContainerCard title="App client">
    <TextSpan>
      Commit: <GitCommitLink commitId={Config.gitCommit}/><br/>
      Build date&nbsp;(UTC):
      <wbr/>
      <NoWrap>&nbsp;{buildDateString}</NoWrap>
      <br/>
      Build date (Local time):
      <wbr/>
      <NoWrap>&nbsp;{localDateString}</NoWrap>
      <wbr/>
      &nbsp;{localTimeString}
      <wbr/>
      &nbsp;{Intl.DateTimeFormat().resolvedOptions().timeZone}
      <br/>
    </TextSpan>
  </ContainerCard>;
}

function NoWrap(props: {children: React.ReactNode}){
  return <span style={{whiteSpace: "nowrap"}}>{props.children}</span>
}

function GitCommitLink({commitId}:{commitId: string}){
  if( !commitId || commitId === unknownCommitId ){
    return <>Unknown commit</>
  }
  return <NewWindowLink href={raidoGithubUrl+"/commit/"+commitId}>
    {commitId.substring(0, 8)}
  </NewWindowLink>
  
}