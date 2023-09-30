import { faker } from "@faker-js/faker";
import { RaidCreateRequest } from "Generated/Raidv2";
import dayjs, { Dayjs } from "dayjs";
import { z } from "zod";

export const extractPrefixAndSuffixFromIdentifier = (
  identifier: string
): { prefix: string; suffix: string } => {
  const pattern = /\/([^/]+)\/([^/]+)$/;
  const matches = identifier.match(pattern);

  if (matches && matches.length === 3) {
    return {
      prefix: matches[1],
      suffix: matches[2],
    };
  }

  return {
    prefix: "",
    suffix: "",
  };
};

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
  date: z.object({
    startDate: z.string(),
    endDate: z.string().optional(),
  }),
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
  alternateUrl: z.array(
    z.object({
      url: z.string().url().nonempty(),
    })
  ),
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
  organisation: z.array(
    z.object({
      id: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
      role: z.array(
        z.object({
          id: z.string(),
          schemaUri: z.string(),
          startDate: z.string(),
          endDate: z.string().optional(),
        })
      ),
    })
  ),
  subject: z.array(
    z.object({
      id: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
      keyword: z.array(
        z.object({
          text: z.string().nonempty(),
          language: z.object({
            id: z.string().nonempty(),
            schemaUri: z.literal("https://iso639-3.sil.org"),
          }),
        })
      ),
    })
  ),
  relatedRaid: z.array(
    z.object({
      id: z.string().nonempty(),
      type: z.object({
        id: z.string(),
        schemaUri: z.string(),
      }),
    })
  ),
  relatedObject: z.array(
    z.object({
      id: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
      type: z.object({
        id: z.string(),
        schemaUri: z.string(),
      }),
      category: z.object({
        id: z.string(),
        schemaUri: z.string(),
      }),
    })
  ),
  alternateIdentifier: z.array(
    z.object({
      id: z.string().nonempty(),
      type: z.string().nonempty(),
    })
  ),
  spatialCoverage: z.array(
    z.object({
      id: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
      place: z.string().nonempty(),
      language: z.object({
        id: z.string().nonempty(),
        schemaUri: z.literal("https://iso639-3.sil.org"),
      }),
    })
  ),
  traditionalKnowledgeLabel: z.array(
    z.object({
      id: z.string().nonempty(),
      schemaUri: z.string().nonempty(),
    })
  ),
});

export const newRaid: RaidCreateRequest = {
  title: [
    {
      text: `[G]: ${faker.lorem.words(5)}`,
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
      text: `[G]: ${faker.lorem.words(5)}`,
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
      text: `[G]: ${faker.lorem.paragraph()}`,
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
      text: `[G]: ${faker.lorem.paragraph()}`,
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
      text: `[G]: ${faker.lorem.words(5)}`,
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
    },
  },
  organisation: [
    {
      id: "https://ror.org/038sjwq14",
      schemaUri: "https://ror.org/",
      role: [
        {
          id: "https://credit.niso.org/contributor-roles/software/",
          schemaUri: "https://credit.niso.org/contributor-roles/",
          startDate: "2020-01-01",
        },
        {
          id: "https://credit.niso.org/contributor-roles/supervision/",
          schemaUri: "https://credit.niso.org/contributor-roles/",
          startDate: "2022-01-01",
        },
      ],
    },
  ],

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
