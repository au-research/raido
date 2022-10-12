import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parseOptionalPageSuffixParams, parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React, { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ApiKey,
  GenerateApiTokenRequest, MintRaidRequestV1,
  UpdateApiKeyRequest
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Alert,
  Checkbox,
  FormControl,
  FormControlLabel,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { ContentCopy, West } from "@mui/icons-material";
import {
  addDays,
  formatLocalDateAsIso,
  formatLocalDateAsIsoShortDateTime
} from "Util/DateUtil";
import { RqQuery } from "Util/ReactQueryUtil";

const log = console;

const pageUrl = "/mint-raid";

export function getMintRaidPageLink(servicePointId: number): string{
  return `${pageUrl}/${servicePointId}`;
}

export function isMintRaidPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number{
  return parsePageSuffixParams<number>(nav, isMintRaidPagePath, Number)
}

export function MintRaidPage(){
  return <NavTransition isPagePath={isMintRaidPagePath}
    title={raidoTitle("Mint RAiD")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [servicePointId] = 
    useState(getServicePointIdFromPathname(nav));

  return <LargeContentMain>
    <MintRaidContainer 
      servicePointId={servicePointId}
      onCreate={(handle)=>{
        //nav.replace(getViewApiKeyPageLink(createdId));
        //setApiKeyId(createdId);
      }}
    />
  </LargeContentMain>
}

function isDifferent(formData: ApiKey, original: ApiKey){
  return formData.subject !== original.subject ||
    formData.role !== original.role ||
    formData.enabled !== original.enabled ||
    formData.tokenCutoff?.getTime() !== original.tokenCutoff?.getTime();
}

function MintRaidContainer({servicePointId, onCreate}: {
  servicePointId: number,
  onCreate: (handle: string)=>void,
}){
  const api = useAuthApi();
  const [formData, setFormData] = useState({
    // id set to null signals creation is being requested  
    handle: undefined as unknown as string,
    servicePointId: servicePointId,
    name: "",
    startDate: new Date(),
  } as MintRaidRequestV1);
  const mintRequest = useMutation(
    async (data: MintRaidRequestV1) => {
      return await api.raido.mintRaidV1({mintRaidRequestV1: data});
    },
    {
      onSuccess: async () => {
      },
    }
  );

  return <ContainerCard title={"Mint RAiD"} action={<MintRaidHelp/>}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      //mintRequest.mutate({apiKey: {...formData}});
    }}>
      <Stack spacing={2}>
        <TextField id="name" label="Name" variant="outlined"
          focused autoCorrect="off" autoCapitalize="on"
          disabled={false}
          value={formData.name}
          onChange={(e) => {
            setFormData({...formData, name: e.target.value});
          }}
        />
        <TextField id="startDate" label="Start date" variant="outlined"
          disabled={true}
          value={formatLocalDateAsIso(formData.startDate)}
        />
        <Stack direction={"row"} spacing={2}>
          <SecondaryButton onClick={navBrowserBack}
            disabled={mintRequest.isLoading}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="submit" context={"mint raid"}
            disabled={true}
            isLoading={mintRequest.isLoading}
            error={mintRequest.error}
          >
            Mint RAiD
          </PrimaryActionButton>
        </Stack>
        <CompactErrorPanel error={mintRequest.error} />
      </Stack>
    </form>
  </ContainerCard>
}

function MintRaidHelp(){
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
