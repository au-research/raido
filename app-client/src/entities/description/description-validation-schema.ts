import { z } from "zod";
import descriptionType from "../../References/description_type.json";
import descriptionTypeSchema from "../../References/description_type_schema.json";
import languageSchema from "../../References/language_schema.json";

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
