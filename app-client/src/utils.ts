import { accessGenerateData } from "Forms/RaidForm/components/FormAccessComponent";
import { alternateIdentifierGenerateData } from "Forms/RaidForm/components/FormAlternateIdentifiersComponent";
import { alternateUrlGenerateData } from "Forms/RaidForm/components/FormAlternateUrlsComponent";
import { contributorsGenerateData } from "Forms/RaidForm/components/FormContributorsComponent";

import { datesGenerateData } from "Forms/RaidForm/components/FormDatesComponent";
import { descriptionsGenerateData } from "Forms/RaidForm/components/FormDescriptionsComponent";
import { organisationsGenerateData } from "Forms/RaidForm/components/FormOrganisationsComponent";
import { relatedObjectGenerateData } from "Forms/RaidForm/components/FormRelatedObjectsComponent";
import { relatedRaidGenerateData } from "Forms/RaidForm/components/FormRelatedRaidsComponent";
import { spatialCoverageGenerateData } from "Forms/RaidForm/components/FormSpatialCoveragesComponent";
import { subjectsGenerateData } from "Forms/RaidForm/components/FormSubjectsComponent";
import { titlesGenerateData } from "Forms/RaidForm/components/FormTitlesComponent";
import { RaidCreateRequest } from "Generated/Raidv2";
import dayjs, { Dayjs } from "dayjs";

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
  ["blue", "#00B0D5"],
  ["pink", "#E51875"],
  ["yellow", "#F8B20E"],
  ["purple", "#8E489B"],
]);

export const newRaid: RaidCreateRequest = {
  title: [
    titlesGenerateData(true),
    titlesGenerateData(false),
    titlesGenerateData(false),
  ],
  description: [descriptionsGenerateData()],
  date: datesGenerateData(),
  access: accessGenerateData(),
  organisation: [
    // organisationsGenerateData()
  ],
  contributor: [contributorsGenerateData()],
  subject: [subjectsGenerateData()],
  relatedRaid: [],
  alternateUrl: [
    alternateUrlGenerateData(),
    alternateUrlGenerateData(),
    alternateUrlGenerateData(),
  ],
  spatialCoverage: [
    // spatialCoverageGenerateData()
  ],
  relatedObject: [
    // relatedObjectGenerateData(),
    // relatedObjectGenerateData(),
    // relatedObjectGenerateData(),
  ],
  alternateIdentifier: [
    alternateIdentifierGenerateData(),
    alternateIdentifierGenerateData(),
    alternateIdentifierGenerateData(),
  ],
};
