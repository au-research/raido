import raidConfig from "@/../raid.config.json";
import contributorPositionGenerator from "@/entities/contributor/position/data-components/contributor-position-generator";
import contributorRoleGenerator from "@/entities/contributor/role/data-components/contributor-role-generator";
import { Contributor } from "@/generated/raid";

type ContributorExtended = Contributor &
  (
    | { uuid: string; id?: never; email?: never }
    | { id: string; uuid?: never; email?: never }
    | { email: string; uuid?: never; id?: never }
  );

const contributorGenerator = (): ContributorExtended => {
  // Create a base object with all required Contributor properties
  const baseData: Omit<Contributor, "id" | "email" | "uuid"> = {
    leader: true,
    contact: true,
    schemaUri: "https://orcid.org/",
    position: [contributorPositionGenerator()],
    role: [contributorRoleGenerator(), contributorRoleGenerator()],
  };

  if (raidConfig.version === "3") {
    return {
      ...baseData,
      email: "jane.doe@ardc-raid.testinator.com",
    } as unknown as ContributorExtended;
  } else {
    return {
      ...baseData,
      id: "http://orcid.org/0000-0000-0000-0001",
    } as ContributorExtended;
  }
};

export default contributorGenerator;
