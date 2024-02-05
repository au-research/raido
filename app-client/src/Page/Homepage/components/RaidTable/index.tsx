import { DataGrid, GridColDef, GridToolbar } from "@mui/x-data-grid";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { FindAllRaidsRequest, RaidDto } from "Generated/Raidv2";
import { RqQuery } from "Util/ReactQueryUtil";

import { Add as AddIcon } from "@mui/icons-material";

import {
  Alert,
  Card,
  CardContent,
  CardHeader,
  Fab,
  LinearProgress,
  Stack,
  Typography,
} from "@mui/material";
import { useAuth } from "Auth/AuthProvider";
import { Link } from "react-router-dom";
import RaidTableRowContextMenu from "./RaidTableRowContextMenu";
import { endDateColumn } from "./columns/endDateColumn";
import { handleColumn } from "./columns/handleColumn";
import { startDateColumn } from "./columns/startDateColumn";
import { titleColumn } from "./columns/titleColumn";

export default function RaidTable() {
  const api = useAuthApi();
  const auth = useAuth();

  const servicePointId = auth.session.payload.servicePointId;

  const listRaids = async ({ servicePointId }: FindAllRaidsRequest) => {
    return await api.raid.findAllRaids({
      servicePointId,
    });
  };

  const raidQuery: RqQuery<RaidDto[]> = useQuery(
    ["listRaids", servicePointId],
    () => listRaids({ servicePointId })
  );

  const spQuery = useQuery(
    ["readServicePoint", servicePointId],
    async () =>
      await api.servicePoint.findServicePointById({
        id: servicePointId!,
      })
  );

  const appWritesEnabled = spQuery.data?.appWritesEnabled;

  if (raidQuery.error) {
    return <CompactErrorPanel error={raidQuery.error} />;
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
      renderCell: (params) => <RaidTableRowContextMenu row={params.row} />,
      sortable: false,
    },
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
        sx={{ position: "fixed", bottom: "16px", right: "16px" }}
        component="button"
        type="button"
        data-testid="mint-raid-button"
      >
        <Link to={`/mint-raid-new/${servicePointId}`}>
          <AddIcon sx={{ mr: 1 }} />
          Mint new RAiD
        </Link>
      </Fab>

      <Card className="raid-card">
        <CardHeader title="Recently minted RAiD data" />
        <CardContent>
          {raidQuery.isLoading && (
            <>
              <Stack gap={2}>
                <Typography>Loading RAiDs...</Typography>
                <LinearProgress />
              </Stack>
            </>
          )}
          {raidQuery.data && (
            <DataGrid
              loading={raidQuery.isLoading}
              slots={{ toolbar: GridToolbar }}
              slotProps={{
                toolbar: { printOptions: { disableToolbarButton: true } },
              }}
              rows={raidQuery.data}
              columns={columns}
              density="compact"
              autoHeight
              isRowSelectable={() => false}
              getRowId={(row) => row.identifier.id}
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
              sx={{
                // Neutralize the hover colour (causing a flash)
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
