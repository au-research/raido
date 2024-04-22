import { Language, Title, TitleType } from "@/generated/raid";
import dayjs from "dayjs";
import titleTypeSchema from "@/references/title_type_schema.json";
import languageSchema from "@/references/language_schema.json";

const primaryTitle = "https://vocabulary.raid.org/title.type.schema/5";

const titleTypeGenerator = (): TitleType => {
  return {
    id: primaryTitle,
    schemaUri: titleTypeSchema[0].uri,
  };
};

const titleLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

const todaysDateStringGenerator = (): string => {
  return dayjs(new Date()).format("YYYY-MM-DD");
};

export const titleGenerator = (): Title => {
  return {
    text: `Example title... ${new Date().toLocaleTimeString()}`,
    type: titleTypeGenerator(),
    language: titleLanguageGenerator(),
    startDate: todaysDateStringGenerator(),
    endDate: undefined,
  };
};
