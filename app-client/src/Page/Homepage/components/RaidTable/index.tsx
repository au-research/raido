import {ListRaidsV1Request, RaidDto} from "../../../../Generated/Raidv2";
import React from "react";
import {useAuthApi} from "../../../../Api/AuthApi";
import {useAuth} from "../../../../Auth/AuthProvider";
import {RqQuery} from "../../../../Util/ReactQueryUtil";
import {useQuery} from "@tanstack/react-query";
import {CompactErrorPanel} from "../../../../Error/CompactErrorPanel";
import {DataGrid, GridColDef} from "@mui/x-data-grid";

import {
    OpenInNew as OpenInNewIcon,
    Visibility as VisibilityIcon,
    Edit as EditIcon,
    Add as AddIcon,
    MoreVert as MoreVertIcon
} from "@mui/icons-material"

import {
    Alert,
    Button,
    Card,
    CardContent,
    CardHeader,
    Fab,
    ListItemIcon,
    ListItemText,
    Menu,
    MenuItem
} from "@mui/material";

import {RefreshIconButton} from "../../../../Component/RefreshIconButton";
import SettingsMenu from "../SettingsMenu";
import {handleColumn} from "./columns/handleColumn";
import {titleColumn} from "./columns/titleColumn";
import {startDateColumn} from "./columns/startDateColumn";
import {endDateColumn} from "./columns/endDateColumn";


import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import ContentCopy from '@mui/icons-material/ContentCopy';
import copy from "clipboard-copy";
import Divider from "@mui/material/Divider";


export default function RaidTable({servicePointId}: ListRaidsV1Request) {

    const [additionalData, setAdditionalData] = React.useState<RaidDto>({} as RaidDto);
    const [prefix, setPrefix] = React.useState<string>("")
    const [suffix, setSuffix] = React.useState<string>("")
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLButtonElement>, additionalDataPayload: RaidDto) => {
        const identifierSplit = additionalDataPayload?.identifier?.id.split("/") || []
        const suffix = identifierSplit[identifierSplit.length - 1] || "";
        const prefix = identifierSplit[identifierSplit.length - 2] || "";

        setAdditionalData(additionalDataPayload)
        setPrefix(prefix)
        setSuffix(suffix)
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
        setTimeout(() => {
            setAdditionalData({} as RaidDto)
            setPrefix("")
            setSuffix("")
        }, 500)

    };


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
        {
            field: "_",
            headerName: "",
            disableColumnMenu: true,
            width: 125,
            renderCell: (params) => {
                const [_, suffix] = new URL(params.row.identifier.id).pathname
                    .substring(1)
                    .split("/");
                return (
                    <Button
                        variant="text"
                        endIcon={<MoreVertIcon/>}
                        aria-controls={open ? 'basic-menu' : undefined}
                        aria-haspopup="true"
                        aria-expanded={open ? 'true' : undefined}
                        onClick={(event) => handleClick(event, params.row)}
                    >
                        Actions
                    </Button>
                );
            },
            sortable: false,
        }
    ];

    return (
        <>
            <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
            >
                <MenuItem onClick={async (event) => {
                    await copy(`${suffix}`);
                    handleClose()
                }}>
                    <ListItemIcon>
                        <ContentCopy fontSize="small"/>
                    </ListItemIcon>
                    <ListItemText primary="Copy Suffix" secondary={`${suffix}`}/>
                </MenuItem>
                <MenuItem onClick={async (event) => {
                    await copy(`${prefix}/${suffix}`);
                    handleClose()
                }}>
                    <ListItemIcon>
                        <ContentCopy fontSize="small"/>
                    </ListItemIcon>
                    <ListItemText primary="Copy Handle" secondary={`${prefix}/${suffix}`}/>
                </MenuItem>
                <Divider/>
                <MenuItem onClick={() => {
                    handleClose()
                    window.location.href = `/show-raid/${prefix}/${suffix}`
                }}>

                    <ListItemIcon>
                        <VisibilityIcon fontSize="small"/>
                    </ListItemIcon>
                    <ListItemText>Show RAiD</ListItemText>

                </MenuItem>
                <MenuItem onClick={() => {
                    handleClose()
                    window.location.href = `/show-raid/${prefix}/${suffix}`
                }}>

                    <ListItemIcon>
                        <EditIcon fontSize="small" />
                    </ListItemIcon>
                    <ListItemText>Edit RAiD</ListItemText>

                </MenuItem>
                <Divider/>
                <MenuItem onClick={() => {
                    handleClose()
                    window.location.href = `https://doi.test.datacite.org/dois/${prefix}%2F${suffix}`
                }}>
                    <ListItemIcon>
                        <OpenInNewIcon fontSize="small"/>
                    </ListItemIcon>
                    <ListItemText primary="Open in Datacite" secondary="Must be signed in to Fabrica to display"/>

                </MenuItem>
            </Menu>
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