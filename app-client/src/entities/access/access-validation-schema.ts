import { z } from "zod";
import accessType from "../../References/access_type.json";
import accessTypeSchema from "../../References/access_type_schema.json";
import languageSchema from "../../References/language_schema.json";

export const accessValidationSchema = z.object({
  type: z.object({
    id: z.enum(accessType.map((type) => type.uri) as [string, ...string[]]),
    schemaUri: z.literal(accessTypeSchema[0].uri),
  }),
  statement: z.object({
    text: z.string().min(1),
    language: z.object({
      id: z.string().min(1),
      schemaUri: z.literal(languageSchema[0].uri),
    }),
  }),
  embargoExpiry: z.date().optional(),
});
