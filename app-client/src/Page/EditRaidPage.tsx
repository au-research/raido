import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import React, { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  LegacyMetadataSchemaV1,
  RaidoMetadataSchemaV1,
  ReadRaidResponseV2,
  ServicePoint
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { Stack } from "@mui/material";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { RqQuery } from "Util/ReactQueryUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import Divider from "@mui/material/Divider";
import { assert } from "Util/TypeUtil";
import { NewWindowLink } from "Component/ExternalLink";
import {
  formatGlobalHandle,
  getRaidLandingPagePath
} from "Page/Public/RaidLandingPage";
import {
  convertMetadata,
  MetaDataContainer
} from "Component/MetaDataContainer";
import { EditRaidoV1SchemaForm } from "Page/EditRaidoV1SchemaForm";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { TextSpan } from "Component/TextSpan";

const log = console;

const pageUrl = "/edit-raid";

export function getEditRaidPageLink(handle: string): string{
  return `${pageUrl}/${handle}`;
}

export function isEditRaidPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string{
  return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String)
}

export function EditRaidPage(){
  return <NavTransition isPagePath={isEditRaidPagePath}
    title={raidoTitle("Edit")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [handle] = useState(getRaidHandleFromPathname(nav));
  window.document.title = window.document.title + " - " + handle;
  return <LargeContentMain>
    <EditRaidContainer handle={handle}/>
  </LargeContentMain>
}

interface ReadData {
  readonly raid: ReadRaidResponseV2,
  readonly metadata: RaidoMetadataSchemaV1|LegacyMetadataSchemaV1,
}

function EditRaidContainer({handle}: {
  handle: string,
}){
  const api = useAuthApi();
  const readQueryName = 'readRaid';
  const queryClient = useQueryClient();
  const readQuery: RqQuery<ReadData> = useQuery(
    [readQueryName, handle],
    async () => {
      //await delay(2000);
      const raid = await api.basicRaid.readRaidV2({
        readRaidV2Request: { handle }
      });
      const metadata = convertMetadata(raid.metadata);
      const readData: ReadData = {raid, metadata};
      return readData;
    }
  );
  
  const servicePointId = readQuery.data?.raid.servicePointId
  const spQuery: RqQuery<ServicePoint> = useQuery(
    ['readServicePoint', servicePointId],
    async () => {
      assert(servicePointId);
      return await api.admin.readServicePoint({servicePointId});
    },
    {enabled: !!servicePointId}
  );

  return <>
    <ContainerCard title={`Edit RAiD`} action={<EditRaidHelp/>}>
      <CompactErrorPanel error={readQuery.error}/>
      <RaidInfoList handle={handle} servicePointName={spQuery.data?.name} />
      <Divider variant={"middle"}
        style={{marginTop: "1em", marginBottom: "1.5em"}} />
      <RaidDataForm readQuery={readQuery} 
        onUpdateSuccess={()=>queryClient.invalidateQueries([readQueryName])} />
    </ContainerCard>
    <br/>
    <MetaDataContainer metadata={readQuery.data?.metadata}/>
  </>
}

/** "switching logic" for deciding what component to use depending on the state 
 of the readQuery and then the schema of the metadata that was read. */
function RaidDataForm({readQuery, onUpdateSuccess}: {
  readQuery: RqQuery<ReadData>,
  onUpdateSuccess: ()=>void,
}){
  if( !readQuery.data ){
    if( readQuery.isLoading ){
      return <SmallPageSpinner message={"loading raid data"}/>
    }

    // paranoia: unexpected state, never actually seen this happen
    throw new Error("no data, but no error or ongoing work");
  }
  
  if( readQuery.data.metadata.metadataSchema === "RaidoMetadataSchemaV1" ){
    return <EditRaidoV1SchemaForm raid={readQuery.data.raid}
      metadata={readQuery.data.metadata as RaidoMetadataSchemaV1}
      onUpdateSuccess={onUpdateSuccess}
    />
  }
  
  if( readQuery.data.metadata.metadataSchema === "LegacyMetadataSchemaV1" ){
    return <TextSpan>Legacy schema must be upgraded to
      RaidoMetadataSchemaV1 before it can be edited</TextSpan>
  }
  
  return <CompactErrorPanel error={{
    message: "unknown metadata schema: " +
      readQuery.data.metadata.metadataSchema,
    problem: readQuery.data.metadata}} />
}

function RaidInfoList({handle, servicePointName}: {
  handle: string,
  servicePointName?: string
}){
  return <InfoFieldList>
    <InfoField id="globalHandle" label="Global handle" value={
      <NewWindowLink href={formatGlobalHandle(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="raidoHandle" label="Raido handle" value={
      <NewWindowLink href={getRaidLandingPagePath(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="servicePoint" label="Service point"
      value={servicePointName ?? ""}
    />
  </InfoFieldList>;
}

function EditRaidHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <ul>
        <li><HelpChip label={"XXX"}/>
          Words.
        </li>
      </ul>
    </Stack>
  }/>;
}
