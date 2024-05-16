import { Organisation, OrganisationRole } from "@/generated/raid";
import dayjs from "dayjs";

import organisationRole from "@/references/organisation_role.json";
import organisationRoleSchema from "@/references/organisation_role_schema.json";

import organisation from "@/references/organisation.json";
import organisationSchema from "@/references/organisation_schema.json";

const organisationRoleGenerator = (): OrganisationRole => {
  return {
    id: organisationRole[0].uri,
    schemaUri: organisationRoleSchema[0].uri,
    startDate: dayjs().format("YYYY-MM-DD"),
  };
};

export const organisationGenerator = ({
  alternativeId,
}: {
  alternativeId?: string;
}): Organisation => {
  return {
    id: alternativeId ? alternativeId : organisation[0].pid,
    schemaUri: organisationSchema[0].uri,
    role: [organisationRoleGenerator()],
  };
};
