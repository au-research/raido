import React from "react";
import { normalisePath } from "Util/Location";
import { SmallContentMain } from "Design/LayoutMain";
import { RaidoDescription } from "Auth/IntroContainer";
import { ContainerCard } from "Design/ContainerCard";
import { Config, unknownBuildDate, unknownCommitId } from "Config";
import {
  formatLocalDateAsIsoShortDateTime,
  parseDateFromEpoch
} from "Util/DateUtil";
import { TextSpan } from "Component/TextSpan";
import { isPagePath, NavTransition } from "Design/NavigationProvider";
import { NewWindowLink, raidoGithubUrl } from "Component/ExternalLink";
import { raidoTitle } from "Component/Util";
import {
  Configuration,
  PublicExperimentalApi,
  VersionResult
} from "Generated/Raidv2";
import { forceError, isError } from "Error/ErrorUtil";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { Stack } from "@mui/material";

const log = console;

const pageUrl = "/about-app";

export function getAboutAppPagePath(): string{
  return pageUrl;
}

export function isAboutAppPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function AboutAppPage(){
  return <NavTransition isPagePath={(pathname)=>isPagePath(pathname, pageUrl)}
    title={raidoTitle("About App")}>
    <Content/>
  </NavTransition>
}

function Content(){
  return <SmallContentMain>
    <Stack spacing={2}>
      <RaidoDescription/>
      <ClientPanel/>
      <ServerPanel/>
    </Stack>
  </SmallContentMain>
}

function ClientPanel(){


  let localDateString: string = "unknown";
  let localTimeString: string = "";
  let buildDateString: string = "unknown";

  if( Config.buildDate && Config.buildDate !== unknownBuildDate ){
    let buildDate = parseDateFromEpoch(Config.buildDate);
    if( buildDate && !isNaN(buildDate.getTime()) ){
      localDateString = buildDate.toDateString();
      localTimeString = buildDate.toLocaleTimeString();
      buildDateString = buildDate.toISOString();
    }
    else {
      log.warn("appBuildDate could not be parsed", buildDate);
      // leave the defaults
    }
    log.debug("Config.buildDate", {
      configBuildDate: Config.buildDate,
      parsedBuildDate: buildDate
    });
  }
  else {
    // no build date provided, leave the defaults
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

function GitCommitLink({commitId}: {commitId: string}){
  if( !commitId || commitId === unknownCommitId ){
    return <>Unknown commit</>
  }
  return <NewWindowLink href={raidoGithubUrl + "/commit/" + commitId}>
    {commitId.substring(0, 8)}
  </NewWindowLink>
}

function ApiSpecLink({commitId}: {commitId: string|undefined}){
  if( !commitId || commitId === unknownCommitId ){
    return <>Unknown commit</>
  }
  const fileHref = raidoGithubUrl + "/blob/" + commitId + 
    "/api-svc/idl-raid-v2/src/raido-openapi-3.0.yaml";
  return <NewWindowLink href={fileHref}>
    raido-openapi-3.0.yaml
  </NewWindowLink>
}

function ServerPanel(){
  const [serverDetails, setServerDetails] = React.useState(
    undefined as undefined | Error | VersionResult);
  const [isLoading, setIsLoading] = React.useState(false);

  const loadApiInfo = React.useCallback(async () => {
    setIsLoading(true);
    try {
      const config = new Configuration({basePath: Config.raidoApiSvc});
      const details = await new PublicExperimentalApi(config).version();
      setServerDetails(details);
    }
    catch( e ){
      setServerDetails(forceError(e));
    }
    finally {
      setIsLoading(false);
    }
  }, [],);

  React.useEffect(() => {
    //noinspection JSIgnoredPromiseFromCall
    loadApiInfo()
  }, [loadApiInfo]);

  if( isError(serverDetails) ){
    return <CompactErrorPanel error={{
      message: "while getting server app info",
      problem: serverDetails
    }}/>
  }

  return <ContainerCard title="API service"
    action={<RefreshIconButton refreshing={isLoading} onClick={loadApiInfo}/>}
  >
    <TextSpan><>
      Server version: {serverDetails ? serverDetails.buildVersion : ""}<br/>
      Commit id:{" "}
      <GitCommitLink commitId={serverDetails?.buildCommitId || ""}/><br/>
      OpenAPI spec: <ApiSpecLink commitId={serverDetails?.buildCommitId}/><br/>
      Build date: {serverDetails ? serverDetails.buildDate : ""}<br/>
      Start date: {
        serverDetails ?
          formatLocalDateAsIsoShortDateTime(serverDetails.startDate) : ""
      }
      <br/>
    </>
    </TextSpan>
  </ContainerCard>;
}