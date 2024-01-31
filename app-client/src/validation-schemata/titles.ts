import {z} from "zod";
import titleType from "../References/title_type.json";
import titleTypeSchema from "../References/title_type_schema.json";
import languageSchema from "../References/language_schema.json";

import {combinedPattern} from "../Util/DateUtil";

const nonEmptyString = z.string().min(1);
const regexString = z.string().regex(combinedPattern).min(1);

const titleTypeEnum = z.enum(titleType.map((type) => type.uri) as [string, ...string[]]);
const typeSchema = z.object({
    id: titleTypeEnum,
    schemaUri: z.literal(titleTypeSchema[0].uri),
});

const languageZodSchema = z.object({
    id: nonEmptyString,
    schemaUri: z.literal(languageSchema[0].uri),
});

export const itemSchema = z.object({
    text: nonEmptyString,
    type: typeSchema,
    language: languageZodSchema,
    startDate: regexString,
    endDate: regexString.optional(),
});

export const titlesValidationSchema  = z.array(itemSchema).min(1)