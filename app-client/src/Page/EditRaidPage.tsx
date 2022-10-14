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
import { useMutation, useQuery } from "@tanstack/react-query";
import { ApiKey, MintRaidRequestV1, ServicePoint } from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { Stack, TextField } from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import { RqQuery } from "Util/ReactQueryUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import Divider from "@mui/material/Divider";
import { assert } from "Util/TypeUtil";

const log = console;

const pageUrl = "/edit-raid";

export function getEditRaidPageLink(handle: string): string{
  return `${pageUrl}/${handle}`;
}

export function isEditRaidPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getRaidHandleFromPathname(nav: NavigationState): string{
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

function isDifferent(formData: ApiKey, original: ApiKey){
  return formData.subject !== original.subject ||
    formData.role !== original.role ||
    formData.enabled !== original.enabled ||
    formData.tokenCutoff?.getTime() !== original.tokenCutoff?.getTime();
}

function EditRaidContainer({handle}: {
  handle: string,
}){
  const api = useAuthApi();
  const raidQueryName = 'readRaid';
  const [formData, setFormData] = useState({
    // id set to null signals creation is being requested  
    handle,
    name: "",
    startDate: new Date(),
  } as MintRaidRequestV1);
  const raidQuery: RqQuery<MintRaidRequestV1> = useQuery(
    [raidQueryName, handle],
    async () => {
      let raid = await api.basicRaid.readRaidV1({
        readRaidV1Request: { handle }
      });
      setFormData({...raid});
      return raid;
    }
  );
  
  const servicePointId = raidQuery.data?.servicePointId
  const spQuery: RqQuery<ServicePoint> = useQuery(
    ['readServicePoint', servicePointId],
    async () => {
      assert(servicePointId);
      return await api.admin.readServicePoint({servicePointId});
    },
    {enabled: !!servicePointId}
  );
  
  const updateRequest = useMutation(
    async (data: MintRaidRequestV1) => {
      return await api.basicRaid.updateRaidV1({mintRaidRequestV1: data});
    },
    {
      onSuccess: async () => {
      },
    }
  );

  const isNameValid = !!formData.name;
  const canSubmit = isNameValid;
  const isWorking = updateRequest.isLoading;
  
  return <ContainerCard title={`Edit RAiD`} action={<EditRaidHelp/>}>
    <InfoFieldList>
      <InfoField id="handle" label="Handle"
        value={handle}
      />
      <InfoField id="servicePoint" label="Service point"
        value={spQuery.data?.name}
      />
    </InfoFieldList>
    <Divider variant={"middle"}
      style={{marginTop: "1em", marginBottom: "1.5em"}}
    />

    <form autoComplete="off" onSubmit={async (e) => {
      e.preventDefault();
      await updateRequest.mutate({...formData});
    }}>
      <Stack spacing={2}>
        <TextField id="name" label="Name" variant="outlined"
          autoFocus autoCorrect="off" autoCapitalize="on"
          required disabled={isWorking}
          value={formData.name}
          onChange={(e) => {
            setFormData({...formData, name: e.target.value});
          }}
          error={!isNameValid}
        />
        <DesktopDatePicker label={"Start date"} inputFormat="YYYY-MM-DD"
          disabled={isWorking}
          value={formData.startDate}
          onChange={(newValue: Dayjs | null) => {
            setFormData({...formData, startDate: newValue?.toDate()})
          }}
          renderInput={(params) => <TextField {...params} />}
        />
        <Stack direction={"row"} spacing={2}>
          <SecondaryButton onClick={navBrowserBack}
            disabled={isWorking}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="submit" context={"minting raid"}
            disabled={!canSubmit}
            isLoading={isWorking}
            error={updateRequest.error}
          >
            Update
          </PrimaryActionButton>
        </Stack>
        <CompactErrorPanel error={updateRequest.error} />
      </Stack>
    </form>
  </ContainerCard>
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
