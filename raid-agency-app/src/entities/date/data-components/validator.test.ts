import { describe, expect, it } from "vitest";
import validator from "./validator";

describe("validator", () => {
  it("should validate correct date formats", () => {
    const validInputs = [
      { startDate: "2023" },
      { startDate: "2023-01" },
      { startDate: "2023-01-01" },
      { startDate: "2023", endDate: "2024" },
      { startDate: "2023-01", endDate: "2023-02" },
      { startDate: "2023-01-01", endDate: "2023-01-02" },
    ];

    validInputs.forEach((input) => {
      expect(() => validator.parse(input)).not.toThrow();
    });
  });

  it("should reject invalid date formats", () => {
    const invalidInputs = [
      { startDate: "20" },
      { startDate: "2023-1" },
      { startDate: "2023-01-1" },
      { startDate: "2023/01/01" },
      { startDate: "invalid" },
    ];

    invalidInputs.forEach((input) => {
      expect(() => validator.parse(input)).toThrow();
    });
  });

  it("should reject when end date is before start date", () => {
    const invalidRanges = [
      { startDate: "2023", endDate: "2022" },
      { startDate: "2023-02", endDate: "2023-01" },
      { startDate: "2023-01-02", endDate: "2023-01-01" },
    ];

    invalidRanges.forEach((input) => {
      expect(() => validator.parse(input)).toThrow();
    });
  });

  it("should allow equal start and end dates", () => {
    const equalDates = [
      { startDate: "2023", endDate: "2023" },
      { startDate: "2023-01", endDate: "2023-01" },
      { startDate: "2023-01-01", endDate: "2023-01-01" },
    ];

    equalDates.forEach((input) => {
      expect(() => validator.parse(input)).not.toThrow();
    });
  });

  it("should allow missing end date", () => {
    const input = { startDate: "2023-01-01" };
    expect(() => validator.parse(input)).not.toThrow();
  });
});
