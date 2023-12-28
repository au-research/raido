import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import {DateTimeDisplay, raidoTitle, RoleDisplay} from "Component/Util";
import {LargeContentMain} from "Design/LayoutMain";
import {ContainerCard} from "Design/ContainerCard";
import {TextSpan} from "Component/TextSpan";
import React from "react";
import {useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import {Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {RefreshIconButton} from "Component/RefreshIconButton";
import {RaidoLink} from "Component/RaidoLink";
import {Visibility, VisibilityOff} from "@mui/icons-material";
import {getCreateApiKeyPageLink, getViewApiKeyPageLink} from "Page/Admin/ApiKeyPage";
import {RaidoAddFab} from "Component/AppButton";

const log = console;

const pageUrl = "/list-api-key";

export function getListApiKeyPageLink(servicePointId: number): string{
  return `${pageUrl}/${servicePointId}`;
}

export function isListApiKeyPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number {
  return parsePageSuffixParams<number>(nav, isListApiKeyPagePath, Number)
}

export function ListApiKeyPage(){
  return <NavTransition isPagePath={(pathname)=>isPagePath(pathname, pageUrl)} 
    title={raidoTitle("API keys")}
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
  const apiKeysQuery = useQuery(['listApiKey', servicePointId], 
    async () => await api.admin.listApiKey({servicePointId}) );
  const servicePointQuery = useQuery(['readServicePoint', servicePointId], 
    async () => await api.servicePoint.findServicePointById({id: servicePointId}) );

  if( apiKeysQuery.error ){
    return <CompactErrorPanel error={apiKeysQuery.error}/>
  }

  if( apiKeysQuery.isLoading ){
    return <TextSpan>loading...</TextSpan>
  }

  if( !apiKeysQuery.data ){
    console.log("unexpected state", apiKeysQuery);
    return <TextSpan>unexpected state</TextSpan>
  }

  return <ContainerCard 
    title={`${servicePointQuery.data?.name ?? '...'} - API keys`}
    action={<>
      <RefreshIconButton refreshing={apiKeysQuery.isLoading} 
        onClick={()=>apiKeysQuery.refetch()} />
      <RaidoAddFab disabled={false} href={getCreateApiKeyPageLink(servicePointId)}/>
    </>}
  >
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Subject</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Expires</TableCell>
            <TableCell align="center">Enabled</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          { apiKeysQuery.data.map((row) => (
            <TableRow
              key={row.id}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell>
                <RaidoLink href={getViewApiKeyPageLink(row.id)}>
                  {row.subject}
                </RaidoLink>
              </TableCell>
              <TableCell>
                <RoleDisplay role={row.role}/>
              </TableCell>
              <TableCell>
                <DateTimeDisplay date={row.tokenCutoff}/>
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

