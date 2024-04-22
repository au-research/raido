import languageSchema from "@/references/language_schema.json";
import subjectType from "@/references/subject_type.json";
import { z } from "zod";

export const subjectValidationSchema = z.array(
  z.object({
    id: z.enum(
      subjectType.map((el) =>
        el.id.replace("https://linked.data.gov.au/def/anzsrc-for/2020/", "")
      ) as [string, ...string[]]
    ),
    schemaUri: z.string(),
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
).optional();
