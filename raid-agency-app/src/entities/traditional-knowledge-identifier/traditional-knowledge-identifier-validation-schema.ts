import { z } from "zod";

export const traditionalKnowledgeIdentifiersValidationSchema = z
  .array(
    z.object({
      id: z.string().min(1),
      schemaUri: z.string().min(1),
    }),
  )
  .optional();
