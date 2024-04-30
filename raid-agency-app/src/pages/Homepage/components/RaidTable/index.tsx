import { FindAllRaidsRequest, RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { DataGrid, GridColDef, GridToolbar } from "@mui/x-data-grid";
import { useQuery } from "@tanstack/react-query";

import SingletonRaidApi from "@/SingletonRaidApi";
import LoadingPage from "@/pages/LoadingPage";
import {
  Alert,
  Card,
  CardContent,
  CardHeader,
  LinearProgress,
  Stack,
  TableContainer,
  Typography,
} from "@mui/material";
import RaidTableRowContextMenu from "./RaidTableRowContextMenu";
import { endDateColumn } from "./columns/endDateColumn";
import { handleColumn } from "./columns/handleColumn";
import { startDateColumn } from "./columns/startDateColumn";
import { titleColumn } from "./columns/titleColumn";

export default function RaidTable({ title }: { title?: string }) {
  const raidApi = SingletonRaidApi.getInstance();
  const { keycloak, initialized } = useCustomKeycloak();

  const servicePointId = keycloak.tokenParsed?.service_point;

  const listRaids = async ({ servicePointId }: FindAllRaidsRequest) => {
    const findAllRaidsRequest: FindAllRaidsRequest = {
      servicePointId: servicePointId,
    };

    return raidApi.findAllRaids(findAllRaidsRequest, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const raidQuery = useQuery<RaidDto[]>({
    queryKey: ["listRaids", servicePointId],
    queryFn: () => listRaids({ servicePointId }),
    enabled: initialized && keycloak.authenticated,
  });

  if (raidQuery.isPending) {
    return <LoadingPage />;
  }

  const appWritesEnabled = true;

  if (raidQuery.isError) {
    return <Typography variant="h6">No data.</Typography>;
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
      {!appWritesEnabled ? (
        <Alert severity="warning">
          Editing is disabled for this service point.
        </Alert>
      ) : (
        <></>
      )}

      <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
        {title && <CardHeader title={title} />}
        <CardContent>
          <TableContainer>
            {raidQuery.isPending && (
              <>
                <Stack gap={2}>
                  <Typography>Loading RAiDs...</Typography>
                  <LinearProgress />
                </Stack>
              </>
            )}
            {raidQuery.data && (
              <DataGrid
                loading={raidQuery.isPending}
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
                  pagination: {
                    paginationModel: { pageSize: 10 },
                  },
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
          </TableContainer>
        </CardContent>
      </Card>
    </>
  );
}
