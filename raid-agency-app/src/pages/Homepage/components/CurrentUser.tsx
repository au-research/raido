import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { Circle as CircleIcon } from "@mui/icons-material";
import { KeycloakTokenParsed } from "keycloak-js";

const keycloakInternalRoles = [
  "default-roles-raid",
  "offline_access",
  "uma_authorization",
];
function getRolesFromToken({
  tokenParsed,
}: {
  tokenParsed: KeycloakTokenParsed | undefined;
}): string[] | undefined {
  return tokenParsed?.realm_access?.roles.filter(
    (el) => !keycloakInternalRoles.includes(el)
  );
}

export default function CurrentUser() {
  const { keycloak } = useCustomKeycloak();
  const roles = getRolesFromToken({ tokenParsed: keycloak.tokenParsed });
  const clientId = keycloak.tokenParsed?.azp;

  return (
    <Card
      data-testid="signed-in-user"
      sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}
    >
      <CardHeader title="Signed-in user" />
      <CardContent>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4} md={4}>
            <Box>
              <Typography variant="body2">Identity</Typography>
              <Typography color="text.secondary" variant="body1">
                {keycloak.tokenParsed?.sub}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Client</Typography>
              <Typography color="text.secondary" variant="body1">
                {clientId}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={4} md={4}>
            <Box>
              <Typography variant="body2">Service point</Typography>
              <Typography color="text.secondary" variant="body1">
                {keycloak.tokenParsed?.service_point_group_id}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={12} md={12}>
            <Box>
              <Typography variant="body2">Roles</Typography>
              <Stack direction="row" gap={1}>
                {roles?.sort().map((el: string) => (
                  <Chip
                    variant="outlined"
                    color="primary"
                    size="small"
                    icon={<CircleIcon color="success" sx={{ height: 8 }} />}
                    label={el}
                  />
                ))}
              </Stack>
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
