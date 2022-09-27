import { NavTransition } from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import React from "react";
import { normalisePath } from "Util/Location";
import { RqQuery } from "Util/ReactQueryUtil";
import { useQuery } from "@tanstack/react-query";
import { ServicePoint } from "Generated/Raidv2";
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
import { getServicePointPageLink } from "Page/Admin/ServicePointPage";
import { Add, Visibility, VisibilityOff } from "@mui/icons-material";

const log = console;

const pageUrl = "/list-service-point";

export function getListServicePointPageLink(): string{
  return pageUrl;
}

export function isListServicePointPagePath(path: string): boolean{
  return normalisePath(path).startsWith(pageUrl);
}

export function ListServicePointPage(){
  return <NavTransition isPath={isListServicePointPagePath}
    title={raidoTitle("Service points")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  return <LargeContentMain>
    <ServicePointListTable/>
  </LargeContentMain>
}

function ServicePointListTable(){
  const api = useAuthApi();
  const query: RqQuery<ServicePoint[]> = useQuery(
    ['listServicePoint'], async () => await api.admin.listServicePoint());

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

  return <ContainerCard title={"Service points"}
    action={<>
      <RefreshIconButton refreshing={query.isLoading} 
        onClick={()=>query.refetch()} />
      <Fab href={getServicePointPageLink(undefined)} color="primary" size="small"
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
                <RaidoLink href={getServicePointPageLink(row.id)}>
                  {row.name}
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