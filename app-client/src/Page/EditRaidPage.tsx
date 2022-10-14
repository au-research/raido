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
import { ApiKey, MintRaidRequestV1 } from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { Stack, TextField } from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import { RqQuery } from "Util/ReactQueryUtil";

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
    title={raidoTitle("Edit RAiD")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [handle] = useState(getRaidHandleFromPathname(nav));

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
  const queryName = 'readRaid';
  const [formData, setFormData] = useState({
    // id set to null signals creation is being requested  
    handle,
    name: "",
    startDate: new Date(),
  } as MintRaidRequestV1);
  const query: RqQuery<MintRaidRequestV1> = useQuery(
    [queryName, handle],
    async () => {
      let raid = await api.basicRaid.readRaidV1({
        readRaidV1Request: { handle }
      });
      setFormData({...raid});
      return raid;
    }
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
  
  return <ContainerCard title={"Edit RAiD"} action={<EditRaidHelp/>}>
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
