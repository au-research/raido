import {
  isPagePath,
  NavPathResult,
  NavTransition
} from "Design/NavigationProvider";
import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import { LargeContentMain } from "Design/LayoutMain";
import {
  DateTimeDisplay,
  IdProviderDisplay,
  raidoTitle,
  RoleDisplay
} from "Component/Util";
import {
  Fab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@mui/material";
import { useAuthApi } from "Api/AuthApi";
import { useQuery } from "@tanstack/react-query";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { TextSpan } from "Component/TextSpan";
import { useAuth } from "Auth/AuthProvider";
import { RqQuery } from "Util/ReactQueryUtil";
import { RaidListItemV1 } from "Generated/Raidv2";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { CompactLinearProgress } from "Component/SmallPageSpinner";
import { Add } from "@mui/icons-material";
import { getMintRaidPageLink } from "Page/MintRaidPage";

const log = console;

const pageUrl = "/home";

export function getHomePageLink(): string{
  return pageUrl;
}

export function isHomePagePath(pathname: string): NavPathResult{
  const pathResult = isPagePath(pathname, pageUrl);
  if( pathResult.isPath ){
    return pathResult;
  }

  // use this page as the "default" or "home" page for the app  
  if( pathname === "" || pathname === "/" ){
    return {isPath: true, pathSuffix: ""};
  }

  return { isPath: false }
}

export function HomePage(){
  return <NavTransition isPagePath={isHomePagePath} 
    title={raidoTitle("Home")}>
    <Content/>
  </NavTransition>
}


function Content(){
  const {session: {payload: user}} = useAuth();
  return <LargeContentMain>
    <RaidCurrentUser/>
    <br/>
    <RaidTableContainer servicePointId={user.servicePointId}/>
  </LargeContentMain>
}

function RaidCurrentUser(){
  const api = useAuthApi();
  const {session: {payload: user}} = useAuth();
  const spQuery = useQuery(['readServicePoint', user.servicePointId],
    async () => await api.admin.readServicePoint({
      servicePointId: user.servicePointId }));
  return <ContainerCard title={"Signed-in user"}>
    <InfoFieldList>
      <InfoField id={"email"} label={"Email"} value={user.email}/>
      <InfoField id={"idProvider"} label={"ID provider"}
        value={<IdProviderDisplay payload={user}/> }/>
      <InfoField id={"servicePoint"} label={"Service point"} value={spQuery.data?.name || ""}/>
      <InfoField id={"role"} label={"Role"} value={<RoleDisplay role={user.role}/>}/>
    </InfoFieldList>
    <CompactErrorPanel error={spQuery.error}/>
  </ContainerCard>
  
}
function RaidTableContainer({servicePointId}: {servicePointId: number}){
  const api = useAuthApi();
  const raidQuery: RqQuery<RaidListItemV1[]> = 
    useQuery(['listRaids', servicePointId], async () => {
      return await api.raido.listRaid({
        raidListRequest: {servicePointId: servicePointId}
      });
    });

  if( raidQuery.error ){
    return <CompactErrorPanel error={raidQuery.error}/>
  }

  return <ContainerCard title={"Recently minted RAiD data"}
    action={<>
      <RefreshIconButton onClick={() => raidQuery.refetch()} 
        refreshing={raidQuery.isLoading || raidQuery.isRefetching} />
      <Fab href={getMintRaidPageLink(servicePointId)} color="primary"
        size="small"
      ><Add/></Fab>
    </>}
  >
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell >Name</TableCell>
            <TableCell >Handle</TableCell>
            <TableCell >Start date</TableCell>
          </TableRow>
        </TableHead>
        { raidQuery.isLoading &&
          <TableBody><TableRow style={{border: 0}}>
            <TableCell colSpan={10} style={{border: 0, padding: 0}}>
              <CompactLinearProgress isLoading={true}/>
            </TableCell>
          </TableRow></TableBody>
        }
        { !raidQuery.isLoading && raidQuery.data?.length === 0 &&
          <TableBody><TableRow style={{border: 0}}>
            <TableCell colSpan={10} style={{border: 0, padding: 0, textAlign: "center"}} >
              <TextSpan style={{lineHeight: "3em"}}>No RAiD data has been minted yet.</TextSpan>
            </TableCell>
          </TableRow></TableBody>
        }
        <TableBody>
          { raidQuery.data?.map((row) => (
            <TableRow
              key={row.handle}
              // don't render a border under last row
              sx={{'&:last-child td, &:last-child th': {border: 0}}}
            >
              <TableCell>
                <TextSpan>{row.name || ''}</TextSpan>
              </TableCell>
              <TableCell>
                <TextSpan>{row.handle || ''}</TextSpan>
              </TableCell>
              <TableCell>
                <DateTimeDisplay date={row.startDate}/>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

    </TableContainer>
    
  </ContainerCard>
}