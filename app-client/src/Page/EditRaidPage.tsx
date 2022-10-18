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
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  MintRaidRequestV1,
  ReadRaidResponseV1,
  ServicePoint
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  Stack,
  TextField
} from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import { RqQuery } from "Util/ReactQueryUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import Divider from "@mui/material/Divider";
import { assert } from "Util/TypeUtil";
import { NewWindowLink } from "Component/ExternalLink";
import { formatGlobalHandle } from "Page/Public/RaidLandingPage";

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

function isDifferent(formData: MintRaidRequestV1, original: MintRaidRequestV1){
  return formData.name !== original.name ||
    formData.startDate?.getDate() !== original.startDate?.getDate() ||
    formData.confidential !== original.confidential;
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
  const raidQuery: RqQuery<ReadRaidResponseV1> = useQuery(
    [raidQueryName, handle],
    async () => {
      //await delay(2000);
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

  const queryClient = useQueryClient();
  const updateRequest = useMutation(
    async (data: MintRaidRequestV1) => {
      return await api.basicRaid.updateRaidV1({mintRaidRequestV1: data});
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([raidQueryName]);
      },
    }
  );

  const isNameValid = !!formData.name;
  const hasChanged = raidQuery.data ? 
    isDifferent(formData, raidQuery.data) : false;
  const canSubmit = isNameValid && hasChanged;
  const isWorking = updateRequest.isLoading;

  return <ContainerCard title={`Edit RAiD`} action={<EditRaidHelp/>}>
    <CompactErrorPanel error={raidQuery.error}/>
    <InfoFieldList>
      <InfoField id="handle" label="Handle" value={
        <NewWindowLink href={formatGlobalHandle(handle)}>
          {handle}
        </NewWindowLink>
      }/>
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
          required disabled={isWorking || raidQuery.isLoading}
          value={formData.name}
          onChange={(e) => {
            setFormData({...formData, name: e.target.value});
          }}
          error={!!raidQuery.data && !isNameValid}
        />
        <DesktopDatePicker label={"Start date"} inputFormat="YYYY-MM-DD"
          disabled={isWorking || raidQuery.isLoading}
          value={formData.startDate}
          onChange={(newValue: Dayjs | null) => {
            setFormData({...formData, startDate: newValue?.toDate()})
          }}
          renderInput={(params) => <TextField {...params} />}
        />
        <FormControl>
          <FormControlLabel
            disabled={isWorking || raidQuery.isLoading}
            label="Confidential"
            labelPlacement="start"
            style={{
              /* by default, MUI lays this out as <checkbox><label>.
               Doing `labelPlacement=start`, flips that around, but ends up 
               right-justifying the content, `marginRight=auto` pushes it back 
               across to the left and `marginLeft=0` aligns nicely. */
              marginLeft: 0,
              marginRight: "auto",
            }}
            control={
              <Checkbox
                checked={formData.confidential ?? false}
                onChange={() => {
                  setFormData({...formData,
                    confidential: !formData.confidential
                  })
                }}
              />
            }
          />
        </FormControl>
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
        <CompactErrorPanel error={updateRequest.error}/>
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
