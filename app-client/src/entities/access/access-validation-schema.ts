import accessType from "References/access_type.json";
import accessTypeSchema from "References/access_type_schema.json";
import languageSchema from "References/language_schema.json";
import { z } from "zod";

const accessTypeValidationSchema = z.object({
  id: z.enum(accessType.map((type) => type.uri) as [string, ...string[]]),
  schemaUri: z.literal(accessTypeSchema[0].uri),
});

const accessStatementLanguageValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.literal(languageSchema[0].uri),
});

const accessStatementValidationSchema = z.object({
  text: z.string().min(1),
  language: accessStatementLanguageValidationSchema,
});

export const accessValidationSchema = z.object({
  type: accessTypeValidationSchema,
  statement: accessStatementValidationSchema,
  embargoExpiry: z.date().optional(),
});
