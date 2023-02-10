import { isPagePath, NavTransition } from "Design/NavigationProvider";
import { DateTimeDisplay, raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React, { useState } from "react";
import { normalisePath } from "Util/Location";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AuthzRequestExtraV1,
  UpdateAuthzRequestStatusRequest
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField
} from "@mui/material";
import {
  PrimaryActionButton,
  PrimaryButton,
  SecondaryButton
} from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { mapClientIdToIdProvider } from "Component/IdProviderDisplay";
import { NewWindowLink } from "Component/ExternalLink";

const log = console;

const pageUrl = "/authz-respond";

export function getAuthzRespondPageLink(authzRequestId: number): string{
  return `${pageUrl}?authzRequestId=${authzRequestId}`;
}

export function getAuthzRequestIdFromLocation(): number | null{
  const urlParams = new URLSearchParams(window.location.search);
  return Number(urlParams.get('authzRequestId'));
}

export function isAuthzRespondPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function AuthzRespondPage(){
  return <NavTransition isPagePath={(pathname)=>isPagePath(pathname, pageUrl)}
    title={raidoTitle("Authorisation request response")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const authzRequestId = getAuthzRequestIdFromLocation();
  if( !authzRequestId ){
    return <TextSpan>
      Unable to parse Authz Request Id from browser location bar
    </TextSpan>;
  }
  
  return <LargeContentMain>
    <AuthzResponseContainer authzRequestId={authzRequestId}/>
  </LargeContentMain>
}

function AuthzResponseContainer({authzRequestId}:{authzRequestId: number}){
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = 'readAuthzRequest';
  const [role, setRole] = useState("SP_USER");
  const [responseClicked, setResponseClicked] = useState(
    undefined as undefined | "REJECTED" | "APPROVED") ;
  
  const query = useQuery(
    [queryName, authzRequestId], 
    async () => {
      return await api.admin.readRequestAuthz({authzRequestId});
    }
  );
  const updateRequest = useMutation(
    async (data: UpdateAuthzRequestStatusRequest) => {
      if( data.updateAuthzRequestStatus.status === "REQUESTED" ){
        throw new Error("cannot change to requested");
      }
      setResponseClicked(data.updateAuthzRequestStatus.status);
      await api.admin.updateAuthzRequestStatus(data);
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

  if( query.isLoading ){
    return <TextSpan>loading...</TextSpan>
  }

  if( !query.data ){
    console.log("unexpected state", query);
    return <TextSpan>unexpected state</TextSpan>
  }

  const roleSelect = <FormControl focused>
    <InputLabel id="roleLabel">Role</InputLabel>
    <Select
      labelId="roleLabel"
      id="roleSelect"
      value={role}
      label="Role"
      onChange={(event: SelectChangeEvent) => {
        setRole(event.target.value);
      }}
    >
      <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
      <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
    </Select>
  </FormControl>

  const responseInfo = <InfoFieldList>
    <InfoField id={"responderEmail"} label={"Responder"} 
      value={query.data.respondingUserEmail}/>
    <InfoField id={"respondedDate"} label={"Responded"} 
      value={<DateTimeDisplay date={query.data.dateResponded}/>}/>
  </InfoFieldList>

  return <ContainerCard title={"Authorisation request"}>
    <Stack spacing={2} >
      <InfoFieldList>
        <InfoField id="servicePointName" label="Service point"
          value={query.data.servicePointName}/>
        <InfoField id="identity" label="Identity" value={query.data.email}/>
        <InfoField id="idProvider" label="ID provider" 
          value={query.data.idProvider}/>
        <SubjectField id="subject" label="Subject" request={query.data}/>
        <InfoField id="requestedDate" label="Requested"
          value={<DateTimeDisplay date={query.data.dateRequested}/>}/>
        <InfoField id="status" label="Status" value={query.data.status}/>
      </InfoFieldList>
      <TextField id="reqeust-text" label="Comments / Information"
        multiline rows={4} variant="outlined"
        value={query.data.comments}
        // I kept missing that there were comments, because the field was marked 
        // disabled (grey), I kept not reading the "greyed out" filler text.
        InputProps={{readOnly: true}}
        error={!!query.data.comments}
      />
      
      { query.data.status === "REQUESTED" ? roleSelect : responseInfo }
      
      <Stack direction={"row"} spacing={2}>
        <SecondaryButton onClick={navBrowserBack}
          disabled={updateRequest.isLoading}>
          Back
        </SecondaryButton>
        { query.data.status === "REQUESTED" && <>
          <PrimaryButton color={"error"}
            disabled={updateRequest.isLoading || query.isLoading}
            isLoading={
              updateRequest.isLoading && responseClicked === "REJECTED" 
            }
            onClick={() => updateRequest.mutate({
              updateAuthzRequestStatus: {
                authzRequestId, status: "REJECTED"
              }
            })}
          >
            Reject
          </PrimaryButton>
          <PrimaryActionButton context={"request response"} color={"success"}
            disabled={updateRequest.isLoading || query.isLoading}
            isLoading={
              updateRequest.isLoading && responseClicked === "APPROVED"
            }
            error={updateRequest.error}
            onClick={() => updateRequest.mutate({
              updateAuthzRequestStatus: {
                authzRequestId, status: "APPROVED", role
              }
            })}
          >
            Approve
          </PrimaryActionButton>
        </>}
      </Stack>
    </Stack>
  </ContainerCard>
}

function SubjectField({request, id, label}:{
  request: AuthzRequestExtraV1,
  id: string,
  label: string,
}){
  const idp = mapClientIdToIdProvider(request.clientId);
  if( idp === "ORCiD" ){
    return <InfoField id={id} label={label}
      value={
      <NewWindowLink href={`https://orcid.org/${request.subject}`}>
        {request.subject}
      </NewWindowLink>
    }/> 
  } 
  return <InfoField id={id} label={label} value={request.subject}/>
}