import languageSchema from "References/language_schema.json";
import subjectTypeSchema from "References/subject_type_schema.json";
import { z } from "zod";

export const subjectValidationSchema = z.array(
  z.object({
    id: z
      .string()
      .regex(/https:\/\/linked\.data\.gov\.au\/def\/anzsrc-for\/2020\/\d+/),
    schemaUri: z.literal(subjectTypeSchema[0].uri),
    keyword: z.array(
      z.object({
        text: z.string().min(1),
        language: z.object({
          id: z.string().min(1),
          schemaUri: z.literal(languageSchema[0].uri),
        }),
      })
    ),
  })
);
