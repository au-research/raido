import { faker } from "@faker-js/faker";
import { RaidCreateRequest } from "Generated/Raidv2";
import dayjs, { Dayjs } from "dayjs";
import { z } from "zod";

export const extractKeyFromIdUri = (inputUri: string): string | null => {
  let result = null;
  const regex = /\/([^/]+)\.json$/;
  const match = inputUri.match(regex);
  if (match && match[1]) {
    return match[1];
  }
  return null;
};

/**
 * Calculates the date that is three years from the given input date.
 * If no date is provided, it will use the current date.
 *
 * @function
 * @param {string} [inputDate=dayjs().format()] - The starting date in string format.
 * @returns {Dayjs} The date that is three years from the provided or current date.
 * @example
 *   // If today is '2023-09-10'
 *   threeYearsFromDate(); // returns '2026-09-10'
 *   threeYearsFromDate('2020-01-01'); // returns '2023-01-01'
 */
export const threeYearsFromDate = (
  inputDate: string = dayjs().format()
): Dayjs => {
  return dayjs(inputDate).add(3, "year");
};

export const raidColors = new Map([
  ["blue", "#00B0D5"],
  ["pink", "#E51875"],
  ["yellow", "#F8B20E"],
  ["purple", "#8E489B"],
]);

export const FormSchema = z.object({
  titles: z
    .array(
      z.object({
        title: z.string().nonempty(),
        type: z.object({
          id: z.string(),
          schemeUri: z.literal(
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/"
          ),
        }),
        language: z.object({
          id: z.string(),
          schemeUri: z.literal("https://www.iso.org/standard/39534.html"),
        }),
        startDate: z.date(),
        endDate: z.date().optional(),
      })
    )
    .min(1),
});

export const newRaid: RaidCreateRequest = {
  title: [
    {
      text: `Generated: ${faker.lorem.words(5)}`,
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json",
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://www.iso.org/standard/39534.html",
      },
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    },
  ],
  date: {
    startDate: dayjs(new Date()).format("YYYY-MM-DD"),
  },
  access: {
    type: {
      id: "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json",
      schemaUri:
        "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/",
    },
    accessStatement: {
      text: "This is a test statement",
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org/",
      },
    },
  },

  contributor: [
    {
      id: "https://orcid.org/0009-0000-9306-3120",
      schemaUri: "https://orcid.org/",
      position: [
        {
          schemaUri:
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
          id: "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json",
          startDate: dayjs(new Date()).format("YYYY-MM-DD"),
        },
      ],
      role: [
        {
          schemaUri: "https://credit.niso.org/contributor-roles/",
          id: "https://credit.niso.org/contributor-roles/supervision/",
        },
      ],
    },
  ],
};
