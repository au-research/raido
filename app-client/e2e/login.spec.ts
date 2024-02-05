import { expect, test } from "@playwright/test";

require("dotenv").config({ path: "./.env.local" });

const BASE_URL = "http://localhost:7080/";
const { RAIDO_ACCESS_TOKEN } = process.env;

if (!RAIDO_ACCESS_TOKEN) {
  throw new Error("RAIDO_ACCESS_TOKEN environment variable is not set.");
}

test.describe("User Login Flow", () => {
  test.beforeEach(async ({ page }) => {
    // Navigate once and perform initial setup.
    await page.goto(BASE_URL);
    await page.evaluate(() => localStorage.clear());
    await page.reload();
  });

  test("user should be able to login", async ({ page }) => {
    // Ensure user is initially logged out.
    await expect(
      page.locator('[data-testid="signed-in-user"]')
    ).not.toBeVisible();

    // Inject token and verify login state.
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, RAIDO_ACCESS_TOKEN);
    await page.reload();
    await expect(page.locator('[data-testid="signed-in-user"]')).toBeVisible();
  });
});
