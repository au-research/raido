import { faker } from "@faker-js/faker";
import dayjs from "dayjs";
import { UseFieldArrayReturn } from "react-hook-form";
import { Language, RaidDto, Title, TitleType } from "../../Generated/Raidv2";
import languageSchema from "../../References/language_schema.json";
import titleType from "../../References/title_type.json";
import titleTypeSchema from "../../References/title_type_schema.json";

const titleTypeGenerator = (
  titlesFieldArray?: UseFieldArrayReturn<RaidDto, "title">
): TitleType => {
  const typeId =
    (titlesFieldArray?.fields && titlesFieldArray?.fields?.length > 0
      ? titleType.find((el) => el.uri.includes("alternative"))?.uri
      : titleType.find((el) => el.uri.includes("primary"))?.uri) || "";
  return {
    id: typeId,
    schemaUri: titleTypeSchema[0].uri,
  };
};

const titleLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

export const titleGenerator = (
  titlesFieldArray?: UseFieldArrayReturn<RaidDto, "title">
): Title => {
  const todaysDate = dayjs(new Date()).format("YYYY-MM-DD");

  return {
    text: `[G] ${faker.lorem.sentence()}`,
    type: titleTypeGenerator(titlesFieldArray),
    language: titleLanguageGenerator(),
    startDate: todaysDate,
    endDate: undefined,
  };
};
