import {
  Add,
  Key,
  People,
  Visibility,
  VisibilityOff,
} from "@mui/icons-material";
import {
  Breadcrumbs,
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
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { ServicePoint } from "Generated/Raidv2";
import { RqQuery } from "Util/ReactQueryUtil";
import { Link, NavLink } from "react-router-dom";

export function ListServicePointPage() {
  const api = useAuthApi();
  const query: RqQuery<ServicePoint[]> = useQuery(
    ["listServicePoint"],
    async () => await api.servicePoint.findAllServicePoints()
  );

  if (query.error) {
    return <CompactErrorPanel error={query.error} />;
  }

  if (query.isLoading) {
    return <Typography>loading...</Typography>;
  }

  if (!query.data) {
    console.log("unexpected state", query);
    return <Typography>unexpected state</Typography>;
  }
  return (
    <Container>
      <Breadcrumbs aria-label="breadcrumb" sx={{ py: 3 }}>
        <Link to="/">Home</Link>
        <Typography color="text.primary">Service points</Typography>
      </Breadcrumbs>
      <Card>
        <CardHeader
          title={"Service points"}
          action={
            <>
              <RefreshIconButton
                refreshing={query.isLoading}
                onClick={() => query.refetch()}
              />
              <Link to="/service-point">
                <Fab color="primary" size="small">
                  <Add />
                </Fab>
              </Link>
            </>
          }
        />
        <CardContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Service point</TableCell>
                  <TableCell align="center">Users</TableCell>
                  <TableCell align="center">API Keys</TableCell>
                  <TableCell align="center">Enabled</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {query.data.map((row) => (
                  <TableRow
                    key={row.id}
                    // don't render a border under last row
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell scope="row">
                      <NavLink to={`/service-point/${row.id}`}>
                        <Button variant="text" sx={{ textTransform: "none" }}>
                          {row.name}
                        </Button>
                      </NavLink>
                    </TableCell>
                    <TableCell align="center">
                      <NavLink to={`/list-app-user/${row.id}`}>
                        <People />
                      </NavLink>
                    </TableCell>
                    <TableCell align="center">
                      <NavLink to={`/list-api-key/${row.id}`}>
                        <Key />
                      </NavLink>
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
