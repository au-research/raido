import contributorPosition from "References/contributor_position.json";
import contributorPositionSchema from "References/contributor_position_schema.json";

import contributorRole from "References/contributor_role.json";
import contributorRoleSchema from "References/contributor_role_schema.json";
import dayjs from "dayjs";

import {
  Contributor,
  ContributorPosition,
  ContributorRole,
} from "Generated/Raidv2";

const contributorPositionGenerator = (): ContributorPosition => {
  const otherIndex = contributorPosition.findIndex((el) =>
    el.uri.includes("other")
  );

  return {
    schemaUri: contributorPositionSchema[0].uri,
    id: contributorPosition[otherIndex].uri,
    startDate: dayjs().format("YYYY-MM-DD"),
  };
};

const contributorRoleGenerator = (): ContributorRole => {
  return {
    schemaUri: contributorRoleSchema[0].uri,
    id: contributorRole[Math.floor(Math.random() * contributorRole.length)].uri,
  };
};

export const contributorGenerator = (): Contributor => {
  const otherIndex = contributorPosition.findIndex((el) =>
    el.uri.includes("other")
  );
  return {
    id: "https://orcid.org/0009-0000-9306-3120",
    leader: true,
    contact: true,
    schemaUri: "https://orcid.org/",
    position: [contributorPositionGenerator()],
    role: [contributorRoleGenerator()],
  };
};
