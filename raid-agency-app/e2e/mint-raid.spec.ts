import { expect, test } from "@playwright/test";
import "dotenv/config";

const BASE_URL = "http://localhost:7080/";
const { VITE_KEYCLOAK_E2E_USER, VITE_KEYCLOAK_E2E_PASSWORD } = process.env;

if (!VITE_KEYCLOAK_E2E_USER || !VITE_KEYCLOAK_E2E_PASSWORD) {
  throw new Error("all environment variables must be set.");
}

test.beforeEach(async ({ page }) => {
  await page.goto(BASE_URL);

  const loginButton = page.locator('[data-testid="login-button"]');
  await loginButton.click();

  await page.waitForSelector('input[name="username"]');

  const userField = page.locator('input[name="username"]');
  await userField.fill(VITE_KEYCLOAK_E2E_USER);

  const passwordField = page.locator('input[name="password"]');
  await passwordField.fill(VITE_KEYCLOAK_E2E_PASSWORD);

  const submitButton = page.locator('input[type="submit"]');
  await submitButton.click();
});

test.describe("Mint RAiD", () => {
  test("user should be able to mint new raid", async ({ page }) => {
    await page.goto(BASE_URL);

    // Wait for the raid table to load and store initial row count for verification later
    await page.waitForSelector(".MuiDataGrid-row");
    const firstRowTexts = await page
      .locator(".MuiDataGrid-row")
      .first()
      .allInnerTexts();

    await page.screenshot({ path: "before.png" });

    // Click the 'mint raid' button to start the raid creation process
    await page.locator('[data-testid="mint-raid-button"]').click();

    // Click the 'save raid' button to finalize the raid creation
    await page.locator('[data-testid="save-raid-button"]').click();

    await page.waitForTimeout(1000);

    await page.goto(BASE_URL);

    await page.waitForSelector(".MuiDataGrid-row");

    await page.screenshot({ path: "after.png" });

    const firstRowTextsUpdated = await page
      .locator(".MuiDataGrid-row")
      .first()
      .allInnerTexts();

    expect(firstRowTexts).not.toBe(firstRowTextsUpdated);
  });

  // test("user should be able to mint new raid", async ({ page }) => {
  //   const testDate = {
  //     unformatted: "2033-05-23",
  //     formatted: "23-May-2033",
  //   };
  //   await page.goto(BASE_URL);

  //   // locate mint raid button and click
  //   const mintRaidButton = page.locator('[data-testid="mint-raid-button"]');
  //   await mintRaidButton.click();

  //   // create a raid with default values by clicking save button
  //   const saveRaidButton = page.locator('[data-testid="save-raid-button"]');
  //   await saveRaidButton.click();

  //   // locate raid speed dial button and hover over
  //   const raidSpeedDialButton = page
  //     .locator('[data-testid="raid-speeddial"] button')
  //     .first();
  //   await raidSpeedDialButton.waitFor({ state: "visible" });

  //   await raidSpeedDialButton.hover();

  //   // locate edit raid button and click
  //   const editRaidButton = page.locator('[data-testid="edit-raid-button"]');
  //   await editRaidButton.waitFor({ state: "visible" });
  //   await editRaidButton.click();

  //   // locate start date field and fill with test date
  //   const startDateField = page.locator('input[placeholder="RAiD start date"]');
  //   await startDateField.waitFor({ state: "visible" });
  //   await startDateField.fill(testDate.unformatted);

  //   // locate save raid button and click
  //   const saveRaidButton02 = page.locator('[data-testid="save-raid-button"]');
  //   await saveRaidButton02.waitFor({ state: "visible" });
  //   await saveRaidButton02.click();

  //   await page.waitForTimeout(1000);

  //   // back to homepage
  //   await page.goto(BASE_URL);

  //   // locate first raid navlink from table and click
  //   const raidNavlink = page.locator('[data-testid="raid-navlink"]').first();
  //   await raidNavlink.waitFor({ state: "visible" });
  //   await raidNavlink.click();

  //   // locate start date label and verify
  //   const startDateLabel = page.locator('[data-testid="start-date-value"]');
  //   await startDateLabel.waitFor({ state: "visible" });
  //   expect(await startDateLabel.innerText()).toBe(testDate.formatted);
  // });
});
