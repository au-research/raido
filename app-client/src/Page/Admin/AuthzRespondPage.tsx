import { isPagePath, NavTransition } from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { normalisePath } from "Util/Location";
import { RqQuery } from "Util/ReactQueryUtil";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AuthzRequest,
  UpdateAuthzRequestStatusRequest
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { Stack, TextField } from "@mui/material";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import {
  PrimaryActionButton,
  PrimaryButton,
  SecondaryButton
} from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";

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
    return <TextSpan>Unable to parse Authz Request Id from browser location bar</TextSpan>;
  }
  
  return <LargeContentMain>
    <AuthzResponseContainer authzRequestId={authzRequestId}/>
  </LargeContentMain>
}

function AuthzResponseContainer({authzRequestId}:{authzRequestId: number}){
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = 'readAuthzRequest';
  const query: RqQuery<AuthzRequest> = useQuery(
    [queryName, authzRequestId], 
    async () => {
      return await api.admin.readRequestAuthz({authzRequestId});
    }
  );
  const updateRequest = useMutation(
    async (data: UpdateAuthzRequestStatusRequest) => {
      console.log("update", data);
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

  return <ContainerCard title={"Authorisation request"}>
    <Stack spacing={2} >
      <TextSpan>Service point: {query.data.servicePointName}</TextSpan>
      <TextSpan>Email: {query.data.email}</TextSpan>
      <TextSpan>ID Provider: {query.data.idProvider}</TextSpan>
      <TextSpan>Requested:{" "} 
        {formatLocalDateAsIsoShortDateTime(query.data.dateRequested)}
      </TextSpan>
      <TextSpan>Status: {query.data.status}</TextSpan>
      <TextField id="reqeust-text" label="Comments / Information"
        multiline rows={4} variant="outlined"
        value={query.data.comments}
        disabled={true}
      />
      { query.data.dateResponded && <>
        <TextSpan>Responder: {query.data.email}</TextSpan>
        <TextSpan>
          Responded:{' '}
          {formatLocalDateAsIsoShortDateTime(query.data.dateResponded)}
        </TextSpan>
      </>}
      <Stack direction={"row"} spacing={2}>
        <SecondaryButton onClick={navBrowserBack}
          disabled={updateRequest.isLoading}>
          Back
        </SecondaryButton>
        { query.data.status === "REQUESTED" && <>
          <PrimaryButton color={"error"}
            disabled={updateRequest.isLoading || query.isLoading}
            isLoading={updateRequest.isLoading}
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
            isLoading={updateRequest.isLoading}
            error={updateRequest.error}
            onClick={() => updateRequest.mutate({
              updateAuthzRequestStatus: {
                authzRequestId, status: "APPROVED"
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