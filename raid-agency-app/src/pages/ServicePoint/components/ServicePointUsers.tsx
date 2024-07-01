import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import useSnackbar from "@/components/Snackbar/useSnackbar";
import { ServicePoint } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { Button } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";

import { useAuthHelper } from "@/components/useAuthHelper";
import { Check as CheckIcon, Circle as CircleIcon } from "@mui/icons-material";
import { Card, CardContent, Chip, Stack } from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { updateUserServicePointUserRole } from "@/services/service-points";

const VITE_KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL as string;
const VITE_KEYCLOAK_REALM = import.meta.env.VITE_KEYCLOAK_REALM as string;

const url = `${VITE_KEYCLOAK_URL}/realms/${VITE_KEYCLOAK_REALM}/group`;

export default function ServicePointUsers({
  servicePoint,
}: {
  servicePoint?: ServicePoint;
}) {
  const { isOperator } = useAuthHelper();
  const { keycloak } = useCustomKeycloak();

  const queryClient = useQueryClient();
  const snackbar = useSnackbar();

  const modifyUserAccessMutation = useMutation({
    mutationFn: updateUserServicePointUserRole,
    onError: (error) => {
      console.error(error);
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["servicePointUsers"],
      });
      snackbar.openSnackbar(
        `✅ Success: ${variables.operation} role service-point-user `
      );
    },
  });

  const columns: GridColDef[] = [
    {
      field: "firstName",
      headerName: "First name",
      width: 125,
      renderCell: ({ row }) => {
        return row.attributes.firstName;
      },
    },
    {
      field: "lastName",
      headerName: "Last name",
      width: 125,
      renderCell: ({ row }) => {
        return row.attributes.lastName;
      },
    },
    {
      field: "userName",
      headerName: "User name",
      width: 175,
      renderCell: ({ row }) => {
        return row.attributes.username;
      },
    },
    {
      field: "email",
      headerName: "Email",
      width: 175,
      renderCell: ({ row }) => {
        return row.attributes.email;
      },
    },
    {
      field: "roles",
      headerName: "Roles",
      width: 150,
      renderCell: ({ row }) => {
        return (
          <Stack direction="column" spacing={1} sx={{ my: 1 }}>
            {row.roles
              ?.filter((el: string[]) => !el.includes("default-roles"))
              .map((el: string) => (
                <Chip
                  key={el}
                  variant="outlined"
                  color="primary"
                  size="small"
                  label={el}
                  icon={<CircleIcon color="success" sx={{ height: 8 }} />}
                />
              ))}
          </Stack>
        );
      },
    },
    {
      field: "grant",
      headerName: "Grant",
      width: 150,
      renderCell: ({ row }) => {
        return (
          <Button
            sx={{ my: 1 }}
            size="small"
            variant="outlined"
            color="success"
            startIcon={<CheckIcon />}
            disabled={row?.roles?.includes("service-point-user")}
            onClick={() => {
              modifyUserAccessMutation.mutate({
                userId: row.id,
                userGroupId: servicePoint?.groupId as string,
                operation: "grant",
                token: keycloak.token as string,
              });
            }}
          >
            Grant service-point-user
          </Button>
        );
      },
    },
    {
      field: "revoke",
      headerName: "Revoke",
      width: 150,
      renderCell: ({ row }) => {
        return (
          <Button
            sx={{ my: 1 }}
            size="small"
            variant="outlined"
            color="error"
            startIcon={<CheckIcon />}
            disabled={!row?.roles?.includes("service-point-user")}
            onClick={() => {
              modifyUserAccessMutation.mutate({
                userId: row.id,
                userGroupId: servicePoint?.groupId as string,
                operation: "revoke",
                token: keycloak.token as string,
              });
            }}
          >
            Revoke service-point-user
          </Button>
        );
      },
    },
  ];

  async function fetchServicePointUsers() {
    const requestUrl =
      isOperator && servicePoint?.groupId
        ? `${url}?groupId=${servicePoint?.groupId}`
        : url;

    const response = await fetch(requestUrl, {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
    return await response.json();
  }

  const query = useQuery({
    queryKey: ["servicePointUsers"],
    queryFn: fetchServicePointUsers,
  });

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  return (
    <>
      <Card>
        <CardContent>
          <DataGrid
            rows={query.data.members}
            columns={columns}
            rowSelection={false}
            density="compact"
            getRowHeight={() => "auto"}
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
        </CardContent>
      </Card>
    </>
  );
}
