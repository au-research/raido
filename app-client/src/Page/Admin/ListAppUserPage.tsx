import {TextSpan} from "Component/TextSpan";
import React from "react";
import {useQuery} from "@tanstack/react-query";
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
import {Visibility, VisibilityOff} from "@mui/icons-material";
import {getAppUserPageLink} from "Page/Admin/AppUserPage";
import {useParams} from "react-router-dom";

export function ListAppUserPage() {
    const {servicePointId} = useParams() as { servicePointId: string };
    const api = useAuthApi();

    const usersQuery = useQuery(['listAppUser', servicePointId],
        async () => await api.admin.listAppUser({servicePointId: parseInt(servicePointId)}));
    const servicePointQuery = useQuery(['readServicePoint', servicePointId],
        async () => await api.servicePoint.findServicePointById({id: parseInt(servicePointId)}));

    if (usersQuery.error) {
        return <CompactErrorPanel error={usersQuery.error}/>
    }

    if (usersQuery.isLoading) {
        return <p>loading...</p>
    }

    if (!usersQuery.data) {
        console.log("unexpected state", usersQuery);
        return <TextSpan>unexpected state</TextSpan>
    }
    return (
        <Container>
            <Card>
                <CardHeader
                    title={`${servicePointQuery.data?.name ?? '...'} - Users`}
                    action={<>
                        <RefreshIconButton
                            refreshing={usersQuery.isLoading}
                            onClick={() => usersQuery.refetch()}/>
                    </>
                    }/>
                <CardContent>
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