import React from "react";
import { normalisePath } from "Util/Location";
import { SmallContentMain } from "Design/LayoutMain";
import { RaidoDescription } from "Auth/IntroContainer";
import { ContainerCard } from "Design/ContainerCard";
import { Config, unknownCommitId } from "Config";
import {
  formatLocalDateAsIsoShortDateTime,
  parseDateFromEpoch
} from "Util/DateUtil";
import { TextSpan } from "Component/TextSpan";
import { NavTransition } from "Design/NavigationProvider";
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
  return <NavTransition isPath={isAboutAppPagePath}
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

function GitCommitLink({commitId}: {commitId: string}){
  if( !commitId || commitId === unknownCommitId ){
    return <>Unknown commit</>
  }
  return <NewWindowLink href={raidoGithubUrl + "/commit/" + commitId}>
    {commitId.substring(0, 8)}
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