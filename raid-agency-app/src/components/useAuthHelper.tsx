import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import servicePointsData from "@/data/service-points.json";

export function useAuthHelper() {
  const { keycloak } = useCustomKeycloak();

  const hasServicePointGroup =
    keycloak.tokenParsed?.service_point_group_id !== undefined;

  const isServicePointUser = keycloak.hasRealmRole("service-point-user");

  const isGroupAdmin = keycloak.hasRealmRole("group-admin");
  const isOperator = keycloak.hasRealmRole("operator");
  const groupId = keycloak.tokenParsed?.service_point_group_id;
  const userServicePointId = servicePointsData.find(
    (servicePoint) => servicePoint.groupId === groupId
  )?.id;

  // Return these values from the hook
  return {
    hasServicePointGroup,
    isServicePointUser,
    isGroupAdmin,
    isOperator,
    groupId,
    userServicePointId,
  };
}
