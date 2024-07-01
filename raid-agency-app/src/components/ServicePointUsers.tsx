import useSnackbar from "@/components/Snackbar/useSnackbar";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { Button, Typography } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";

import { ServicePointWithMembers } from "@/types";
import { Check as CheckIcon, Circle as CircleIcon } from "@mui/icons-material";
import { Chip, Stack } from "@mui/material";
import { useMutation, useQueryClient } from "@tanstack/react-query";

const VITE_KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL as string;
const VITE_KEYCLOAK_REALM = import.meta.env.VITE_KEYCLOAK_REALM as string;

const url = `${VITE_KEYCLOAK_URL}/realms/${VITE_KEYCLOAK_REALM}/group`;

export default function ServicePointUsers({
  servicePointWithMembers,
}: {
  servicePointWithMembers?: ServicePointWithMembers;
}) {
  const { keycloak } = useCustomKeycloak();

  const queryClient = useQueryClient();
  const snackbar = useSnackbar();

  const modifyUserAccessMutation = useMutation({
    mutationFn: async ({
      userId,
      userGroupId,
      operation,
    }: {
      userId: string;
      userGroupId: string;
      operation: "grant" | "revoke";
    }) => {
      const response = await fetch(`${url}/${operation}`, {
        method: "PUT",
        credentials: "include",
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userId, groupId: userGroupId }),
      });
      if (!response.ok) {
        throw new Error(`Failed to ${operation}`);
      }
      return response.json();
    },
    onError: (error) => {
      console.error(error);
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["servicePoints"],
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
              console.log({
                userId: row.id,
                userGroupId: servicePointWithMembers?.groupId as string,
                operation: "grant",
              });
              modifyUserAccessMutation.mutate({
                userId: row.id,
                userGroupId: servicePointWithMembers?.groupId as string,
                operation: "grant",
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
              console.log({
                userId: row.id,
                userGroupId: servicePointWithMembers?.groupId as string,
                operation: "grant",
              });
              modifyUserAccessMutation.mutate({
                userId: row.id,
                userGroupId: servicePointWithMembers?.groupId as string,
                operation: "revoke",
              });
            }}
          >
            Revoke service-point-user
          </Button>
        );
      },
    },
  ];

  return (
    <>
      <>
        <Typography variant="h6">{servicePointWithMembers?.name}</Typography>
        <DataGrid
          rows={servicePointWithMembers?.members || []}
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
      </>
    </>
  );
}
