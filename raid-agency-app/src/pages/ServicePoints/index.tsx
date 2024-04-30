import SingletonServicePointApi from "@/SingletonServicePointApi";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { useAuthHelper } from "@/components/useAuthHelper";
import { FindServicePointByIdRequest, ServicePoint } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import ServicePointCreateForm from "@/pages/ServicePoint/components/ServicePointCreateForm";
import { Breadcrumb } from "@/types";
import {
  Cancel as CancelIcon,
  CheckCircleOutline as CheckCircleOutlineIcon,
  Home as HomeIcon,
  Hub as HubIcon,
  Key as KeyIcon,
  People as PeopleIcon,
} from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
} from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { useQuery } from "@tanstack/react-query";
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
    renderCell: ({ row }) => {
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
    renderCell: ({ row }) => {
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
    renderCell: ({ row }) => {
      return row.enabled ? (
        <CheckCircleOutlineIcon sx={{ color: "success.main" }} />
      ) : (
        <CancelIcon sx={{ color: "error.main" }} />
      );
    },
  },
];

export default function ServicePoints() {
  const servicePointApi = SingletonServicePointApi.getInstance();
  const { keycloak, initialized } = useCustomKeycloak();

  const {
    hasServicePointGroup,
    isServicePointUser,
    groupId,
    isOperator,
    isGroupAdmin,
    userServicePointId,
  } = useAuthHelper();

  const fetchAllServicePointsForOperator = async () => {
    return await servicePointApi.findAllServicePoints({
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const fetchOneServicePointForAdmin = async ({
    userServicePointId,
  }: {
    userServicePointId: number;
  }) => {
    const servicePoints: ServicePoint[] = [];
    const findServicePointByIdRequest: FindServicePointByIdRequest = {
      id: userServicePointId,
    };
    const data = await servicePointApi.findServicePointById(
      findServicePointByIdRequest,
      {
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      }
    );
    servicePoints.push(data);
    return servicePoints;
  };

  const getDataQuery = async () => {
    if (isOperator) {
      return await fetchAllServicePointsForOperator();
    } else if (isGroupAdmin) {
      return await fetchOneServicePointForAdmin({
        userServicePointId: userServicePointId!,
      });
    } else {
      return [];
    }
  };

  const query = useQuery<ServicePoint[]>({
    queryKey: ["servicePoints"],
    queryFn: getDataQuery,
    enabled: initialized && keycloak.authenticated,
  });

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  console.log(query.data);

  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "Service points",
      to: "/service-points",
      icon: <HubIcon />,
    },
  ];

  return (
    <Container>
      <Stack direction="column" gap={2}>
        <BreadcrumbsBar breadcrumbs={breadcrumbs} />
        {isOperator && (
          <Card variant="outlined">
            <CardHeader title="Create new service point" />
            <CardContent>
              <ServicePointCreateForm />
            </CardContent>
          </Card>
        )}

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
