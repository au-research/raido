import {UseFieldArrayReturn} from "react-hook-form";
import {RaidDto} from "../Generated/Raidv2";
import titleType from "../References/title_type.json";
import {faker} from "@faker-js/faker";
import titleTypeSchema from "../References/title_type_schema.json";
import languageSchema from "../References/language_schema.json";
import dayjs from "dayjs";

export const titleGenerator = (
    titlesFieldArray?: UseFieldArrayReturn<RaidDto, "title">
) => {
    const typeId =
        (
            titlesFieldArray?.fields && titlesFieldArray?.fields?.length > 0
            ? titleType.find((el) => el.uri.includes("alternative"))?.uri
            : titleType.find((el) => el.uri.includes("primary"))?.uri
        )
        || "" ;

    const todaysDate = dayjs(new Date()).format("YYYY-MM-DD")

    return {
        text: `[G] ${faker.lorem.sentence()}`,
        type: {
            id: typeId,
            schemaUri: titleTypeSchema[0].uri,
        },
        language: {
            id: "eng",
            schemaUri: languageSchema[0].uri,
        },
        startDate: todaysDate,
        endDate: undefined,
    };
};

