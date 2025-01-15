import { useAuthHelper } from "@/keycloak";
import { GroupSelector } from "@/pages/home/components/GroupSelector";
import { RaidTable } from "@/pages/raid-table";
import { fetchCurrentUserKeycloakGroups } from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import { Add as AddIcon } from "@mui/icons-material";
import { Alert, Container, Fab, Stack, Typography } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";

export const Home = () => {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();
  const { keycloak } = useKeycloak();
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
        <Typography
          variant="h4"
          color="primary"
          sx={{ textShadow: "1px 1px 1px rgba(0,0,0,.3)" }}
        >
          Welcome to RAiD
        </Typography>
        <h1 className="hero-text">Welcome to My Website</h1>

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
        {hasServicePointGroup && isServicePointUser && <RaidTable />}
      </Stack>
    </Container>
  );
};
