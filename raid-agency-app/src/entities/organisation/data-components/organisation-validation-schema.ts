import organisationRoles from "@/references/organisation_role.json";
import organisationRoleSchema from "@/references/organisation_role_schema.json";
import organisationSchemaReference from "@/references/organisation_schema.json";
import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

// Ensure the arrays are not empty
if (
  organisationRoles.length === 0 ||
  organisationRoleSchema.length === 0 ||
  organisationSchemaReference.length === 0
) {
  throw new Error("One or more reference arrays are empty");
}

const organisationRoleValidationSchema = z
  .array(
    z.object({
      id: z.enum(
        organisationRoles.map((role) => role.uri) as [string, ...string[]]
      ),
      schemaUri: z.literal(organisationRoleSchema[0].uri),
      startDate: z.string().regex(combinedPattern, {
        message: "Invalid date format. Please use ISO 8601 format.",
      }),
      endDate: z
        .string()
        .regex(combinedPattern, {
          message: "Invalid date format. Please use ISO 8601 format.",
        })
        .optional()
        .nullable(),
    })
  )
  .nonempty({
    message: "At least one organisation role is required",
  });

export const organisationValidationSchema = z
  .array(
    z.object({
      id: z.string().min(1, {
        message: "Organisation ID is required",
      } ),
      schemaUri: z.literal(organisationSchemaReference[0].uri),
      role: organisationRoleValidationSchema,
    })
  )
  .nonempty({
    message: "At least one organisation is required",
  });
