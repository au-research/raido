import { Language, Title, TitleType } from "@/generated/raid";
import languageSchema from "@/references/language_schema.json";
import titleTypeReference from "@/references/title_type.json";
import titleTypeSchemaReference from "@/references/title_type_schema.json";
import dayjs from "dayjs";

const PRIMARY_TITLE_URI = "https://vocabulary.raid.org/title.type.schema/5";

const primaryTitleReferenceUri = titleTypeReference.find(
  (el) => el.uri === PRIMARY_TITLE_URI
)?.uri;

const titleTypeGenerator = (): TitleType => {
  return {
    id: primaryTitleReferenceUri,
    schemaUri: titleTypeSchemaReference[0].uri,
  };
};

const titleLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

const titleGenerator = (): Title => {
  return {
    text: `Example title... ${new Date().toLocaleTimeString()}`,
    type: titleTypeGenerator(),
    language: titleLanguageGenerator(),
    startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    endDate: dayjs(new Date()).add(1, "year").format("YYYY-MM-DD"),
  };
};

export default titleGenerator;
