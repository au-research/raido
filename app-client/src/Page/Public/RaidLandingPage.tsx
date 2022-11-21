import React from "react";
import { isPagePath } from "Design/NavigationProvider";
import { normalisePath } from "Util/Location";
import {
  AlternateUrlBlock,
  MetadataSchemaV1,
  RaidoMetaschema,
  PublicReadRaidResponseV2, PublicReadRaidResponseV3
} from "Generated/Raidv2";
import { Config } from "Config";
import { RqQuery } from "Util/ReactQueryUtil";
import {
  QueryClient,
  QueryClientProvider,
  useQuery
} from "@tanstack/react-query";
import { publicApi } from "Api/SimpleApi";
import {
  SmallContentMain,
  SmallScreenMain,
  SmallScreenPaper
} from "Design/LayoutMain";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { BooleanDisplay, DateDisplay } from "Component/Util";
import { TextSpan } from "Component/TextSpan";
import { formatMetadata } from "Component/MetaDataContainer";
import { List, ListItem } from "@mui/material";
import { NewWindowLink } from "Component/ExternalLink";

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
  const query: RqQuery<PublicReadRaidResponseV3> = useQuery(
    [queryName, handle],
    async () => {
      //await delay(2000);
      return await api.publicReadRaidV3({handle});
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

  if( !query.data.metadata ){
    return <CompactErrorPanel
      error={"no metadata in response"}/>
  }
  
  console.log("landingPage.render()", 
    query.data, typeof query.data);
  
  if( 
    (query.data.metadata as any)?.metadataSchema !== 
    RaidoMetaschema.PublicMetadataSchemaV1 
  ){
    return <CompactErrorPanel error={{
      message: `unknown metadataSchema`,
      problem: query.data }}/>
  }

  /* need to do more work on the OpenAPI spec so that 
   openapi-generator/typescript-fetch knows that it is/might be a 
   MetadataSchemaV1.
   Since it's plain JSON, dates will be strings - but we've casted to an object
   that expects them to be dates, watch out.  
   */
  const metadata: MetadataSchemaV1 = query.data.metadata as MetadataSchemaV1;
  
  return metadata.access.type === "Open" ?
    <OpenRaid raid={query.data} metadata={metadata}/> :
    <ConfidentialRaid raid={query.data} metadata={metadata}/>
}

function ConfidentialRaid({raid, metadata}: {
  raid: PublicReadRaidResponseV2,
  metadata: MetadataSchemaV1,
}){
  return <SmallContentMain><InfoFieldList>
    <InfoField id="handle" label="Handle"
      value={formatGlobalHandle(raid.handle)}/>
    <InfoField id="createDate" label="Create date" value={
      <DateDisplay date={raid.createDate}/>
    }/>

    <InfoField id="confidential" label="Confidential"
      value={<BooleanDisplay value={true}/>}
    />
    
    <InfoField id="confidential" label="Access statement"
      value={<TextSpan>{metadata.access.accessStatement}</TextSpan>}
    />
  </InfoFieldList></SmallContentMain>
}

function OpenRaid({raid, metadata}: {
  raid: PublicReadRaidResponseV2,
  metadata: MetadataSchemaV1,
}){
  return <SmallScreenMain>
    <SmallScreenPaper>  
      <InfoFieldList>
        <InfoField id="handle" label="Handle"
          value={formatGlobalHandle(raid.handle)}/>
        <InfoField id="createDate" label="Create date" value={
          <DateDisplay date={raid.createDate}/>
        }/>

        <InfoField id="confidential" label="Confidential"
          value={<BooleanDisplay value={false}/>}
        />

        <InfoField id="servicePoint" label="Service point"
          value={raid.servicePointName}
        />

        {/*todo:sto make a widget, this only works when it's the first item*/}
        <InfoField id="primaryTitle" label="Primary title"
          value={metadata.titles[0]?.title}/>

        <InfoField id="startDate" label="Start date" value={
          metadata.dates.startDate.toString()
        }/>
      </InfoFieldList>
    </SmallScreenPaper>
    <AlternateUrlLinks urls={metadata.alternateUrls}></AlternateUrlLinks>
    <SmallScreenPaper>
      <pre style={{overflowX: "scroll"}}>
        {formatMetadata(metadata)}
      </pre>
    </SmallScreenPaper>
  </SmallScreenMain>
}

function AlternateUrlLinks({urls}:{urls?: AlternateUrlBlock[]}){
  if( !urls || urls.length === 0 ){
    return null;
  }
  
  return <SmallScreenPaper>
    <List>{urls.map(iUrl=>
      <ListItem key={iUrl.url}>
        <NewWindowLink href={iUrl.url}>
          {iUrl.url}
        </NewWindowLink>
      </ListItem>
    )}</List>
  </SmallScreenPaper>
}