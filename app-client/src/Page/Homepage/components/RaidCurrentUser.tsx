import { Box, Card, CardContent, CardHeader, Grid } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "../../../Api/AuthApi";
import { useAuth } from "../../../Auth/AuthProvider";
import { getIdProvider } from "../../../Component/GetIdProvider";
import { getRoleForKey } from "../../../Component/Util";

export default function RaidCurrentUser() {
  const api = useAuthApi();
  const auth = useAuth();
  const { email, role, servicePointId, clientId } = auth.session.payload;

  const spQuery = useQuery(
    ["readServicePoint", servicePointId],
    async () =>
      await api.servicePoint.findServicePointById({
        id: servicePointId,
      })
  );
  return (
    <Card className="raid-card" data-testid="signed-in-user">
      <CardHeader title="Signed-in user" />
      <CardContent>
        <Grid container>
          <Grid item xs={12} sm={6} md={6}>
            <Box>
              <Typography variant="body2">Identity</Typography>
              <Typography color="text.secondary" variant="body1">
                {email}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">ID provider</Typography>
              <Typography color="text.secondary" variant="body1">
                {getIdProvider(clientId)}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Service point</Typography>
              <Typography color="text.secondary" variant="body1">
                {spQuery.data?.name || ""}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Role</Typography>
              <Typography color="text.secondary" variant="body1">
                {getRoleForKey(role)}
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
