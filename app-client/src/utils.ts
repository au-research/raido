import {
  Access,
  AlternateIdentifier,
  AlternateUrl,
  Contributor,
  Description,
  Id,
  ModelDate,
  Organisation,
  RaidCreateRequest,
  RaidDto,
  RelatedObject,
  RelatedRaid,
  SpatialCoverage,
  Subject,
  Title,
  TraditionalKnowledgeLabel,
} from "Generated/Raidv2";
import dayjs, { Dayjs } from "dayjs";
import { accessGenerator } from "entities/access/access-generator";
import { contributorGenerator } from "entities/contributor/contributor-generator";
import { dateGenerator } from "entities/date/date-generator";
import { titleGenerator } from "entities/title/title-generator";

export const raidRequest = (data: RaidDto): RaidDto => {
  return {
    identifier: data?.identifier || ({} as Id),
    description: data?.description || ([] as Description[]),
    title: data?.title || ([] as Title[]),
    access: data?.access || ({} as Access),
    alternateUrl: data?.alternateUrl || ([] as AlternateUrl[]),
    relatedRaid: data?.relatedRaid || ([] as RelatedRaid[]),
    date: data?.date || ({} as ModelDate),
    contributor: data?.contributor || ([] as Contributor[]),
    alternateIdentifier:
      data?.alternateIdentifier || ([] as AlternateIdentifier[]),
    organisation: data?.organisation || ([] as Organisation[]),
    relatedObject: data?.relatedObject || ([] as RelatedObject[]),
    spatialCoverage: data?.spatialCoverage || ([] as SpatialCoverage[]),
    subject: data?.subject || ([] as Subject[]),
    traditionalKnowledgeLabel:
      data?.traditionalKnowledgeLabel || ([] as TraditionalKnowledgeLabel[]),
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

export const extractLastUrlSegment = (inputUri: string = ""): string => {
  let result = "";
  // Look for a sequence of characters that don't include a slash,
  // located just before a trailing slash (or the end of the string).
  const regex = /\/([^/]+)\/?$/;
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
  ["blue", "#0284c7"],
  ["pink", "#E51875"],
  ["yellow", "#F8B20E"],
  ["purple", "#8E489B"],
]);

export const newRaid: RaidCreateRequest = {
  title: [titleGenerator()],
  // description: [descriptionGenerator()],
  date: dateGenerator(),
  access: accessGenerator(),
  // organisation: [organisationGenerator()],
  contributor: [contributorGenerator()],
  // subject: [subjectGenerator()],
  // relatedRaid: [],
  alternateUrl: [],
  // spatialCoverage: [spatialCoverageGenerator()],
  // relatedObject: [
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  // ],
  // alternateIdentifier: [
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  // ],
};
