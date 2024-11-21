import ServicePointSwitcher from "@/components/ServicePointSwitcher";
import { useAuthHelper } from "@/components/useAuthHelper";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import RaidTablePage from "@/pages/RaidTablePage";
import { fetchCurrentUserKeycloakGroups } from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import { Add as AddIcon } from "@mui/icons-material";
import { Alert, Container, Fab, Paper, Stack } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import GroupSelector from "@/components/GroupSelector";

export default function HomePage() {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  const { keycloak } = useCustomKeycloak();

  const keycloakGroupsQuery = useQuery<KeycloakGroup[]>({
    queryKey: ["keycloak-groups"],
    queryFn: async () => {
      const servicePoints = await fetchCurrentUserKeycloakGroups({
        token: keycloak.token!,
      });
      return servicePoints;
    },
  });

  if (keycloakGroupsQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (keycloakGroupsQuery.isError) {
    return <div>Error...</div>;
  }

  const activeGroup = keycloakGroupsQuery.data?.find(
    (el) => el.id === keycloak.tokenParsed?.service_point_group_id
  );

  return (
    <Container>
      <Stack gap={2}>
        {hasServicePointGroup && isServicePointUser && (
          <Fab
            variant="extended"
            component={Link}
            color="primary"
            sx={{ position: "fixed", bottom: "32px", right: "32px" }}
            type="button"
            to="/raids/new"
            data-testid="mint-raid-button"
          >
            <AddIcon sx={{ mr: 1 }} />
            Mint new RAiD
          </Fab>
        )}
        {/* {hasServicePointGroup && <CurrentUser />} */}
        {hasServicePointGroup && !isServicePointUser && (
          <Alert severity="error">
            You successfully logged in, but the admin of the service point group
            has not granted you access yet.
          </Alert>
        )}
        {!hasServicePointGroup && <GroupSelector />}
        {hasServicePointGroup && isServicePointUser && (
          <Stack gap={2}>
            <Paper>{activeGroup && <ServicePointSwitcher />}</Paper>

            <RaidTablePage />
          </Stack>
        )}
      </Stack>
    </Container>
  );
}
