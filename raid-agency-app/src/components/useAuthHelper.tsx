import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";

const REALM_ROLES = {
  SERVICE_POINT_USER: "service-point-user",
  GROUP_ADMIN: "group-admin",
  OPERATOR: "operator",
} as const;

export function useAuthHelper() {
  const { keycloak } = useCustomKeycloak();
  const { tokenParsed } = keycloak;

  return {
    hasServicePointGroup: tokenParsed?.service_point_group_id != null,
    isServicePointUser: keycloak.hasRealmRole(REALM_ROLES.SERVICE_POINT_USER),
    isGroupAdmin: keycloak.hasRealmRole(REALM_ROLES.GROUP_ADMIN),
    isOperator: keycloak.hasRealmRole(REALM_ROLES.OPERATOR),
    groupId: tokenParsed?.service_point_group_id,
  };
}
