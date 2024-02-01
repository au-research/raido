import { Add as AddIcon, Visibility, VisibilityOff } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Fab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { getRoleForKey } from "Component/Util";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { NavLink, useParams } from "react-router-dom";

export function ListApiKeyPage() {
  const theme = useTheme();
  const { servicePointId } = useParams() as { servicePointId: string };
  const api = useAuthApi();
  const apiKeysQuery = useQuery(
    ["listApiKey", servicePointId],
    async () =>
      await api.admin.listApiKey({ servicePointId: parseInt(servicePointId) })
  );
  const servicePointQuery = useQuery(
    ["readServicePoint", servicePointId],
    async () =>
      await api.servicePoint.findServicePointById({
        id: parseInt(servicePointId),
      })
  );

  if (apiKeysQuery.error) {
    return <CompactErrorPanel error={apiKeysQuery.error} />;
  }

  if (apiKeysQuery.isLoading) {
    return <Typography>loading...</Typography>;
  }

  if (!apiKeysQuery.data) {
    console.log("unexpected state", apiKeysQuery);
    return <Typography>unexpected state</Typography>;
  }

  return (
    <Container>
      <Card>
        <CardHeader
          title={`${servicePointQuery.data?.name ?? "..."} - API keys`}
          action={
            <NavLink to={`/create-api-key/${servicePointId}`}>
              <Fab
                size="small"
                sx={{
                  background: theme.palette.primary.main,
                  color: theme.palette.text.primary,
                }}
              >
                <AddIcon />
              </Fab>
            </NavLink>
          }
        />
        <CardContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Subject</TableCell>
                  <TableCell>Role</TableCell>
                  <TableCell>Expires</TableCell>
                  <TableCell align="center">Enabled</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {apiKeysQuery.data.map((row) => (
                  <TableRow
                    key={row.id}
                    // don't render a border under last row
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell>
                      <NavLink to={`/api-key/${row.id}`}>
                        <Button variant="text" sx={{ textTransform: "none" }}>
                          {row.subject}
                        </Button>
                      </NavLink>
                    </TableCell>
                    <TableCell>{getRoleForKey(row.role)}</TableCell>
                    <TableCell>
                      {Intl.DateTimeFormat("en-AU", {
                        dateStyle: "medium",
                        timeStyle: "short",
                        hour12: false,
                      }).format(row.tokenCutoff)}
                    </TableCell>
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
