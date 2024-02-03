import languageSchema from "References/language_schema.json";
import titleType from "References/title_type.json";
import titleTypeSchema from "References/title_type_schema.json";
import { combinedPattern } from "Util/DateUtil";
import { z } from "zod";

const titleTypeValidationSchema = z.object({
  id: z.enum(titleType.map((type) => type.uri) as [string, ...string[]]),
  schemaUri: z.literal(titleTypeSchema[0].uri),
});

const titleLanguageValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.literal(languageSchema[0].uri),
});

export const titleValidationSchema = z
  .array(
    z.object({
      text: z.string().min(1),
      type: titleTypeValidationSchema,
      language: titleLanguageValidationSchema,
      startDate: z.string().regex(combinedPattern).min(1),
      endDate: z.string().regex(combinedPattern).min(1).optional(),
    })
  )
  .min(1);
