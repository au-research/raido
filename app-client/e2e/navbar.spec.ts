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

test.describe("Navbar authenticated vs. unauthenticated", () => {
  test("account menu should NOT be visible when authenticated", async ({
    page,
  }) => {
    await expect(
      page.locator('[data-testid="account-menu-button"]')
    ).toHaveCount(0);
  });

  test("account menu should be visible when authenticated", async ({
    page,
  }) => {
    await page.goto(BASE_URL);
    await page.evaluate(() => localStorage.clear());
    await page.reload();
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, RAIDO_ACCESS_TOKEN);
    await page.reload();

    await page.waitForTimeout(3000);

    const accountMenuButton = page.locator(
      '[data-testid="account-menu-button"]'
    );
    expect(await accountMenuButton.isVisible()).toBe(true);
  });

  test("user should be able to sign out", async ({ page }) => {
    await page.goto(BASE_URL);
    await page.evaluate(() => localStorage.clear());
    await page.reload();
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, RAIDO_ACCESS_TOKEN);
    await page.reload();

    await page.waitForTimeout(3000);

    const accountMenuButton = page.locator(
      '[data-testid="account-menu-button"]'
    );

    await accountMenuButton.waitFor({ state: "visible" });

    await accountMenuButton.click();

    const signOutButton = page.locator('[data-testid="sign-out-button"]');
    await signOutButton.waitFor({ state: "visible" });
    await signOutButton.click();
    await page.waitForTimeout(3000);

    expect(await accountMenuButton.isVisible()).toBe(false);
  });
});
