import LaunchIcon from "@mui/icons-material/Launch";
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
  RaidoMetadataSchemaV1, RaidoMetadataSchemaV2,
  ReadRaidResponseV2,
  ServicePoint
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { RqQuery } from "Util/ReactQueryUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import Divider from "@mui/material/Divider";
import { assert } from "Util/TypeUtil";
import { NewWindowLink } from "Component/ExternalLink";
import {
  formatCnriUrl,
  getRaidLandingPagePath
} from "Page/Public/RaidLandingPage";
import {
  convertMetadata,
  MetaDataContainer
} from "Component/MetaDataContainer";
import {
  ComplicatedMetadataWarning,
  EditRaidoV1SchemaForm,
  findMetadataUpdateProblems
} from "Page/EditRaidoV1SchemaForm";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { UpgradeLegacySchemaForm } from "Page/UpgradeLegacySchemaForm";
import { MintRaidHelp } from "Page/MintRaidPage";
import { IconButton } from "@mui/material";

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
  readonly metadata: RaidoMetadataSchemaV1|RaidoMetadataSchemaV2|LegacyMetadataSchemaV1,
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
    <ContainerCard title={`Edit RAiD`} action={
      /* bit dodgy, will have to re-factor when we want different help between
      the edit page and the upgrade page. */
      <>
        <IconButton aria-label="show raid" href={`/show-raid/${handle}`}>
          <LaunchIcon />
        </IconButton>
        <MintRaidHelp/>
      </>
      
    }
    >
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
    const metadata = readQuery.data.metadata as RaidoMetadataSchemaV1
    const problems = findMetadataUpdateProblems(metadata);
    
    if( problems.length > 0 ){
      return <ComplicatedMetadataWarning 
        metadata={metadata} problems={problems}/>
    }

    return <EditRaidoV1SchemaForm raid={readQuery.data.raid}
      metadata={metadata}
      onUpdateSuccess={onUpdateSuccess}
    />
  }

  if( readQuery.data.metadata.metadataSchema === "RaidoMetadataSchemaV2" ){
    const metadata = readQuery.data.metadata as RaidoMetadataSchemaV2
    const problems = findMetadataUpdateProblems(metadata);

    if( problems.length > 0 ){
      return <ComplicatedMetadataWarning
          metadata={metadata} problems={problems}/>
    }

    return <EditRaidoV1SchemaForm raid={readQuery.data.raid}
                                  metadata={metadata}
                                  onUpdateSuccess={onUpdateSuccess}
    />
  }
  
  if( readQuery.data.metadata.metadataSchema === "LegacyMetadataSchemaV1" ){
    return <UpgradeLegacySchemaForm onUpgradeSuccess={onUpdateSuccess} 
      raid={readQuery.data.raid} 
      metadata={readQuery.data.metadata as LegacyMetadataSchemaV1}/>
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
    <InfoField id="globalUrl" label="CNRI URL" value={
      <NewWindowLink href={formatCnriUrl(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="raidoHandle" label="Agency URL" value={
      <NewWindowLink href={getRaidLandingPagePath(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="servicePoint" label="Service point"
      value={servicePointName ?? ""}
    />
  </InfoFieldList>;
}
