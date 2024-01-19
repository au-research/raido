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
import { TextSpan } from "Component/TextSpan";
import React from "react";
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
import { RefreshIconButton } from "Component/RefreshIconButton";
import { RaidoLink } from "Component/RaidoLink";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { getAppUserPageLink } from "Page/Admin/AppUserPage";
import {useParams} from "react-router-dom";

const log = console;

const pageUrl = "/list-app-user";

export function getListAppUserPageLink(servicePointId: number): string{
  return `${pageUrl}/${servicePointId}`;
}

export function isListAppUserPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number {
  return parsePageSuffixParams<number>(nav, isListAppUserPagePath, Number)
}

export function ListAppUserPage(){
  return <Content/>
}

function Content(){
  const {servicePointId} = useParams() as { servicePointId: string };
  const nav = useNavigation()
  return <LargeContentMain>
    <AppUserListTable servicePointId={+servicePointId}/>
  </LargeContentMain>
}

function AppUserListTable({servicePointId}: {
  servicePointId: number,
}){
  const api = useAuthApi();
  const usersQuery = useQuery(['listAppUser', servicePointId], 
    async () => await api.admin.listAppUser({servicePointId}) );
  const servicePointQuery = useQuery(['readServicePoint', servicePointId], 
    async () => await api.servicePoint.findServicePointById({id: servicePointId}) );

  if( usersQuery.error ){
    return <CompactErrorPanel error={usersQuery.error}/>
  }

  if( usersQuery.isLoading ){
    return <TextSpan>loading...</TextSpan>
  }

  if( !usersQuery.data ){
    console.log("unexpected state", usersQuery);
    return <TextSpan>unexpected state</TextSpan>
  }

  return <ContainerCard 
    title={`${servicePointQuery.data?.name ?? '...'} - Users`}
    action={<>
      <RefreshIconButton refreshing={usersQuery.isLoading} 
        onClick={()=>usersQuery.refetch()} />
    </>}
  >
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Identity</TableCell>
            <TableCell>ID Provider</TableCell>
            <TableCell align="center">Enabled</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {usersQuery.data.map((row) => (
            <TableRow
              key={row.id}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell>
                <RaidoLink href={getAppUserPageLink(row.id)}>
                  {row.email}
                </RaidoLink>
              </TableCell>
              <TableCell>
                {row.idProvider}
              </TableCell>
              <TableCell align="center">
                { row.enabled ?
                  <Visibility color={"success"}/> : 
                  <VisibilityOff color={"error"}/>
                }
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

    </TableContainer>
  </ContainerCard>
}