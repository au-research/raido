import { faker } from "@faker-js/faker";
import { Language, Subject, SubjectKeyword } from "Generated/Raidv2";

import languageSchema from "References/language_schema.json";

import subjectType from "References/subject_type.json";
import subjectTypeSchema from "References/subject_type_schema.json";

const subjectKeywordLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

export const subjectKeywordGenerator = (): SubjectKeyword => {
  return {
    text: `[G] ${faker.lorem.sentence()}`,
    language: subjectKeywordLanguageGenerator(),
  };
};
export const subjectGenerator = (): Subject => {
  const randomIndex = Math.floor(Math.random() * subjectType.length);
  return {
    id: subjectType[randomIndex].id,
    schemaUri: subjectTypeSchema[0].uri,
    keyword: [subjectKeywordGenerator()],
  };
};
