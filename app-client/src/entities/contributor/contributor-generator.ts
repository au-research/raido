import contributorPosition from "References/contributor_position.json";
import contributorPositionSchema from "References/contributor_position_schema.json";
import dayjs from "dayjs";

import contributorRole from "References/contributor_role.json";
import contributorRoleSchema from "References/contributor_role_schema.json";

export const contributorGenerator = () => {
  const otherIndex = contributorPosition.findIndex((el) =>
    el.uri.includes("other")
  );
  return {
    id: "https://orcid.org/0009-0000-9306-3120",
    leader: true,
    contact: true,
    schemaUri: "https://orcid.org/",
    position: [
      {
        schemaUri: contributorPositionSchema[0].uri,
        id: contributorPosition[otherIndex].uri,
        startDate: dayjs().format("YYYY-MM-DD"),
      },
    ],
    role: [
      {
        schemaUri: contributorRoleSchema[0].uri,
        id: contributorRole[Math.floor(Math.random() * contributorRole.length)]
          .uri,
      },
    ],
  };
};
