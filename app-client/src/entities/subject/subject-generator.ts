import { faker } from "@faker-js/faker";

import languageSchema from "References/language_schema.json";

import subjectType from "References/subject_type.json";
import subjectTypeSchema from "References/subject_type_schema.json";

export const subjectGenerator = () => {
  const randomIndex = Math.floor(Math.random() * subjectType.length);
  return {
    id: subjectType[randomIndex].id,
    schemaUri: subjectTypeSchema[0].uri,
    keyword: [
      {
        text: `[G] ${faker.lorem.sentence()}`,
        language: {
          id: "eng",
          schemaUri: languageSchema[0].uri,
        },
      },
      {
        text: `[G] ${faker.lorem.sentence()}`,
        language: {
          id: "deu",
          schemaUri: languageSchema[0].uri,
        },
      },
    ],
  };
};
