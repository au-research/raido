import { Visibility, VisibilityOff } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { NavLink, useParams } from "react-router-dom";

export function ListAppUserPage() {
  const { servicePointId } = useParams() as { servicePointId: string };
  const api = useAuthApi();

  const usersQuery = useQuery(
    ["listAppUser", servicePointId],
    async () =>
      await api.admin.listAppUser({ servicePointId: parseInt(servicePointId) })
  );
  const servicePointQuery = useQuery(
    ["readServicePoint", servicePointId],
    async () =>
      await api.servicePoint.findServicePointById({
        id: parseInt(servicePointId),
      })
  );

  if (usersQuery.error) {
    return <CompactErrorPanel error={usersQuery.error} />;
  }

  if (usersQuery.isLoading) {
    return <p>loading...</p>;
  }

  if (!usersQuery.data) {
    console.log("unexpected state", usersQuery);
    return <Typography>unexpected state</Typography>;
  }
  return (
    <Container>
      <Card>
        <CardHeader
          title={`${servicePointQuery.data?.name ?? "..."} - Users`}
          action={
            <>
              <RefreshIconButton
                refreshing={usersQuery.isLoading}
                onClick={() => usersQuery.refetch()}
              />
            </>
          }
        />
        <CardContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Identity</TableCell>
                  <TableCell>ID Provider</TableCell>
                  <TableCell align="center">Enabled</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {usersQuery.data.map((row) => (
                  <TableRow
                    key={row.id}
                    // don't render a border under last row
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell>
                      <NavLink to={`/app-user/${row.id}`}>
                        <Button variant="text" sx={{ textTransform: "none" }}>
                          {row.email}
                        </Button>
                      </NavLink>
                    </TableCell>
                    <TableCell>{row.idProvider}</TableCell>
                    <TableCell align="center">
                      {row.enabled ? (
                        <Visibility color={"success"} />
                      ) : (
                        <VisibilityOff color={"error"} />
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>
    </Container>
  );
}
