import { expect, test } from "@playwright/test";
require("dotenv").config({ path: "./.env.local" });

const BASE_URL = "http://localhost:7080/";
const { RAIDO_ACCESS_TOKEN } = process.env;

if (!RAIDO_ACCESS_TOKEN) {
  throw new Error("RAIDO_ACCESS_TOKEN environment variable is not set.");
}

test.beforeEach(async ({ page }) => {
  // Navigate once and perform initial setup.
  await page.goto(BASE_URL);
  await page.evaluate(() => localStorage.clear());
  await page.reload();
});

test.describe("Mint RAiD", () => {
  test.beforeEach(async ({ page }) => {
    // Navigate once and perform initial setup.
    await page.goto(BASE_URL);
    await page.evaluate(() => localStorage.clear());
    await page.reload();
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, RAIDO_ACCESS_TOKEN);
    await page.reload();
  });

  test("user should be able to mint new raid", async ({ page }) => {
    const testDate = {
      unformatted: "2033-05-23",
      formatted: "23-May-2033",
    };
    await page.goto(BASE_URL);

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

    await page.waitForTimeout(3000);

    // back to homepage
    await page.goto(BASE_URL);

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
