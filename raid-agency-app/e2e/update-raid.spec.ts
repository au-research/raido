import { expect, test } from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const BASE_URL = "http://localhost:7080/";

const SELECTORS = {
  editRaidButton: '[data-testid="edit-raid-button"]',
  endDateField: '[name="date.endDate"]',
  firstTitleField: '[name="title.0"]',
  raidNavLink: '[data-testid="raid-navlink"]',
  raidRow: ".MuiDataGrid-row",
  saveRaidButton: '[data-testid="save-raid-button"]',
};

test.beforeEach(async ({ page }) => {
  await login(page);
});

test.describe("Update RAiD", () => {
  test("user should be able to update the first raid title", async ({
    page,
  }) => {
    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidNavLink);
    await page.locator(SELECTORS.raidNavLink).first().click();

    await page.waitForSelector(SELECTORS.editRaidButton);
    await page.locator(SELECTORS.editRaidButton).first().click();

    await page.waitForSelector(SELECTORS.firstTitleField);
    const firstTitleField = page.locator(SELECTORS.firstTitleField).first();
    await firstTitleField.fill("An updated title...");

    await page.waitForSelector(SELECTORS.saveRaidButton);
    const saveRaidButton = page.locator(SELECTORS.saveRaidButton).first();
    await saveRaidButton.click();

    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidRow);
    const firstRowTexts = await page
      .locator(SELECTORS.raidRow)
      .first()
      .allInnerTexts();

    expect(firstRowTexts[0]).toContain("An updated title...");
  });
});
