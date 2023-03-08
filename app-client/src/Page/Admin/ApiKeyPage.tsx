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
import React, { ReactNode, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ApiKey,
  GenerateApiTokenRequest,
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
      return result;
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );
  const generateRequest = useMutation(
    async (data: GenerateApiTokenRequest) => {
      return await api.admin.generateApiToken({
        generateApiTokenRequest: data
      });
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

  const canEditSubject = !apiKeyId;
  const isWorking = query.isLoading || updateRequest.isLoading;
  const isValid = !!formData.subject;
  const hasChanged = isDifferent(formData, query.data);
  const canGenerateToken = 
    !isWorking && !hasChanged && !updateRequest.isLoading && !!apiKeyId;
  const canSubmitUpdate = 
    !isWorking && hasChanged && isValid && !updateRequest.isLoading;
  
  return <ContainerCard title={"API Key"} action={<ApiKeyHelp/>}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      updateRequest.mutate({apiKey: {...formData}});
    }}>
      <Stack spacing={2}>
        <TextField id="subject" label="Subject" variant="outlined"
          autoFocus autoCorrect="off" autoCapitalize="on"
          disabled={isWorking || !canEditSubject}
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
              if( !apiKeyId ){
                throw new Error("cannot call generate without an apiKeyId");
              }
              generateRequest.mutate({apiKeyId});
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
        { generateRequest.data &&
          <Alert severity="success">
            <Typography>
              An API Token has been generated.
              <br/>
              Click to copy to clipboard <IconButton onClick={()=>
              navigator.clipboard.writeText(generateRequest.data?.apiToken!)
            }><ContentCopy/></IconButton>
            </Typography>
          </Alert>
        }
        <CompactErrorPanel error={generateRequest.error} />
      </Stack>
    </form>
  </ContainerCard>
}

function ApiKeyHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <HelpList>
        <HelpItem><HelpChip label={"Subject"}/>
          Subject cannot be updated after the API key has been created.
        </HelpItem>
        <HelpItem><HelpChip label={"Expire"}/>
          Note that if you use "Extend expiry" - you still need to generate a 
          new token if your old tokens are expired. Old, previously generated 
          tokens have their "expires at" set to the old value as at the time 
          they were generated.
          <br/>
          The "extend expiry" button exists to allow you to extend your 
          api-key without having to create a whole new api-key with a new 
          subject - it won't let you define old, stale tokens that are past 
          their expiry date as "still valid" .
        </HelpItem>
        <HelpItem><HelpChip label={"Enabled"}/>
          Setting the api-key to "disabled" invalidates all issued api-key
          tokens, regardless of their expiry date.
        </HelpItem>
        <HelpItem><HelpChip label={"Generate token"}/>
          This generates a brand new token based on the current api-key stored 
          on the server.  Raido does not store api tokens on the server, every 
          time you click the "Generate token" button, the token generated has 
          an updated "issued at" claim, and the "expires at" claim is based on 
          the api-key "Expire" field. 
        </HelpItem>
      </HelpList>
    </Stack>
  }/>;
}

function HelpList({children}:{children: ReactNode}){
  return <ul>
    {children}
  </ul>
}

function HelpItem({children}:{children: ReactNode}){
  return <li style={{marginBottom: ".5em"}}>
    {children}
  </li>
}