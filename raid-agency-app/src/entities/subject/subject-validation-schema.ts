import languageSchema from "@/references/language_schema.json";
import { z } from "zod";

export const subjectValidationSchema = z
  .array(
    z.object({
      id: z.string().min(1),
      schemaUri: z.string(),
      keyword: z
        .array(
          z.object({
            text: z.string().min(1),
            language: z.object({
              id: z.string().min(1),
              schemaUri: z.literal(languageSchema[0].uri),
            }),
          })
        )
        .optional(),
    })
  )
  .optional();
