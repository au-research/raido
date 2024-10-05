import { Organisation } from "@/generated/raid";
import { organisationRoleGenerator } from "@/entities/organisation/child-component/data-components/organisation-role-generator";
import organisations from "@/references/organisation.json";
import organisationSchemas from "@/references/organisation_schema.json";

interface OrganisationGeneratorOptions {
  alternativeId?: string;
  numRoles?: number;
}

export const organisationGenerator = ({
  alternativeId,
  numRoles = 1,
}: OrganisationGeneratorOptions = {}): Organisation => {
  if (organisations.length === 0 || organisationSchemas.length === 0) {
    throw new Error("Organisation or schema data is empty");
  }

  return {
    id: alternativeId ?? organisations[0].pid,
    schemaUri: organisationSchemas[0].uri,
    role: Array.from({ length: numRoles }, () => organisationRoleGenerator()),
  };
};
