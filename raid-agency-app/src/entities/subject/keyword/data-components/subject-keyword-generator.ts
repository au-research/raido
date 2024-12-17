import { Language, SubjectKeyword } from "@/generated/raid";
import languageSchema from "@/references/language_schema.json";

const subjectKeywordLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

export const subjectKeywordGenerator = (): SubjectKeyword => {
  return {
    text: `Subject keyword example text...`,
    language: subjectKeywordLanguageGenerator(),
  };
};

export default subjectKeywordGenerator;
