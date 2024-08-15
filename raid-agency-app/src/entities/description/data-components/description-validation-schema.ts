import descriptionType from "@/references/description_type.json";
import descriptionTypeSchema from "@/references/description_type_schema.json";
import languageSchema from "@/references/language_schema.json";
import { z } from "zod";

const descriptionTypeValidationSchema = z.object({
  id: z.enum(descriptionType.map((type) => type.uri) as [string, ...string[]]),
  schemaUri: z.literal(descriptionTypeSchema[0].uri),
});

const descriptionLanguageValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.literal(languageSchema[0].uri),
});

export const descriptionValidationSchema = z.array(
  z.object({
    text: z.string().min(1),
    type: descriptionTypeValidationSchema,
    language: descriptionLanguageValidationSchema,
  })
);
