import React from "react";
import { isPagePath } from "Design/NavigationProvider";
import { normalisePath } from "Util/Location";
import { PublicReadRaidResponseV1 } from "Generated/Raidv2";
import { Config } from "Config";
import { RqQuery } from "Util/ReactQueryUtil";
import {
  QueryClient,
  QueryClientProvider,
  useQuery
} from "@tanstack/react-query";
import { publicApi } from "Api/SimpleApi";
import { SmallContentMain } from "Design/LayoutMain";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { BooleanDisplay, DateDisplay } from "Component/Util";

const pageUrl = "/handle";

export function formatGlobalHandle(handle: string){
  return `https://hdl.handle.net/${handle}`;
}

/*
Not used by anything in the app, the "public landing page" link from the raid 
edit page links via the https://hdl.handle.net domain, which redirects to this
url, but that's encoding the raid url (content path) on the server.

This page should be its own separate codebase.  Probably should be server-side
generated HTML, not an SPA;  or at least, should be Preact / Svelte or 
something a bit more light weight.  
*/
export function getRaidLandingPagePath(handle: string): string{
  return `${pageUrl}/${handle}`;
}

export function isRaidLandingPagePath(pathname: string): boolean{
  return normalisePath(pathname).startsWith(pageUrl);
}

export function RaidLandingPage(){
  const isPath = isPagePath(window.location.pathname, pageUrl);

  if( !isPath.isPath ){
    return null;
  }

  if( !isPath.pathSuffix ){
    throw new Error("could not parse handle from path");
  }

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        ...Config.publicApiQuery,
      }
    }
  });

  return <>
    <QueryClientProvider client={queryClient}>
      <Content handle={isPath.pathSuffix}/>
    </QueryClientProvider>
  </>;
}

function Content({handle}: {handle: string}){
  const api = publicApi();
  const queryName = 'publicReadRaid';
  const query: RqQuery<PublicReadRaidResponseV1> = useQuery(
    [queryName, handle],
    async () => {
      //await delay(2000);
      return await api.publicReadRaid({handle});
    }
  );

  if( query.isLoading ){
    return <SmallPageSpinner message={"loading RAiD data"}/>
  }

  if( query.error ){
    return <CompactErrorPanel error={query.error}/>
  }

  if( !query.data ){
    return <CompactErrorPanel
      error={"unknown state: not loading, no error, no data"}/>
  }

  return <SmallContentMain>
    <InfoFieldList>
      <InfoField id="handle" label="Handle"
        value={formatGlobalHandle(handle)}/>
      <InfoField id="createDate" label="Create Date" value={
        <DateDisplay date={query.data.createDate}/>
      }/>

      <InfoField id="confidential" label="Confidential"
        value={<BooleanDisplay value={query.data.confidential}/>}
      />

      { !query.data.confidential &&  <>
        <InfoField id="servicePoint" label="Service point"
          value={query.data.servicePointName}
        />
        <InfoField id="name" label="Name" value={query.data.name}/>
        <InfoField id="startDate" label="Start Date" value={
          <DateDisplay date={query.data.startDate}/>
        }/>
      </>}
        
    </InfoFieldList>
  </SmallContentMain>
}