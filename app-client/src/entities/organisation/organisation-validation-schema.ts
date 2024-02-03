import organisationRole from "References/organisation_role.json";
import organisationRoleSchema from "References/organisation_role_schema.json";

import { combinedPattern } from "Util/DateUtil";
import { z } from "zod";

const organisationRoleValidationSchema = z
  .array(
    z.object({
      id: z.enum(
        organisationRole.map((role) => role.uri) as [string, ...string[]]
      ),
      schemaUri: z.literal(organisationRoleSchema[0].uri),
      startDate: z.string().regex(combinedPattern).min(1),
      endDate: z.string().regex(combinedPattern).optional().nullable(),
    })
  )
  .max(1);

export const organisationValidationSchema = z.array(
  z.object({
    id: z.string().min(1),
    schemaUri: z.string().min(1),
    role: organisationRoleValidationSchema,
  })
);
