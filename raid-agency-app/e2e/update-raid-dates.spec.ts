import {expect, test} from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const BASE_URL = process.env.BASE_URL;

if (!BASE_URL) {
  throw new Error("BASE_URL is not set");
}
const SELECTORS = {
  raidNavLink: '[data-testid="raid-navlink"]',
  editRaidButton: '[data-testid="edit-raid-button"]',
  startDateField: '[name="date.startDate"]',
  endDateField: '[name="date.endDate"]',
  saveRaidButton: '[data-testid="save-raid-button"]',
};

test.beforeEach(async ({ page }) => {
  await login(page);
});

test.describe("Update RAiD - Dates component", () => {
  test("button to update raid should be disabled when start date is invalid", async ({
    page,
  }) => {
    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidNavLink);
    await page.locator(SELECTORS.raidNavLink).first().click();

    await page.waitForSelector(SELECTORS.editRaidButton);
    await page.locator(SELECTORS.editRaidButton).first().click();

    const endDateField = page.locator(SELECTORS.startDateField).first();
    await endDateField.fill("123");

    const saveRaidButton = page.locator(SELECTORS.saveRaidButton).first();
    expect(await saveRaidButton.isDisabled()).toBe(true);
  });

  test("button to update raid should be disabled when end date is invalid", async ({
    page,
  }) => {
    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidNavLink);
    await page.locator(SELECTORS.raidNavLink).first().click();

    await page.waitForSelector(SELECTORS.editRaidButton);
    await page.locator(SELECTORS.editRaidButton).first().click();

    const endDateField = page.locator(SELECTORS.endDateField).first();
    await endDateField.fill("2022-13-31");

    const saveRaidButton = page.locator(SELECTORS.saveRaidButton).first();
    expect(await saveRaidButton.isDisabled()).toBe(true);
  });

  test("button to update raid should be disabled when start date is empty", async ({
    page,
  }) => {
    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidNavLink);
    await page.locator(SELECTORS.raidNavLink).first().click();

    await page.waitForSelector(SELECTORS.editRaidButton);
    await page.locator(SELECTORS.editRaidButton).first().click();

    const endDateField = page.locator(SELECTORS.startDateField).first();
    await endDateField.fill("");

    const saveRaidButton = page.locator(SELECTORS.saveRaidButton).first();
    expect(await saveRaidButton.isDisabled()).toBe(true);
  });

  test("button to update raid should be enabled when start date is valid", async ({
    page,
  }) => {
    await page.goto(BASE_URL);

    await page.waitForSelector(SELECTORS.raidNavLink);
    await page.locator(SELECTORS.raidNavLink).first().click();

    await page.waitForSelector(SELECTORS.editRaidButton);
    await page.locator(SELECTORS.editRaidButton).first().click();

    const endDateField = page.locator(SELECTORS.startDateField).first();
    await endDateField.fill("2020");

    const saveRaidButton = page.locator(SELECTORS.saveRaidButton).first();
    expect(await saveRaidButton.isEnabled()).toBe(true);
  });
});
