import { contributorPositionValidationSchema } from "@/entities/contributor/position/data-components/contributor-position-validation-schema";
import { contributorRoleValidationSchema } from "@/entities/contributor/role/data-components/contributor-role-validation-schema";
import { z } from "zod";

const baseContributorSchema = z.object({
  leader: z.boolean(),
  contact: z.boolean(),
  schemaUri: z.literal("https://orcid.org/"),
  position: contributorPositionValidationSchema,
  role: contributorRoleValidationSchema,
});

export const singleContributorValidationSchema = z.union([
  baseContributorSchema
    .extend({
      id: z
        .string()
        .regex(
          new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$"),
          {
            message:
              "Invalid ORCID ID, must be full url, e.g. https://orcid.org/0000-0000-0000-0000",
          }
        ),
    })
    .strict(),
  baseContributorSchema
    .extend({
      uuid: z.string(),
    })
    .strict(),
  baseContributorSchema
    .extend({
      email: z.string().email(),
    })
    .strict(),
]);

export const contributorValidationSchema = z.array(
  singleContributorValidationSchema
);
