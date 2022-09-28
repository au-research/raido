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
  Fab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { RaidoLink } from "Component/RaidoLink";
import { Add, Visibility, VisibilityOff } from "@mui/icons-material";
import { getAppUserPageLink } from "Page/Admin/AppUserPage";

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
  return <NavTransition isPagePath={(pathname)=>isPagePath(pathname, pageUrl)} 
    title={raidoTitle("Users")}
  >
    <Content/>
  </NavTransition>
}

function Content(){
  const nav = useNavigation()
  return <LargeContentMain>
    <AppUserListTable servicePointId={getServicePointIdFromPathname(nav)}/>
  </LargeContentMain>
}

function AppUserListTable({servicePointId}: {
  servicePointId: number,
}){
  const api = useAuthApi();
  const query = useQuery(['listAppUser'], 
    async () => await api.admin.listAppUser({servicePointId}) );

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

  return <ContainerCard title={"Users"}
    action={<>
      <RefreshIconButton refreshing={query.isLoading} 
        onClick={()=>query.refetch()} />
      <Fab href={"#"} 
        //href={getServicePointPageLink(undefined)} 
        color="primary" size="small"
      >
        <Add/>
      </Fab>
    </>}
  >
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Service point</TableCell>
            <TableCell align="center">Enabled</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {query.data.map((row) => (
            <TableRow
              key={row.id}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell scope="row">
                <RaidoLink href={getAppUserPageLink(row.id)}>
                  {row.email}
                </RaidoLink>
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