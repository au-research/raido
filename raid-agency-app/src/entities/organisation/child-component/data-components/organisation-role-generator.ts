import { OrganisationRole } from "@/generated/raid";
import dayjs from "dayjs";

import organisationRole from "@/references/organisation_role.json";
import organisationRoleSchema from "@/references/organisation_role_schema.json";

export const organisationRoleGenerator = (): OrganisationRole => {
  return {
    id: organisationRole[0].uri,
    schemaUri: organisationRoleSchema[0].uri,
    startDate: dayjs().format("YYYY-MM-DD"),
    endDate: "",
  };
};
