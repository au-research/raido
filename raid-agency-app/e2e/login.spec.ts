import { expect, test } from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const SELECTORS = {
  signedInUser: '[data-testid="signed-in-user"]',
};

test.describe("login", () => {
  test("user should be able to login", async ({ page }) => {
    await login(page);
    await page.waitForSelector(SELECTORS.signedInUser);
    expect(await page.locator(SELECTORS.signedInUser).isVisible()).toBe(true);
  });
});
