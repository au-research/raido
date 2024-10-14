import { contributorPositionValidationSchema } from "@/entities/contributor-position/data-components/contributor-position-validation-schema";
import { contributorRoleValidationSchema } from "@/entities/contributor-role/data-components/contributor-role-validation-schema";
import { s } from "node_modules/vite/dist/node/types.d-aGj9QkWt";
import { z } from "zod";

export const singleContributorValidationSchema = z.object({
  id: z.string().optional(),
  status: z.string().optional(),
  email: z.string().optional(),
  uuid: z.string().optional(),
  leader: z.boolean(),
  contact: z.boolean(),
  schemaUri: z.literal("https://orcid.org/"),
  position: contributorPositionValidationSchema,
  role: contributorRoleValidationSchema,
});

export const contributorValidationSchema = z.array(
  singleContributorValidationSchema
);
