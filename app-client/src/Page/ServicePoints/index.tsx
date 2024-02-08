import {
  Cancel as CancelIcon,
  CheckCircleOutline as CheckCircleOutlineIcon,
  Key as KeyIcon,
  People as PeopleIcon,
} from "@mui/icons-material";
import {
  Breadcrumbs,
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
  Typography,
} from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import ServicePointCreateForm from "Component/ServicePointCreateForm";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { ServicePoint } from "Generated/Raidv2";
import { Link, NavLink } from "react-router-dom";

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
            SP {value}
          </Button>
        </NavLink>
      );
    },
  },
  { field: "name", headerName: "Name", width: 350 },
  { field: "prefix", headerName: "Prefix", width: 200 },
  {
    field: "users",
    headerName: "Users",
    renderCell: ({ value, row }) => {
      return (
        <NavLink to={`/list-app-user/${row.id}`}>
          <Button
            variant="outlined"
            size="small"
            fullWidth={true}
            sx={{ textTransform: "none" }}
          >
            <PeopleIcon />
          </Button>
        </NavLink>
      );
    },
  },
  {
    field: "apikeys",
    headerName: "API Keys",
    renderCell: ({ value, row }) => {
      return (
        <NavLink to={`/list-api-key/${row.id}`}>
          <Button
            variant="outlined"
            size="small"
            fullWidth={true}
            sx={{ textTransform: "none" }}
          >
            <KeyIcon />
          </Button>
        </NavLink>
      );
    },
  },
  {
    field: "enabled",
    headerName: "Enabled?",
    renderCell: ({ value, row }) => {
      return row.enabled ? (
        <CheckCircleOutlineIcon sx={{ color: "#36f443" }} />
      ) : (
        <CancelIcon sx={{ color: "#f44336" }} />
      );
    },
  },
];

export default function ServicePoints() {
  const api = useAuthApi();
  const query = useQuery<ServicePoint[]>(
    ["servicePoints"],
    async () => await api.servicePoint.findAllServicePoints()
  );

  if (query.isLoading) {
    return <Typography>loading...</Typography>;
  }

  if (query.error) {
    return <CompactErrorPanel error={query.error} />;
  }

  return (
    <Container>
      <Stack direction="column" gap={2} sx={{ mt: 2 }}>
        <Card variant="outlined">
          <CardHeader title="Create new service point" />
          <CardContent>
            <ServicePointCreateForm />
          </CardContent>
        </Card>

        <Breadcrumbs aria-label="breadcrumb">
          <Link to="/">Home</Link>
          <Typography color="text.primary">Service points</Typography>
        </Breadcrumbs>

        <DataGrid
          rows={query.data || []}
          columns={columns}
          rowSelection={false}
          density="compact"
          autoHeight
          isRowSelectable={() => false}
          initialState={{
            sorting: {
              sortModel: [{ field: "id", sort: "desc" }],
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
      </Stack>
    </Container>
  );
}
