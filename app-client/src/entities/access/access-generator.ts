import { faker } from "@faker-js/faker";
import dayjs from "dayjs";
import accessType from "../../References/access_type.json";
import accessTypeSchema from "../../References/access_type_schema.json";
import languageSchema from "../../References/language_schema.json";

export const accessGenerator = () => {
  return {
    type: {
      id: accessType[1].uri,
      schemaUri: accessTypeSchema[0].uri,
    },
    statement: {
      text: `[G]: ${faker.lorem.sentence()}`,
      language: {
        id: "eng",
        schemaUri: languageSchema[0].uri,
      },
    },
    embargoExpiry: dayjs().add(180, "day").toDate(),
  };
};
