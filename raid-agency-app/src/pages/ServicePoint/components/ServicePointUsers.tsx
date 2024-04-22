import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import useSnackbar from "@/components/Snackbar/useSnackbar";
import { ServicePoint } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";

import { useAuthHelper } from "@/components/useAuthHelper";

import { Cancel as CancelIcon, Check as CheckIcon } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  Chip,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

type Member = {
  id: string;
  attributes: {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
  };
  roles: string[];
};

const VITE_KEYCLOAK_URL = import.meta.env.VITE_KEYCLOAK_URL as string;
const VITE_KEYCLOAK_REALM = import.meta.env.VITE_KEYCLOAK_REALM as string;
const VITE_KEYCLOAK_CLIENT_ID = import.meta.env
  .VITE_KEYCLOAK_CLIENT_ID as string;

const url = `${VITE_KEYCLOAK_URL}/realms/${VITE_KEYCLOAK_REALM}/group`;

export default function ServicePointUsers({
  servicePoint,
}: {
  servicePoint?: ServicePoint;
}) {
  const { keycloak } = useCustomKeycloak();

  const queryClient = useQueryClient();
  const snackbar = useSnackbar();

  async function fetchServicePointUsers() {
    const response = await fetch(url, {
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
        queryKey: ["servicePointUsers"],
      });
      snackbar.openSnackbar(
        `✅ Success: ${variables.operation} role service-point-user `
      );
    },
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
          <TableContainer
            component={Paper}
            variant="outlined"
            sx={{
              background: "transparent",
            }}
          >
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>First Name</TableCell>
                  <TableCell>Last Name</TableCell>
                  <TableCell>User Name</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Roles</TableCell>
                  <TableCell>Grant?</TableCell>
                  <TableCell>Revoke?</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {query.data.id === servicePoint?.groupId &&
                  query.data.members.map((member: Member) => {
                    return (
                      <TableRow
                        key={member.id}
                        sx={{
                          "&:last-child td, &:last-child th": {
                            border: 0,
                          },
                        }}
                      >
                        <TableCell component="th" scope="row">
                          {member.attributes.firstName}
                        </TableCell>
                        <TableCell component="th" scope="row">
                          {member.attributes.lastName}
                        </TableCell>
                        <TableCell component="th" scope="row">
                          {member.attributes.username}
                        </TableCell>
                        <TableCell component="th" scope="row">
                          {member.attributes.email}
                        </TableCell>
                        <TableCell component="th" scope="row">
                          <Stack direction="column" spacing={1}>
                            {member?.roles?.map((el: string) => (
                              <Chip
                                key={el}
                                variant="outlined"
                                color="success"
                                size="small"
                                label={el}
                              />
                            ))}
                          </Stack>
                        </TableCell>
                        <TableCell component="th" scope="row">
                          <Button
                            size="small"
                            variant="outlined"
                            color="success"
                            startIcon={<CheckIcon />}
                            disabled={member?.roles?.includes(
                              "service-point-user"
                            )}
                            onClick={() => {
                              console.log("query.data", query.data);
                              modifyUserAccessMutation.mutate({
                                userId: member.id,
                                userGroupId: query.data.id,
                                operation: "grant",
                              });
                            }}
                          >
                            Grant
                          </Button>
                        </TableCell>
                        <TableCell component="th" scope="row">
                          <Button
                            size="small"
                            variant="outlined"
                            color="error"
                            startIcon={<CancelIcon />}
                            disabled={
                              !member?.roles?.includes("service-point-user")
                            }
                            onClick={() => {
                              modifyUserAccessMutation.mutate({
                                userId: member.id,
                                userGroupId: query.data.id,
                                operation: "revoke",
                              });
                            }}
                          >
                            Revoke
                          </Button>
                        </TableCell>
                      </TableRow>
                    );
                  })}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>
    </>
  );
}
