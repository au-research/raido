import { expect, test } from "@playwright/test";

test.describe("Dates component", () => {
  test("start date field should not accept invalid date value", async ({
    page,
  }) => {
    const baseURL = process.env.BASE_URL;
    await page.goto(`${baseURL}/mint-raid-new/20000000`);
    await page.waitForLoadState("domcontentloaded");

    const testDates = {
      validDates: ["2033-05-23", "2033-05", "2033"],
      invalidDates: ["2033-05-35", "2033-13", "192", ""],
    };

    // locate save button
    const saveRaidButton = page.locator('[data-testid="save-raid-button"]');

    // locate start date field
    const startDateField = page.locator('input[placeholder="RAiD start date"]');
    await startDateField.waitFor({ state: "visible" });

    // fill start date field with valid dates and verify save button is enabled
    for (const date of testDates.validDates) {
      await startDateField.fill(date);
      expect(await saveRaidButton.isEnabled()).toBe(true);
    }

    // fill start date field with invalid dates and verify save button is disabled
    for (const date of testDates.invalidDates) {
      await startDateField.fill(date);
      expect(await saveRaidButton.isEnabled()).toBe(false);
    }
  });
});
