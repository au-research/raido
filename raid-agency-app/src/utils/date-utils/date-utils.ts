import dayjs from "dayjs";

export function parseJwtDate(
  date: string | number | undefined,
): Date | undefined {
  if (!date || isNaN(Number(date))) {
    return undefined;
  }
  return new Date(Number(date) * 1000);
}

export function addDays(d: Date | undefined, days: number): Date {
  const date = d ? new Date(d) : new Date();
  date.setDate(date.getDate() + days);
  return date;
}

export const combinedPattern =
  /^(?:\d{4}-(?:(?:0[13578]|1[02])-(?:0[1-9]|[12]\d|3[01])|(?:0[469]|11)-(?:0[1-9]|[12]\d|30)|02-(?:0[1-9]|1\d|2[0-8]))|\d{4}-(?:0[1-9]|1[0-2])|\d{4})?$/;

export const yearPattern = /^\d{4}$/;
export const yearMonthPattern = /^\d{4}-\d{2}$/;
export const yearMonthDayPattern =
  /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/;

const isYearFormattedString = (input: string): boolean => {
  return yearPattern.test(input);
};

const isYearMonthFormattedString = (input: string): boolean => {
  return yearMonthPattern.test(input);
};

const isYearMonthDayFormattedString = (input: string): boolean => {
  return yearMonthDayPattern.test(input);
};

export const dateDisplayFormatter = (input?: string) => {
  if (!input || input === "" || input === undefined) {
    return "";
  }

  if (isYearFormattedString(input)) {
    return input;
  }

  if (isYearMonthFormattedString(input)) {
    const [year, month] = input.split("-");
    return dayjs(`${year}-${month}-01`).format("MMM-YYYY");
  }

  if (isYearMonthDayFormattedString(input)) {
    const [year, month, day] = input.split("-");
    return dayjs(`${year}-${month}-${day}`).format("DD-MMM-YYYY");
  }

  return "";
};

export const dateHelperTextRequired = "YYYY or YYYY-MM or YYYY-MM-DD";
export const dateHelperText = "YYYY or YYYY-MM or YYYY-MM-DD or empty";
