import contributorPositionSchema from "References/contributor_position_schema.json";

import contributorRoleSchema from "References/contributor_role_schema.json";

import { combinedPattern } from "Util/DateUtil";
import { z } from "zod";

export const singleContributorSchema = z.object({
  id: z
    .string()
    .regex(new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$")),
  leader: z.boolean(),
  contact: z.boolean(),
  schemaUri: z.literal("https://orcid.org/"),
  position: z
    .array(
      z.object({
        id: z.string(),
        schemaUri: z.literal(contributorPositionSchema[0].uri),
        startDate: z.string().regex(combinedPattern).min(1),
        endDate: z.string().regex(combinedPattern).optional().nullable(),
      })
    )
    .max(1),
  role: z.array(
    z.object({
      id: z.string(),
      schemaUri: z.literal(contributorRoleSchema[0].uri),
    })
  ),
});

export const contributorValidationSchema = z
  .array(singleContributorSchema)
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
