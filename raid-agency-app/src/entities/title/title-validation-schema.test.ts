import { titleValidationSchema } from "@/entities/title/title-validation-schema";
import languageSchema from "@/references/language_schema.json";
import titleType from "@/references/title_type.json";
import titleTypeSchema from "@/references/title_type_schema.json";
import { describe, expect, it } from "vitest";

describe("titleValidationSchema", () => {
  it("should validate a correct title object", () => {
    const validTitle = [
      {
        text: "Sample Title",
        type: {
          id: titleType[0].uri,
          schemaUri: titleTypeSchema[0].uri,
        },
        language: {
          id: "en",
          schemaUri: languageSchema[0].uri,
        },
        startDate: "2023-01-01",
        endDate: "2023-12-31",
      },
    ];

    const result = titleValidationSchema.safeParse(validTitle);
    expect(result.success).toBe(true);
  });

  it("should reject an empty array", () => {
    const emptyArray: never[] = [];
    const result = titleValidationSchema.safeParse(emptyArray);
    expect(result.success).toBe(false);
  });

  it("should reject a title with missing required fields", () => {
    const invalidTitle = [
      {
        text: "Sample Title",
        // Missing type and language
        startDate: "2023-01-01",
      },
    ];

    const result = titleValidationSchema.safeParse(invalidTitle);
    expect(result.success).toBe(false);
  });

  it("should reject a title with invalid type", () => {
    const invalidTitle = [
      {
        text: "Sample Title",
        type: {
          id: "invalid-type",
          schemaUri: titleTypeSchema[0].uri,
        },
        language: {
          id: "en",
          schemaUri: languageSchema[0].uri,
        },
        startDate: "2023-01-01",
      },
    ];

    const result = titleValidationSchema.safeParse(invalidTitle);
    expect(result.success).toBe(false);
  });

  it("should reject a title with invalid date format", () => {
    const invalidTitle = [
      {
        text: "Sample Title",
        type: {
          id: titleType[0].uri,
          schemaUri: titleTypeSchema[0].uri,
        },
        language: {
          id: "en",
          schemaUri: languageSchema[0].uri,
        },
        startDate: "invalid-date",
      },
    ];

    const result = titleValidationSchema.safeParse(invalidTitle);
    expect(result.success).toBe(false);
  });

  it("should accept a title without an end date", () => {
    const validTitle = [
      {
        text: "Sample Title",
        type: {
          id: titleType[0].uri,
          schemaUri: titleTypeSchema[0].uri,
        },
        language: {
          id: "en",
          schemaUri: languageSchema[0].uri,
        },
        startDate: "2023-01-01",
        // No endDate
      },
    ];

    const result = titleValidationSchema.safeParse(validTitle);
    expect(result.success).toBe(true);
  });
});
