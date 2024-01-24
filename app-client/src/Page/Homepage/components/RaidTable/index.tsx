import {FindAllRaidsRequest, RaidDto} from "../../../../Generated/Raidv2";
import React from "react";
import {useAuthApi} from "../../../../Api/AuthApi";
import {RqQuery} from "../../../../Util/ReactQueryUtil";
import {useQuery} from "@tanstack/react-query";
import {CompactErrorPanel} from "../../../../Error/CompactErrorPanel";
import {DataGrid, GridColDef, GridToolbar} from "@mui/x-data-grid";

import {Add as AddIcon} from "@mui/icons-material"

import {Alert, Card, CardContent, CardHeader, Fab, LinearProgress, Stack, Typography} from "@mui/material";
import {handleColumn} from "./columns/handleColumn";
import {titleColumn} from "./columns/titleColumn";
import {startDateColumn} from "./columns/startDateColumn";
import {endDateColumn} from "./columns/endDateColumn";
import {useAuth} from "../../../../Auth/AuthProvider";
import RaidTableRowContextMenu from "./RaidTableRowContextMenu";

export default function RaidTable() {
    const api = useAuthApi();
    const auth = useAuth();

    const servicePointId = auth.session.payload.servicePointId

    const listRaids = async ({servicePointId}: FindAllRaidsRequest) => {
        return await api.raid.findAllRaids({
            servicePointId,
        });
    };

    const raidQuery: RqQuery<RaidDto[]> = useQuery(
        ["listRaids", servicePointId],
        () => listRaids({servicePointId}),
    );

    const spQuery = useQuery(
        ["readServicePoint", servicePointId],
        async () =>
            await api.servicePoint.findServicePointById({
                id: servicePointId!,
            }),
    );

    const appWritesEnabled = spQuery.data?.appWritesEnabled;

    if (raidQuery.error) {
        return <CompactErrorPanel error={raidQuery.error}/>;
    }

    const columns: GridColDef[] = [
        handleColumn,
        titleColumn,
        startDateColumn,
        endDateColumn,
        {
            field: "_",
            headerName: "",
            disableColumnMenu: true,
            width: 25,
            disableExport: true,
            disableReorder: true,
            filterable: false,
            hideable: false,
            renderCell: (params) => <RaidTableRowContextMenu row={params.row}/>,
            sortable: false,
        }
    ];

    return (
        <>
            {!appWritesEnabled && !spQuery.isLoading ? (
                <Alert severity="warning">
                    Editing is disabled for this service point.
                </Alert>
            ) : (
                <></>
            )}

            <Fab
                variant="extended"
                color="primary"
                sx={{position: "fixed", bottom: "16px", right: "16px"}}
                component="button"
                type="submit"
                href={`/mint-raid-new/${servicePointId}`}
            >
                <AddIcon sx={{mr: 1}}/>
                Mint new RAiD
            </Fab>

            <Card className="raid-card">
                <CardHeader title="Recently minted RAiD data"/>
                <CardContent>
                    {raidQuery.isLoading && (
                        <>
                            <Stack gap={2}>
                                <Typography>Loading RAiDs...</Typography>
                                <LinearProgress/>
                            </Stack>
                        </>
                    )}
                    {raidQuery.data && (
                        <DataGrid
                            loading={raidQuery.isLoading}
                            slots={{toolbar: GridToolbar}}
                            slotProps={{toolbar: {printOptions: {disableToolbarButton: true}}}}
                            rows={raidQuery.data}
                            columns={columns}
                            density="compact"
                            autoHeight
                            isRowSelectable={() => false}
                            getRowId={(row) => row.identifier.id}
                            initialState={{
                                pagination: {paginationModel: {pageSize: 10}},
                                columns: {
                                    columnVisibilityModel: {
                                        avatar: false,
                                        primaryDescription: false,
                                    },
                                },
                            }}
                            pageSizeOptions={[10, 25, 50, 100]}
                            sx={{// Neutralize the hover colour (causing a flash)
                                "& .MuiDataGrid-row.Mui-hovered": {
                                    backgroundColor: "transparent",
                                },
                                // Take out the hover colour
                                "& .MuiDataGrid-row:hover": {
                                    backgroundColor: "transparent",
                                },
                            }}
                        />
                    )}
                </CardContent>
            </Card>
        </>
    );
}