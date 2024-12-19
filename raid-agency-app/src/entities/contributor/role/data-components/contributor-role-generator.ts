import contributorRole from "@/references/contributor_role.json";
import contributorRoleSchema from "@/references/contributor_role_schema.json";
import { ContributorRole } from "@/generated/raid";

const contributorRoleGenerator = (): ContributorRole => {
  return {
    schemaUri: contributorRoleSchema[0].uri,
    id: contributorRole[Math.floor(Math.random() * contributorRole.length)].uri,
  };
};
export default contributorRoleGenerator;
