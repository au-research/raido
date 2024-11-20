import organisationRole from "@/references/organisation_role.json";
import organisationRoleSchema from "@/references/organisation_role_schema.json";
import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

export const organisationRoleValidationSchema = z.array(
  z.object({
    id: z.enum(
      organisationRole.map((role) => role.uri) as [string, ...string[]]
    ),
    schemaUri: z.literal(organisationRoleSchema[0].uri),
    startDate: z.string().regex(combinedPattern).min(1),
    endDate: z.string().regex(combinedPattern).optional().nullable(),
  })
);
