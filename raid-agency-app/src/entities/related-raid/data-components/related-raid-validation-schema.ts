import { z } from "zod";

export const relatedRaidValidationSchema = z.array(
  z.object({
    id: z.string().min(1),
    type: z.object({
      id: z.string(),
      schemaUri: z.string(),
    }),
  }),
);
