import { faker } from "@faker-js/faker";
import { RaidCreateRequest } from "Generated/Raidv2";
import dayjs, { Dayjs } from "dayjs";
import { z } from "zod";

export const extractKeyFromIdUri = (inputUri: string = ""): string => {
  let result = "";
  const regex = /\/([^/]+)\.json$/;
  const match = inputUri.match(regex);
  if (match && match[1]) {
    result = match[1];
  }
  return result;
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

// "identifier": {
//   "id": "http://localhost:8080/10378.1/1756266",
//   "owner": {
//     "id": "https://ror.org/038sjwq14",
//     "schemaUri": "https://ror.org/",
//     "servicePoint": 20000000
//   },
//   "license": "Creative Commons CC-0",
//   "version": 1,
//   "globalUrl": "https://hdl.handle.net/10378.1/1756266",
//   "schemaUri": "https://raid.org/",
//   "raidAgencyUrl": "http://localhost:8080/10378.1/1756266",
//   "registrationAgency": {
//     "id": "https://ror.org/038sjwq14",
//     "schemaUri": "https://ror.org/"
//   }
// },

export const FormSchema = z.object({
  identifier: z
    .object({
      id: z.string().nonempty(),
      owner: z.object({
        id: z.string().nonempty(),
        schemaUri: z.string().nonempty(),
        servicePoint: z.number().int(),
      }),
      license: z.string().nonempty(),
      version: z.number().int(),
      globalUrl: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
      raidAgencyUrl: z.string().nonempty(),
      registrationAgency: z.object({
        id: z.string().nonempty(),
        schemaUri: z.string().nonempty(),
      }),
    })
    .optional(),
  title: z
    .array(
      z.object({
        text: z.string().nonempty(),
        type: z.object({
          id: z.string(),
          schemaUri: z.literal(
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/"
          ),
        }),
        language: z.object({
          id: z.string().nonempty(),
          schemaUri: z.literal("https://iso639-3.sil.org"),
        }),
        startDate: z.string(),
        endDate: z.string().optional(),
      })
    )
    .min(1),
  description: z.array(
    z.object({
      text: z.string().nonempty(),
      type: z.object({
        id: z.string(),

        schemaUri: z.literal(
          "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/"
        ),
      }),
      language: z.object({
        id: z.string().nonempty(),
        schemaUri: z.literal("https://iso639-3.sil.org"),
      }),
    })
  ),
  date: z.object({
    startDate: z.string().nonempty(),
    endDate: z.string().optional(),
  }),
  access: z.object({
    type: z.object({
      id: z.string(),
      schemaUri: z.literal(
        "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/"
      ),
    }),
    accessStatement: z.object({
      text: z.string().nonempty(),
      language: z.object({
        id: z.string().nonempty(),
        schemaUri: z.literal("https://iso639-3.sil.org"),
      }),
    }),
  }),
  contributor: z.array(
    z.object({
      id: z
        .string()
        .regex(
          new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$")
        ),
      schemaUri: z.literal("https://orcid.org/"),
      position: z.array(
        z.object({
          id: z.string(),
          schemaUri: z.literal(
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/"
          ),
          startDate: z.string(),
        })
      ),
      role: z.array(
        z.object({
          id: z.string(),
          schemaUri: z.literal("https://credit.niso.org/"),
        })
      ),
    })
  ),
});

export const newRaid: RaidCreateRequest = {
  title: [
    {
      text: `[Generated]: ${faker.lorem.words(5)}`,
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json",
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    },
    {
      text: `[Generated]: ${faker.lorem.words(5)}`,
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json",
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    },
  ],
  description: [
    {
      text: `[Generated]: ${faker.lorem.paragraph()}`,
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/alternative.json",
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
    },
    {
      text: `[Generated]: ${faker.lorem.paragraph()}`,
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json",
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
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
      text: `[Generated]: ${faker.lorem.words(5)}`,
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
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
          startDate: "2023-08-24",
        },
      ],
      role: [
        {
          schemaUri: "https://credit.niso.org/",
          id: "https://credit.niso.org/contributor-roles/conceptualization/",
        },
      ],
    },
  ],
};
