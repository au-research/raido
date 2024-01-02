import {ListRaidsV1Request, RaidDto} from "../../../Generated/Raidv2";
import React from "react";
import {useAuthApi} from "../../../Api/AuthApi";
import {useAuth} from "../../../Auth/AuthProvider";
import {RqQuery} from "../../../Util/ReactQueryUtil";
import {useQuery} from "@tanstack/react-query";
import {CompactErrorPanel} from "../../../Error/CompactErrorPanel";
import {DataGrid, GridColDef} from "@mui/x-data-grid";
import {RaidoLink} from "../../../Component/RaidoLink";
import {TextSpan} from "../../../Component/TextSpan";
import {Alert, Button, Card, CardContent, CardHeader, Fab} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {RefreshIconButton} from "../../../Component/RefreshIconButton";
import SettingsMenu from "./SettingsMenu";

export default function RaidTableContainerV2({ servicePointId }: ListRaidsV1Request) {

    const api = useAuthApi();
    const {
        session: { payload: user },
    } = useAuth();

    const listRaids = async ({ servicePointId }: ListRaidsV1Request) => {
        return await api.raid.listRaidsV1({
            servicePointId,
        });
    };

    const raidQuery: RqQuery<RaidDto[]> = useQuery(
        ["listRaids", servicePointId],
        () => listRaids({ servicePointId }),
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
        return <CompactErrorPanel error={raidQuery.error} />;
    }

    const columns: GridColDef[] = [
        {
            field: "identifier",
            headerName: "Handle",
            width: 100,
            renderCell: (params) => {
                const [prefix, suffix] = new URL(params.row.identifier.id).pathname
                    .substring(1)
                    .split("/");
                return (
                    <RaidoLink href={`/show-raid/${prefix}/${suffix}`}>
                        <TextSpan>{suffix}</TextSpan>
                    </RaidoLink>
                );
            },
        },
        {
            field: "title",
            headerName: "Primary Title",
            flex: 1,
            minWidth: 500,
            sortComparator: (a, b) => {
                return a[0].text.localeCompare(b[0].text);
            },

            renderCell: (params) => {
                return params.row.title.map((title: any, index: number) => {
                    return title.text + (index < params.row.title.length - 1 ? ", " : "");
                });
            },
        },
        {
            field: "date.startDate",
            headerName: "Start Date",
            width: 100,
            renderCell: (params) => {
                return params.row.date.startDate;
            },
            sortable: false,
        },
        {
            field: "date.endDate",
            headerName: "End Date",
            width: 100,
            renderCell: (params) => {
                return params.row.date.endDate;
            },
            sortable: false,
        },
        {
            field: "dataciteLink",
            headerName: "Datacite",
            width: 100,
            renderCell: (params) => {
                const [_, suffix] = new URL(params.row.identifier.id).pathname
                    .substring(1)
                    .split("/");
                return (
                    <Button
                        size={"small"}
                        variant={"outlined"}
                        href={`https://doi.test.datacite.org/dois/10.82841%2F${suffix}`}
                    >
                        Datacite
                    </Button>
                );
            },
            sortable: false,
        },
    ];

    const handleRowClick = (event: any) => {
        const [prefix, suffix] = new URL(event.row.identifier.id).pathname
            .substring(1)
            .split("/");

        window.location.href = `/show-raid/${prefix}/${suffix}`;
    };

    return (
        <>
            {/* Ensure the `readServicePoint` data has completely loaded before evaluating `spQuery`.
        This prevents a flash of the warning message when the page first loads.
    */}
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
                sx={{ position: "fixed", bottom: "16px", right: "16px" }}
                component="button"
                type="submit"
                href={"/mint-raid-new/20000000"}
            >
                <AddIcon sx={{ mr: 1 }} />
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
                            <SettingsMenu raidData={raidQuery.data} />
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
                            getRowId={(row) => row.identifier.globalUrl}
                            onRowClick={handleRowClick}
                            initialState={{
                                pagination: { paginationModel: { pageSize: 10 } },
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