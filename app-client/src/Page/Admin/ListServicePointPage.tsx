import {TextSpan} from "Component/TextSpan";
import React from "react";
import {RqQuery} from "Util/ReactQueryUtil";
import {useQuery} from "@tanstack/react-query";
import {ServicePoint} from "Generated/Raidv2";
import {useAuthApi} from "Api/AuthApi";
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import {
    Card,
    CardContent,
    CardHeader,
    Container,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import {RefreshIconButton} from "Component/RefreshIconButton";
import {RaidoLink} from "Component/RaidoLink";
import {Key, People, Visibility, VisibilityOff} from "@mui/icons-material";
import {RaidoAddFab} from "Component/AppButton";


export function ListServicePointPage() {
    const api = useAuthApi();
    const query: RqQuery<ServicePoint[]> = useQuery(
        ['listServicePoint'], async () => await api.servicePoint.findAllServicePoints());

    if (query.error) {
        return <CompactErrorPanel error={query.error}/>
    }

    if (query.isLoading) {
        return <TextSpan>loading...</TextSpan>
    }

    if (!query.data) {
        console.log("unexpected state", query);
        return <TextSpan>unexpected state</TextSpan>
    }
    return (
        <Container>
            <Card>
                <CardHeader
                    title={"Service points"}
                    action={<>
                        <RefreshIconButton
                            refreshing={query.isLoading}
                            onClick={() => query.refetch()}/>
                        <RaidoAddFab disabled={false} href="/service-point"/>
                    </>}/>
                <CardContent>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Service point</TableCell>
                                    <TableCell align="center">Users</TableCell>
                                    <TableCell align="center">API Keys</TableCell>
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
                                            <RaidoLink href={`/service-point/${row.id}`}>
                                                {row.name}
                                            </RaidoLink>
                                        </TableCell>
                                        <TableCell align="center">
                                            <RaidoLink href={`/list-app-user/${row.id}`}>
                                                <People/>
                                            </RaidoLink>
                                        </TableCell>
                                        <TableCell align="center">
                                            <RaidoLink href={`/list-api-key/${row.id}`}>
                                                <Key/>
                                            </RaidoLink>
                                        </TableCell>
                                        <TableCell align="center">
                                            {row.enabled ?
                                                <Visibility color={"success"}/> :
                                                <VisibilityOff color={"error"}/>
                                            }
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </CardContent>
            </Card>
        </Container>
    )
}
