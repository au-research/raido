import { NavTransition } from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { normalisePath } from "Util/Location";
import { RqQuery } from "Util/ReactQueryUtil";
import { useQuery } from "@tanstack/react-query";
import { AuthzRequest } from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Table, TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";
import { RefreshIconButton } from "Component/RefreshIconButton";

const log = console;

const pageUrl = "/admin-authz-request";

export function getAdminAuthzRequestPageLink(): string{
  return pageUrl;
}

export function isAdminAuthzRequestPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function AdminAuthzRequestPage(){
  return <NavTransition isPath={isAdminAuthzRequestPagePath}
    title={raidoTitle("Authorisation requests")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  return <LargeContentMain>
    <AuthzRequestContainer/>
  </LargeContentMain>
}

function AuthzRequestContainer(){
  const api = useAuthApi();
  const query: RqQuery<AuthzRequest[]> = useQuery(
    ['listAuthzRequest'], async () => await api.admin.listAuthzRequest());

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
            <TableCell>Email</TableCell>
            <TableCell>ID provider</TableCell>
            <TableCell>Requested</TableCell>
            <TableCell>Status</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {query.data.map((row) => (
            <TableRow
              key={row.servicePointId}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell component="th" scope="row">
                {row.servicePointName}
              </TableCell>
              <TableCell>{row.email}</TableCell>
              <TableCell>{row.idProvider}</TableCell>
              <TableCell>{formatLocalDateAsIsoShortDateTime(row.dateRequested)}</TableCell>
              <TableCell>{row.status}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

    </TableContainer>
  </ContainerCard>
}