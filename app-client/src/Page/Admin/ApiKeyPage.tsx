import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parseOptionalPageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React, { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ApiKey, UpdateApiKeyRequest } from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField
} from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpPopover } from "Component/HelpPopover";
import { West } from "@mui/icons-material";
import { addDays, formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import { RqQuery } from "Util/ReactQueryUtil";

const log = console;

const viewPageUrl = "/api-key";
const createPageUrl = "/create-api-key";

export function getViewApiKeyPageLink(apiKeyId: number): string{
  return `${viewPageUrl}/${apiKeyId}`;
}

export function isViewApiKeyPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, viewPageUrl);
}

export function getApiKeyIdFromPathname(nav: NavigationState): number | undefined{
  return parseOptionalPageSuffixParams<number>(nav, isViewApiKeyPagePath, Number)
}

export function getCreateApiKeyPageLink(servicePointId: number): string{
  return `${createPageUrl}/${servicePointId}`;
}

export function isCreateApiKeyPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, createPageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number|undefined{
  return parseOptionalPageSuffixParams<number>(nav, isCreateApiKeyPagePath, Number)
}

export function isEitherPagePath(pathname: string): NavPathResult{
  let isViewPath = isViewApiKeyPagePath(pathname);
  if( isViewPath.isPath ){
    return isViewPath;
  }
  return isCreateApiKeyPagePath(pathname);
}

export function ApiKeyPage(){
  return <NavTransition isPagePath={isEitherPagePath}
    title={raidoTitle("API key")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [apiKeyId, setApiKeyId] =
    useState(getApiKeyIdFromPathname(nav));
  const [servicePointId, setServicePointId] = 
    useState(getServicePointIdFromPathname(nav));

  return <LargeContentMain>
    <ApiKeyContainer apiKeyId={apiKeyId}
      servicePointId={servicePointId}
      onCreate={(createdId)=>{
        nav.replace(getViewApiKeyPageLink(createdId));
        setApiKeyId(createdId);
      }}
    />
  </LargeContentMain>
}

function isDifferent(formData: ApiKey, original: ApiKey){
  console.log("isDifferent", {
    formCutoff: formData.tokenCutoff,
    originalCutoff: original.tokenCutoff,
  });
  return formData.subject !== original.subject ||
    formData.role !== original.role ||
    formData.enabled !== original.enabled ||
    formData.tokenCutoff?.getTime() !== original.tokenCutoff?.getTime();
}

function ApiKeyContainer({apiKeyId, servicePointId, onCreate}: {
  apiKeyId: number|undefined,
  servicePointId: number|undefined,
  onCreate: (aipKeyId: number)=>void,
}){
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = 'readApiKey';
  const [formData, setFormData] = useState({
    // id set to null signals creation is being requested  
    id: undefined as unknown as number,
    servicePointId: servicePointId,
    idProvider: "RAIDO_API",
    /* date will be ignored for create, but must be set to something or 
     the openApi generated code will fail */
    dateCreated: new Date(),

    // these are the editable fields
    subject: "",
    role: "SP_USER",
    tokenCutoff: addDays(new Date(), 365),
    enabled: true
  } as ApiKey);
  const query: RqQuery<ApiKey> = useQuery(
    [queryName, apiKeyId],
    async () => {
      if( apiKeyId ){
        let apiKey = await api.admin.readApiKey({
          apiKeyId: apiKeyId
        });
        setFormData({...apiKey});
        return apiKey;
      }
      else {
        return formData;
      }
    }
  );
  const updateRequest = useMutation(
    async (data: UpdateApiKeyRequest) => {
      const result = await api.admin.updateApiKey(data);
      if( !apiKeyId ){
        onCreate(result.id);
      }
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );

  if( query.error ){
    return <CompactErrorPanel error={query.error}/>
  }

  if( !query.data ){
    if( query.isLoading ){
      return <TextSpan>loading...</TextSpan>
    }
    else {
      console.log("unexpected state", query);
      return <TextSpan>unexpected state</TextSpan>
    }
  }

  const isWorking = query.isLoading || updateRequest.isLoading;
  const isValid = !!formData.subject;
  const hasChanged = isDifferent(formData, query.data);
  const canGenerateToken = 
    !isWorking && !hasChanged && !updateRequest.isLoading;
  const canSubmitUpdate = 
    !isWorking && hasChanged && isValid && !updateRequest.isLoading;
  console.log("render", {isWorking, isValid, hasChanged, canGenerateToken, canSubmitUpdate});
  
  return <ContainerCard title={"Authorisation request"} action={<ApiKeyHelp/>}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      updateRequest.mutate({apiKey: {...formData}});
    }}>
      <Stack spacing={2}>
        <TextField id="subject" label="Subject" variant="outlined"
          focused autoCorrect="off" autoCapitalize="on"
          disabled={isWorking}
          value={formData.subject || ''}
          onChange={(e) => {
            setFormData({...formData, subject: e.target.value});
          }}
        />
        <FormControl>
          <InputLabel id="roleLabel">Role</InputLabel>
          <Select
            labelId="roleLabel"
            id="roleSelect"
            value={formData.role ?? "SP_USER"}
            label="Role"
            onChange={(event: SelectChangeEvent) => {
              setFormData({...formData, role: event.target.value});
            }}
          >
            <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
            <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
          </Select>
        </FormControl>
        <FormControl>
          <Stack direction={"row"} spacing={2} alignItems={"center"}>
            <TextField id="expires" label="Expire"
              variant="outlined" disabled
              value={formatLocalDateAsIsoShortDateTime(formData.tokenCutoff)}
            />
            <West/>
            <SecondaryButton onClick={(e) => {
              e.preventDefault();
              setFormData({...formData,
                tokenCutoff: addDays(formData.tokenCutoff, 365) });
            }}>
              Extend expiry
            </SecondaryButton>
          </Stack>
        </FormControl>
        <FormControl>
          <FormControlLabel
            disabled={isWorking}
            label="Enabled"
            labelPlacement="start"
            style={{
              /* by default, MUI lays this out as <checkbox><label>.
               Doing `labelPlacement=start`, flips that around, but ends up 
               right-justigying the content, `marginRight=auto` pushes it back 
               across to the left and `marginLeft=0` aligns nicely. */
              marginLeft: 0,
              marginRight: "auto",
            }}
            control={
              <Checkbox
                checked={formData.enabled ?? true}
                onChange={() => {
                  setFormData({...formData, enabled: !formData.enabled})
                }}
              />
            }
          />
        </FormControl>        
        <Stack direction={"row"} spacing={2}>
          <SecondaryButton onClick={navBrowserBack}
            disabled={updateRequest.isLoading}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="button" context={"generate token"}
            error={undefined} variant={"outlined"}
            disabled={!canGenerateToken}
            onClick={e=>{
              e.preventDefault();
              alert("call server to generate token");
            }}
          >
            Generate token
          </PrimaryActionButton>
          <PrimaryActionButton type="submit" context={"update api key"}
            disabled={!canSubmitUpdate}
            isLoading={updateRequest.isLoading}
            error={updateRequest.error}
          >
            { apiKeyId ? "Update" : "Create" }
          </PrimaryActionButton>
        </Stack>
      </Stack>
    </form>
  </ContainerCard>
}

function ApiKeyHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <TextSpan>
        API key help placeholder
      </TextSpan>
    </Stack>
  }/>;
}
