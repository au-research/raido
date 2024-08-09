import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { fetchCurrentUserKeycloakGroups } from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import { Typography } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import ServicePointSwitcher from "./ServicePointSwitcher";

export default function WelcomeBox() {
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
    <>
      <Typography variant="h4" color="primary.main">
        Welcome to RAiD
      </Typography>

      {activeGroup && (
        <ServicePointSwitcher
          currentServicePoint={activeGroup}
          showLabel={true}
        />
      )}
    </>
  );
}
