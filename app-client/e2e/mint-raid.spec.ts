import { expect, test } from "@playwright/test";
require("dotenv").config({ path: "./.env.local" });

test.describe("Mint RAiD", () => {
  test("user should be able to mint new raid", async ({ page }) => {
    const baseURL = process.env.BASE_URL!;

    const testDate = {
      unformatted: "2033-05-23",
      formatted: "23-May-2033",
    };
    await page.goto(baseURL);

    // locate mint raid button and click
    const mintRaidButton = page.locator('[data-testid="mint-raid-button"]');
    await mintRaidButton.click();

    // create a raid with default values by clicking save button
    const saveRaidButton = page.locator('[data-testid="save-raid-button"]');
    await saveRaidButton.click();

    // locate raid speed dial button and hover over
    const raidSpeedDialButton = page
      .locator('[data-testid="raid-speeddial"] button')
      .first();
    await raidSpeedDialButton.waitFor({ state: "visible" });

    await raidSpeedDialButton.hover();

    // locate edit raid button and click
    const editRaidButton = page.locator('[data-testid="edit-raid-button"]');
    await editRaidButton.waitFor({ state: "visible" });
    await editRaidButton.click();

    // locate start date field and fill with test date
    const startDateField = page.locator('input[placeholder="RAiD start date"]');
    await startDateField.waitFor({ state: "visible" });
    await startDateField.fill(testDate.unformatted);

    // locate save raid button and click
    const saveRaidButton02 = page.locator('[data-testid="save-raid-button"]');
    await saveRaidButton02.waitFor({ state: "visible" });
    await saveRaidButton02.click();

    // back to homepage
    await page.goto(baseURL!);

    await page.waitForTimeout(1000);

    // locate first raid navlink from table and click
    const raidNavlink = page.locator('[data-testid="raid-navlink"]').first();
    await raidNavlink.waitFor({ state: "visible" });
    await raidNavlink.click();

    // locate start date label and verify
    const startDateLabel = page.locator('[data-testid="start-date-value"]');
    await startDateLabel.waitFor({ state: "visible" });
    expect(await startDateLabel.innerText()).toBe(testDate.formatted);
  });
});
