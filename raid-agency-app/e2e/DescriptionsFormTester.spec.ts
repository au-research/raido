import { test, expect } from "@playwright/test";
import login from "./utils/login";

const { BASE_URL } = process.env;

if (!BASE_URL || BASE_URL === "undefined") {
  console.error("All environment variables must be set.");
  process.exit(1);
}

test.describe("DescriptionsFormTester", () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.goto(`${BASE_URL}/form-tester/descriptions`);
    await page.click('button[aria-label="Add Description"]');
  });

  test("renders TitlesForm and submit button", async ({ page }) => {
    await expect(
      page.locator('[data-testid="descriptions-form"]')
    ).toBeVisible();
    await expect(page.locator('[data-testid="submit-button"]')).toBeVisible();
  });

  test("initializes form with generated description", async ({ page }) => {
    const descriptionInput = page.locator('input[name="description.0.text"]');
    await expect(descriptionInput).toHaveValue(/./);
  });

  test("submits form with valid description", async ({ page }) => {
    await page.fill('input[name="description.0.text"]', "Valid Description");
    await page.click('[data-testid="submit-button"]');

    await expect(page.locator('[data-testid="submit-button"]')).toHaveText(
      "success"
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveText(
      "Form submitted successfully!"
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveCSS(
      "color",
      "rgb(46, 125, 50)"
    );
  });

  test("allows adding multiple descriptions", async ({ page }) => {
    await page.click('button[aria-label="Add Description"]');
    const descriptionsInput = await page
      .locator('input[name^="description."][name$=".text"]')
      .all();
    expect(descriptionsInput.length).toBeGreaterThan(1);
  });
});
