import contributorRoleSchema from "@/references/contributor_role_schema.json";

import contributorPositionSchema from "@/references/contributor_position_schema.json";

import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

const contributorPositionValidationSchema = z
  .array(
    z.object({
      id: z.string(),
      schemaUri: z.literal(contributorPositionSchema[0].uri),
      startDate: z.string().regex(combinedPattern).min(1),
      endDate: z.string().regex(combinedPattern).optional().nullable(),
    })
  )
  .max(1);

const contributorRoleValidationSchema = z.array(
  z.object({
    id: z.string(),
    schemaUri: z.literal(contributorRoleSchema[0].uri),
  })
);

export const singleContributorValidationSchema = z.object({
  id: z
    .string()
    .regex(
      new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$"),
      {
        message:
          "Invalid ORCID ID, must be full url, e.g. https://orcid.org/0000-0000-0000-0000",
      }
    ),
  leader: z.boolean(),
  contact: z.boolean(),
  schemaUri: z.literal("https://orcid.org/"),
  position: contributorPositionValidationSchema,
  role: contributorRoleValidationSchema,
});

export const contributorValidationSchema = z
  .array(singleContributorValidationSchema)
  .refine(
    (data) => {
      const hasLeader = data.some((contributor) => contributor.leader);
      const hasContact = data.some((contributor) => contributor.contact);
      return hasLeader && hasContact;
    },
    {
      message:
        "There must be at least one leader and one contact in the contributors.",
    }
  );
