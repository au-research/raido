import dayjs, { Dayjs } from "dayjs";

export const extractKeyFromIdUri = (inputUri: string): string | null => {
  let result = null;
  const regex = /\/([^\/]+)\.json$/;
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
