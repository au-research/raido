import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";

export function useAuthHelper() {
  const { keycloak } = useCustomKeycloak();

  const hasServicePointGroup =
    keycloak.tokenParsed?.service_point_group_id !== undefined;

  const isServicePointUser = keycloak.hasRealmRole("service-point-user");

  const isGroupAdmin = keycloak.hasRealmRole("group-admin");
  const isOperator = keycloak.hasRealmRole("operator");
  const groupId = keycloak.tokenParsed?.service_point_group_id;

  return {
    hasServicePointGroup,
    isServicePointUser,
    isGroupAdmin,
    isOperator,
    groupId,
  };
}
