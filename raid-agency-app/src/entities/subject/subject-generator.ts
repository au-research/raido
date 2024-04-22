import { Language, Subject, SubjectKeyword } from "@/generated/raid";

import languageSchema from "@/references/language_schema.json";

import subjectType from "@/references/subject_type.json";

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
export const subjectGenerator = (): Subject => {
  const randomIndex = Math.floor(Math.random() * subjectType.length);
  return {
    id: subjectType[randomIndex].id,
    schemaUri: `https://vocabs.ardc.edu.au/viewById/316`,
    keyword: [subjectKeywordGenerator()],
  };
};