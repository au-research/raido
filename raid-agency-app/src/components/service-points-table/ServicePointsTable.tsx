import { ServicePoint } from "@/generated/raid";
import {
  Cancel as CancelIcon,
  CheckCircleOutline as CheckCircleOutlineIcon,
} from "@mui/icons-material";
import { Button, Tooltip } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { NavLink } from "react-router-dom";

const columns: GridColDef[] = [
  {
    field: "id",
    headerName: "ID",
    width: 125,
    renderCell: ({ value }) => {
      return (
        <NavLink to={`/service-points/${value}`}>
          <Button
            variant="outlined"
            size="small"
            fullWidth={true}
            sx={{ textTransform: "none" }}
          >
            {new Intl.NumberFormat("en-AU", {})
              .format(+value)
              .replace(/,/g, " ")}
          </Button>
        </NavLink>
      );
    },
  },
  {
    field: "enabled",
    headerName: "Enabled?",
    renderCell: ({ row }) => {
      return row.enabled ? (
        <CheckCircleOutlineIcon sx={{ color: "success.main" }} />
      ) : (
        <CancelIcon sx={{ color: "error.main" }} />
      );
    },
  },
  { field: "name", headerName: "Name", width: 350 },
  { field: "prefix", headerName: "Prefix", width: 175 },
  { field: "repositoryId", headerName: "Repository Id", width: 200 },
  {
    field: "groupId",
    headerName: "Group ID?",
    renderCell: ({ row }) => {
      return row.groupId ? (
        <Tooltip title={row.groupId} placement="left">
          <CheckCircleOutlineIcon sx={{ color: "success.main" }} />
        </Tooltip>
      ) : (
        <CancelIcon sx={{ color: "error.main" }} />
      );
    },
  },
];

export function ServicePointsTable({
  servicePoints,
}: {
  servicePoints: ServicePoint[];
}) {
  return (
    <DataGrid
      rows={servicePoints}
      columns={columns}
      rowSelection={false}
      density="compact"
      autoHeight
      isRowSelectable={() => false}
      initialState={{
        sorting: {
          sortModel: [{ field: "id", sort: "asc" }],
        },
        pagination: {
          paginationModel: {
            pageSize: 25,
          },
        },
      }}
      pageSizeOptions={[5, 10, 25, 50]}
      disableRowSelectionOnClick
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
  );
}
