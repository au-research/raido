import { isPagePath, NavTransition } from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { normalisePath } from "Util/Location";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { RaidoLink } from "Component/RaidoLink";
import { getAuthzRespondPageLink } from "Page/Admin/AuthzRespondPage";
import { AuthzRequestExtraV1 } from "Generated/Raidv2";

const log = console;

const pageUrl = "/admin-authz-request";

export function getAdminAuthzRequestPageLink(): string{
  return pageUrl;
}

export function isAdminAuthzRequestPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function AdminAuthzRequestPage(){
  return <Content/>
}


function Content(){
  return <LargeContentMain>
    <AuthzRequestContainer/>
  </LargeContentMain>
}

function AuthzRequestContainer(){
  const api = useAuthApi();
  const query = useQuery(['listAuthzRequest'], 
    async () => await api.admin.listAuthzRequest() );

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

  return <ContainerCard title={"Authorisation requests"}
    action={<RefreshIconButton refreshing={query.isLoading} onClick={()=>query.refetch()}/>}
  >
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Service point</TableCell>
            <TableCell>Identity</TableCell>
            <TableCell>ID Provider</TableCell>
            <TableCell>Requested</TableCell>
            <TableCell>Status</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {query.data.map((row) => (
            <TableRow
              key={row.id}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell component="th" scope="row">
                {row.servicePointName}
              </TableCell>
              <TableCell><IdentityDisplay request={row}/></TableCell>
              <TableCell>{row.idProvider}</TableCell>
              <TableCell>
                {formatLocalDateAsIsoShortDateTime(row.dateRequested)}
              </TableCell>
              <TableCell>
                <RaidoLink href={getAuthzRespondPageLink(row.id)}>
                  {row.status}
                </RaidoLink>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

    </TableContainer>
  </ContainerCard>
}

function IdentityDisplay({request}:{request: AuthzRequestExtraV1}){
  return <>{request.email}</>
}