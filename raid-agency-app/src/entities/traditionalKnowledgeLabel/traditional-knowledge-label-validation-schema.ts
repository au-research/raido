import { z } from "zod";

export const traditionalKnowledgeLabelValidationSchema = z
  .array(
    z.object({
      id: z.string().min(1),
      schemaUri: z.string().min(1),
    })
  )
  .optional();
