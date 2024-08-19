import { contributorPositionGenerator } from "@/entities/contributor-position/data-components/contributor-position-generator";
import { contributorRoleGenerator } from "@/entities/contributor-role/data-components/contributor-role-generator";
import { Contributor } from "@/generated/raid";

export const contributorGenerator = (): Contributor => {
  return {
    id: "https://orcid.org/0000-0000-0000-0001",
    leader: true,
    contact: true,
    schemaUri: "https://orcid.org/",
    position: [contributorPositionGenerator()],
    role: [contributorRoleGenerator(), contributorRoleGenerator()],
  };
};
