import { describe, it, expect } from "vitest";
import { descriptionValidationSchema } from "@/entities/description/data-components/description-validation-schema"; // Replace with the actual file name
import descriptionType from "@/references/description_type.json";
import descriptionTypeSchema from "@/references/description_type_schema.json";
import languageSchema from "@/references/language_schema.json";

describe("Description Validation Schema", () => {
  const validDescription = {
    text: "Sample description",
    type: {
      id: descriptionType[0].uri,
      schemaUri: descriptionTypeSchema[0].uri,
    },
    language: {
      id: "en",
      schemaUri: languageSchema[0].uri,
    },
  };

  it("should validate a correct description object", () => {
    const result = descriptionValidationSchema.safeParse([validDescription]);
    expect(result.success).toBe(true);
  });

  it("should validate multiple correct description objects", () => {
    const result = descriptionValidationSchema.safeParse([
      validDescription,
      validDescription,
    ]);
    expect(result.success).toBe(true);
  });

  it("should reject a description with empty text", () => {
    const invalidDescription = { ...validDescription, text: "" };
    const result = descriptionValidationSchema.safeParse([invalidDescription]);
    expect(result.success).toBe(false);
  });

  it("should reject a description with invalid type id", () => {
    const invalidDescription = {
      ...validDescription,
      type: { ...validDescription.type, id: "invalid-id" },
    };
    const result = descriptionValidationSchema.safeParse([invalidDescription]);
    expect(result.success).toBe(false);
  });

  it("should reject a description with invalid type schemaUri", () => {
    const invalidDescription = {
      ...validDescription,
      type: { ...validDescription.type, schemaUri: "invalid-schema-uri" },
    };
    const result = descriptionValidationSchema.safeParse([invalidDescription]);
    expect(result.success).toBe(false);
  });

  it("should reject a description with empty language id", () => {
    const invalidDescription = {
      ...validDescription,
      language: { ...validDescription.language, id: "" },
    };
    const result = descriptionValidationSchema.safeParse([invalidDescription]);
    expect(result.success).toBe(false);
  });

  it("should reject a description with invalid language schemaUri", () => {
    const invalidDescription = {
      ...validDescription,
      language: {
        ...validDescription.language,
        schemaUri: "invalid-schema-uri",
      },
    };
    const result = descriptionValidationSchema.safeParse([invalidDescription]);
    expect(result.success).toBe(false);
  });
});
