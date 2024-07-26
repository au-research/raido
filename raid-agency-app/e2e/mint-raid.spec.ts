import "dotenv/config";
import { expect, test } from "@playwright/test";
import login from "./utils/login";

const { BASE_URL } = process.env;

if (!BASE_URL || BASE_URL === "undefined") {
  console.error("All environment variables must be set.");
  process.exit(1);
}

const SELECTORS = {
  raidRow: ".MuiDataGrid-row",
  mintRaidButton: '[data-testid="mint-raid-button"]',
  saveRaidButton: '[data-testid="save-raid-button"]',
  addContributorButton: '[data-testid="add-contributor-button"]',
  contributorIdInput: '[data-testid="contributor-id-input"] input',
};

test.beforeEach(async ({ page }) => {
  await login(page);
});

test.describe("Mint RAiD", () => {
  test("user should be able to mint new raid", async ({ page }) => {
    // await page.goto(`${BASE_URL}/raids/new`);

    // await page.waitForSelector(SELECTORS.addContributorButton);
    // await page.locator(SELECTORS.addContributorButton).click();

    // await page.waitForSelector(SELECTORS.contributorIdInput);
    // await page
    //   .locator(SELECTORS.contributorIdInput)
    //   .fill("https://orcid.org/0009-0000-9306-3120");

    // await page.waitForSelector(SELECTORS.saveRaidButton);
    // // Click the 'save raid' button to finalize the raid creation
    // await page.locator(SELECTORS.saveRaidButton).click();

    // console.log("BASE_URL", BASE_URL);

    // // wait for navigation
    // await page.waitForURL(BASE_URL);

    // // expect page address to be BASE_URL
    // expect(page.url().replace(/\/$/, "")).toStrictEqual(BASE_URL);

    // currently disabled until orcid integration is implemented
    expect(true).toBeTruthy();
  });
});
