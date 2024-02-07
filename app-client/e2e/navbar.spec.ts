import { expect, test } from "@playwright/test";

require("dotenv").config({ path: "./.env.local" });

test.describe("Navbar authenticated vs. unauthenticated", () => {
  test("account menu should be visible when authenticated", async ({
    page,
  }) => {
    const baseURL = process.env.BASE_URL!;
    await page.goto(baseURL);
    await page.waitForLoadState("domcontentloaded");
    await expect(
      page.locator('[data-testid="account-menu-button"]')
    ).toHaveCount(1);
  });

  test("account menu should NOT be visible when NOT authenticated", async ({
    page,
  }) => {
    const baseURL = process.env.BASE_URL!;
    await page.goto(baseURL);
    await page.waitForLoadState("domcontentloaded");
    await page.evaluate(() => localStorage.clear());
    await page.reload();
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, process.env.RAIDO_ACCESS_TOKEN!);
    await page.reload();

    const accountMenuButton = page.locator(
      '[data-testid="account-menu-button"]'
    );
    expect(await accountMenuButton.isVisible()).toBe(true);
  });

  test("user should be able to sign out", async ({ page }) => {
    const baseURL = process.env.BASE_URL!;
    await page.goto(baseURL);
    await page.evaluate(() => localStorage.clear());
    await page.reload();
    await page.evaluate((token) => {
      localStorage.setItem("raidoAccessToken", token);
    }, process.env.RAIDO_ACCESS_TOKEN!);
    await page.reload();

    const accountMenuButton = page.locator(
      '[data-testid="account-menu-button"]'
    );

    await accountMenuButton.waitFor({ state: "visible" });

    await accountMenuButton.click();

    const signOutButton = page.locator('[data-testid="sign-out-button"]');
    await signOutButton.waitFor({ state: "visible" });
    await signOutButton.click();

    expect(await accountMenuButton.isVisible()).toBe(false);
  });
});
