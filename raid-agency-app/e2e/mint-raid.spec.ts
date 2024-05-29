import { expect, test } from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const BASE_URL = process.env.BASE_URL;

if (!BASE_URL) {
  throw new Error("BASE_URL is not set");
}

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
    await page.goto(`${BASE_URL}/raids/new`);

    // // Wait for the raid table to load and store initial row count for verification later
    // await page.waitForSelector(SELECTORS.raidRow);
    // const firstRowTexts = await page
    //   .locator(SELECTORS.raidRow)
    //   .first()
    //   .allInnerTexts();

    // // Click the 'mint raid' button to start the raid creation process
    // await page.locator(SELECTORS.mintRaidButton).click();

    await page.waitForSelector(SELECTORS.saveRaidButton);
    // Click the 'save raid' button to finalize the raid creation
    await page.locator(SELECTORS.saveRaidButton).click();

    // wait for navigation
    await page.waitForURL(BASE_URL);

    // expect page address to be BASE_URL
    expect(page.url().replace(/\/$/, "")).toStrictEqual(BASE_URL);
  });
});
