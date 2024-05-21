import { dateValidationSchema } from "@/entities/date/date-validation-schema";
import { describe, test, expect } from "vitest";

describe("dateValidationSchema", () => {
  const validStartDate = "2023-05-20";
  const validEndDate = "2023-05-21";
  const invalidEndDate = "2023-05-19";
  const validYearFormat = "2023";
  const validYearMonthFormat = "2023-05";
  const invalidYearFormat = "202";
  const invalidMonthFormat = "2023-13";

  test("validates successfully with valid startDate and endDate", () => {
    const data = { startDate: validStartDate, endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).not.toThrow();
  });

  test("validates successfully with only startDate", () => {
    const data = { startDate: validStartDate };
    expect(() => dateValidationSchema.parse(data)).not.toThrow();
  });

  test("fails validation when endDate is before startDate", () => {
    const data = { startDate: validStartDate, endDate: invalidEndDate };
    expect(() => dateValidationSchema.parse(data)).toThrow(
      expect.objectContaining({
        issues: expect.arrayContaining([
          expect.objectContaining({
            message: "startDate must be before endDate",
          }),
        ]),
      })
    );
  });

  test("fails validation when startDate is not in the correct format", () => {
    const data = { startDate: "invalid-date", endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });

  test("fails validation when endDate is not in the correct format", () => {
    const data = { startDate: validStartDate, endDate: "invalid-date" };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });

  test("validates successfully with startDate in YYYY format", () => {
    const data = { startDate: validYearFormat, endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).not.toThrow();
  });

  test("validates successfully with startDate in YYYY-MM format", () => {
    const data = { startDate: validYearMonthFormat, endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).not.toThrow();
  });

  test("validates successfully with startDate in YYYY and endDate in YYYY-MM format", () => {
    const data = { startDate: validYearFormat, endDate: validYearMonthFormat };
    expect(() => dateValidationSchema.parse(data)).not.toThrow();
  });

  test("fails validation when endDate in YYYY format is before startDate in YYYY-MM format", () => {
    const data = { startDate: validYearMonthFormat, endDate: validYearFormat };
    expect(() => dateValidationSchema.parse(data)).toThrow(
      expect.objectContaining({
        issues: expect.arrayContaining([
          expect.objectContaining({
            message: "startDate must be before endDate",
          }),
        ]),
      })
    );
  });

  test("fails validation when startDate is in invalid YYYY format", () => {
    const data = { startDate: invalidYearFormat, endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });

  test("fails validation when startDate is in invalid YYYY-MM format", () => {
    const data = { startDate: invalidMonthFormat, endDate: validEndDate };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });

  test("fails validation when endDate is in invalid YYYY format", () => {
    const data = { startDate: validStartDate, endDate: invalidYearFormat };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });

  test("fails validation when endDate is in invalid YYYY-MM format", () => {
    const data = { startDate: validStartDate, endDate: invalidMonthFormat };
    expect(() => dateValidationSchema.parse(data)).toThrow();
  });
});
