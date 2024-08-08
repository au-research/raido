import { expect, test } from "@playwright/test";
import "dotenv/config";
import login from "./utils/login";

const SELECTORS = {
  signedInUser: '[data-testid="signed-in-user"]',
};

test.describe("login", () => {
  test("user should be able to login", async ({ page }) => {
    await login(page);
    // Wait for the specific element that indicates a successful login
    await page.waitForSelector("text=Welcome to RAiD");

    // Alternatively, you can use page.waitForEvent("load") if the page is reloaded upon login
    // await page.waitForEvent('load');

    // Validate the presence of the welcome text
    const textContent = await page.evaluate(() => document.body.innerText);
    expect(textContent).toContain("Welcome to RAiD");
  });
});
