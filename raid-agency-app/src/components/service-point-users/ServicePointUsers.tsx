import { useSnackbar } from "@/components/snackbar";
import { Button, Typography } from "@mui/material";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { updateUserServicePointUserRole } from "@/services/service-points";
import { ServicePointWithMembers } from "@/types";
import { Check as CheckIcon, Circle as CircleIcon } from "@mui/icons-material";
import { Chip, Stack } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const ServicePointUsers = ({
  servicePointWithMembers,
}: {
  servicePointWithMembers?: ServicePointWithMembers;
}) => {
  const { keycloak } = useKeycloak();

  const queryClient = useQueryClient();
  const snackbar = useSnackbar();

  const modifyUserAccessMutation = useMutation({
    mutationFn: updateUserServicePointUserRole,
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
      renderCell: ({ row }) => (
        <span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>
          {row.attributes.firstName}
        </span>
      ),
    },
    {
      field: "lastName",
      headerName: "Last name",
      width: 125,
      renderCell: ({ row }) => (
        <span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>
          {row.attributes.lastName}
        </span>
      ),
    },
    {
      field: "userName",
      headerName: "User name",
      width: 175,
      renderCell: ({ row }) => (
        <span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>
          {row.attributes.username}
        </span>
      ),
    },
    {
      field: "email",
      headerName: "Email",
      width: 175,
      renderCell: ({ row }) => (
        <span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>
          {row.attributes.email}
        </span>
      ),
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
                token: keycloak?.token as string,
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
                userGroupId: servicePointWithMembers?.groupId as string,
                operation: "revoke",
                token: keycloak?.token as string,
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
};
