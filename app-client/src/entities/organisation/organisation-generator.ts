import dayjs from "dayjs";

import organisationRole from "References/organisation_role.json";
import organisationRoleSchema from "References/organisation_role_schema.json";

export const organisationGenerator = () => {
  return {
    id: "https://ror.org/038sjwq14",
    schemaUri: "https://ror.org/",
    role: [
      {
        id: organisationRole[0].uri,
        schemaUri: organisationRoleSchema[0].uri,
        startDate: dayjs().format("YYYY-MM-DD"),
      },
    ],
  };
};
