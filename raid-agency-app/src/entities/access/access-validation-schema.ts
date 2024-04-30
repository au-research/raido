import accessType from "@/references/access_type.json";
import accessTypeSchema from "@/references/access_type_schema.json";
import languageSchema from "@/references/language_schema.json";
import { z } from "zod";

const accessTypeValidationSchema = z.object({
  id: z.enum(accessType.map((type) => type.uri) as [string, ...string[]]),
  schemaUri: z.enum(
    accessTypeSchema.map((el) => el.uri) as [string, ...string[]]
  ),
});

const accessStatementLanguageValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.enum(
    languageSchema.map((el) => el.uri) as [string, ...string[]]
  ),
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
