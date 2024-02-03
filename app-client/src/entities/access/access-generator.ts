import { faker } from "@faker-js/faker";
import {
  Access,
  AccessStatement,
  AccessType,
  Language,
} from "Generated/Raidv2";
import dayjs from "dayjs";
import accessType from "../../References/access_type.json";
import accessTypeSchema from "../../References/access_type_schema.json";
import languageSchema from "../../References/language_schema.json";

const accessTypeGenerator = (): AccessType => {
  return {
    id: accessType[1].uri,
    schemaUri: accessTypeSchema[0].uri,
  };
};

const accessStatementLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

const accessStatementGenerator = (): AccessStatement => {
  return {
    text: `[G]: ${faker.lorem.sentence()}`,
    language: accessStatementLanguageGenerator(),
  };
};

export const accessGenerator = (): Access => {
  const threeYearsFromNow = dayjs().add(1, "year").toDate();
  return {
    type: accessTypeGenerator(),
    statement: accessStatementGenerator(),
    embargoExpiry: threeYearsFromNow,
  };
};
