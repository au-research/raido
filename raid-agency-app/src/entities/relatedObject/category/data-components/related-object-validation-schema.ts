import { z } from "zod";

export const relatedObjectValidationSchema = z.array(
  z.object({
    id: z.string().min(1),
    schemaUri: z.string().min(1),
    type: z.object({
      id: z.string(),
      schemaUri: z.string(),
    }),
    category: z
      .object({
        id: z.string(),
        schemaUri: z.string(),
      })
      .array(),
  }),
);
