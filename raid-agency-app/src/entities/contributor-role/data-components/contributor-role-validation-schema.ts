import contributorRoleSchema from "@/references/contributor_role_schema.json";
import { z } from "zod";

export const contributorRoleValidationSchema = z.array(
  z.object({
    id: z.string(),
    schemaUri: z.literal(contributorRoleSchema[0].uri),
  })
);
