import {accessGenerateData} from "Forms/RaidForm/components/FormAccessComponent";
import {contributorsGenerateData} from "Forms/RaidForm/components/FormContributorsComponent";

import {datesGenerateData} from "Forms/RaidForm/components/FormDatesComponent";
import {titlesGenerateData} from "Forms/RaidForm/components/FormTitlesComponent";
import {
  Access, AlternateIdentifier,
  AlternateUrl, Contributor,
  Description,
  Id,
  ModelDate, Organisation,
  RaidCreateRequest,
  RaidDto, RelatedObject,
  RelatedRaid, SpatialCoverage, Subject,
  Title, TraditionalKnowledgeLabel
} from "Generated/Raidv2";
import dayjs, {Dayjs} from "dayjs";

export const raidRequest = (data: RaidDto) => {
  return {
    identifier: data?.identifier || ({} as Id),
    description: data?.description || ([] as Description[]),
    title: data?.title || ([] as Title[]),
    access: data?.access || ({} as Access),
    alternateUrl: data?.alternateUrl || ({} as AlternateUrl[]),
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
        data?.traditionalKnowledgeLabel ||
        ([] as TraditionalKnowledgeLabel[])
  }
}

export const extractPrefixAndSuffixFromIdentifier = (
  identifier: string,
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

// Expected output: "formal-analysis"

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
  inputDate: string = dayjs().format(),
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
  title: [titlesGenerateData()],
  // description: [descriptionsGenerateData()],
  date: datesGenerateData(),
  access: accessGenerateData(),
  // organisation: [organisationsGenerateData()],
  contributor: [contributorsGenerateData()],
  // subject: [subjectsGenerateData()],
  // relatedRaid: [],
  // alternateUrl: [
  //   alternateUrlGenerateData(),
  //   alternateUrlGenerateData(),
  //   alternateUrlGenerateData(),
  // ],
  // spatialCoverage: [spatialCoverageGenerateData()],
  // relatedObject: [
  //   relatedObjectGenerateData(),
  //   relatedObjectGenerateData(),
  //   relatedObjectGenerateData(),
  // ],
  // alternateIdentifier: [
  //   alternateIdentifierGenerateData(),
  //   alternateIdentifierGenerateData(),
  //   alternateIdentifierGenerateData(),
  // ],
};
