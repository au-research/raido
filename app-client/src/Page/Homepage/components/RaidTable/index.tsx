import {ListRaidsV1Request, RaidDto} from "../../../../Generated/Raidv2";
import React from "react";
import {useAuthApi} from "../../../../Api/AuthApi";
import {useAuth} from "../../../../Auth/AuthProvider";
import {RqQuery} from "../../../../Util/ReactQueryUtil";
import {useQuery} from "@tanstack/react-query";
import {CompactErrorPanel} from "../../../../Error/CompactErrorPanel";
import {DataGrid, GridColDef} from "@mui/x-data-grid";
import {Alert, Card, CardContent, CardHeader, Fab} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {RefreshIconButton} from "../../../../Component/RefreshIconButton";
import SettingsMenu from "../SettingsMenu";
import {handleColumn} from "./columns/handleColumn";
import {titleColumn} from "./columns/titleColumn";
import {startDateColumn} from "./columns/startDateColumn";
import {endDateColumn} from "./columns/endDateColumn";
import {linkColumn} from "./columns/linkColumn";


export default function RaidTable({servicePointId}: ListRaidsV1Request) {

    const api = useAuthApi();
    const {
        session: {payload: user},
    } = useAuth();

    const listRaids = async ({servicePointId}: ListRaidsV1Request) => {
        return await api.raid.listRaidsV1({
            servicePointId,
        });
    };

    const raidQuery: RqQuery<RaidDto[]> = useQuery(
        ["listRaids", servicePointId],
        () => listRaids({servicePointId}),
    );

    const spQuery = useQuery(
        ["readServicePoint", user.servicePointId],
        async () =>
            await api.admin.readServicePoint({
                servicePointId: user.servicePointId,
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
        linkColumn
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
                href={"/mint-raid-new/20000000"}
            >
                <AddIcon sx={{mr: 1}}/>
                Mint new RAiD
            </Fab>

            <Card
                sx={{
                    borderLeft: "solid",
                    borderLeftColor: "primary.main",
                    borderLeftWidth: 3,
                }}
            >
                <CardHeader
                    title="Recently minted RAiD data"
                    action={
                        <>
                            <SettingsMenu raidData={raidQuery.data}/>
                            <RefreshIconButton
                                onClick={() => raidQuery.refetch()}
                                refreshing={raidQuery.isLoading || raidQuery.isRefetching}
                            />
                        </>
                    }
                />
                <CardContent>
                    {raidQuery.data && (
                        <DataGrid
                            rows={raidQuery.data}
                            columns={columns}
                            density="compact"
                            autoHeight
                            isRowSelectable={(params) => false}
                            getRowId={(row) => row.identifier.globalUrl}
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
                        />
                    )}
                </CardContent>
            </Card>
        </>
    );
}