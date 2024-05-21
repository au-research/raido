import { removeNumberInBrackets } from "@/utils/string-utils/string-utils";
import { describe, expect, test } from "vitest";

describe("removeNumberInBrackets", () => {
  test("removes number in brackets", () => {
    expect(removeNumberInBrackets("Hello [123] World")).toBe("Hello  World");
  });

  test("removes only the first occurrence of number in brackets", () => {
    expect(removeNumberInBrackets("Test [1] string [2]")).toBe(
      "Test  string [2]"
    );
  });

  test("returns the same string if there are no brackets", () => {
    expect(removeNumberInBrackets("No brackets here")).toBe("No brackets here");
  });

  test("returns an empty string if input is an empty string", () => {
    expect(removeNumberInBrackets("")).toBe("");
  });

  test("removes only the first occurrence when nested brackets are present", () => {
    expect(removeNumberInBrackets("Nested [123[456]] brackets")).toBe(
      "Nested [123[456]] brackets"
    );
  });
});
