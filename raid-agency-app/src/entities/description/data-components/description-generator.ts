import { Description, DescriptionType, Language } from "@/generated/raid";

import descriptionTypeSchema from "@/references/description_type_schema.json";

import languageSchema from "@/references/language_schema.json";

const primaryDescriptionType =
  "https://vocabulary.raid.org/description.type.schema/318";

const descriptionTypeGenerator = (): DescriptionType => {
  return {
    id: primaryDescriptionType,
    schemaUri: descriptionTypeSchema[0].uri,
  };
};

const descriptionLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

export const descriptionGenerator = (): Description => {
  return {
    text: `Description text`,
    type: descriptionTypeGenerator(),
    language: descriptionLanguageGenerator(),
  };
};
