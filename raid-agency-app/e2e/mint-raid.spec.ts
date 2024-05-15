import { expect, test } from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const BASE_URL = "http://localhost:7080/";

const SELECTORS = {
  raidRow: ".MuiDataGrid-row",
  mintRaidButton: '[data-testid="mint-raid-button"]',
  saveRaidButton: '[data-testid="save-raid-button"]',
};

test.beforeEach(async ({ page }) => {
  await login(page);
});

test.describe("Mint RAiD", () => {
  test("user should be able to mint new raid", async ({ page }) => {
    await page.goto(BASE_URL);

    // Wait for the raid table to load and store initial row count for verification later
    await page.waitForSelector(SELECTORS.raidRow);
    const firstRowTexts = await page
      .locator(SELECTORS.raidRow)
      .first()
      .allInnerTexts();

    // Click the 'mint raid' button to start the raid creation process
    await page.locator(SELECTORS.mintRaidButton).click();

    // Click the 'save raid' button to finalize the raid creation
    await page.locator(SELECTORS.saveRaidButton).click();

    // Wait for the raid table to update
    await page.waitForSelector(SELECTORS.raidRow);

    // Wait for the raid table to load and compare the updated row count
    const firstRowTextsUpdated = await page
      .locator(SELECTORS.raidRow)
      .first()
      .allInnerTexts();

    expect(firstRowTexts).not.toBe(firstRowTextsUpdated);
  });
});
