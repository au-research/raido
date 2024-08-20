import { test, expect } from "@playwright/test";
import login from "./utils/login";

const { BASE_URL } = process.env;

if (!BASE_URL || BASE_URL === "undefined") {
  console.error("All environment variables must be set.");
  process.exit(1);
}

test.describe("TitlesFormTester", () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await page.goto(`${BASE_URL}/form-tester/titles`);
  });

  test("renders TitlesForm and submit button", async ({ page }) => {
    await expect(page.locator('[data-testid="titles-form"]')).toBeVisible();
    await expect(page.locator('[data-testid="submit-button"]')).toBeVisible();
  });

  test("initializes form with generated title", async ({ page }) => {
    const titleInput = page.locator('input[name="title.0.text"]');
    await expect(titleInput).toHaveValue(/./);
  });

  test("submits form with valid title", async ({ page }) => {
    await page.fill('input[name="title.0.text"]', "Valid Title");
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

  test("does not submit form with invalid title", async ({ page }) => {
    await page.fill('input[name="title.0.text"]', "");
    await page.click('[data-testid="submit-button"]');

    await expect(page.locator('[data-testid="submit-button"]')).toHaveText(
      "error"
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveText(
      "Form submission failed. Please check your inputs."
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveCSS(
      "color",
      "rgb(211, 47, 47)"
    );
  });

  test("submits form with valid start date", async ({ page }) => {
    await page.fill('input[name="title.0.startDate"]', "2023");
    await page.click('[data-testid="submit-button"]');

    await expect(page.locator('[data-testid="submit-button"]')).toHaveText(
      "success"
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveText(
      "Form submitted successfully!"
    );
  });

  test("does not submit form with invalid start date", async ({ page }) => {
    await page.fill('input[name="title.0.startDate"]', "20231");
    await page.click('[data-testid="submit-button"]');

    await expect(page.locator('[data-testid="submit-button"]')).toHaveText(
      "error"
    );
    await expect(page.locator('[data-testid="submission-status"]')).toHaveText(
      "Form submission failed. Please check your inputs."
    );
  });

  test("allows adding multiple titles", async ({ page }) => {
    await page.click('button[aria-label="Add Title"]');
    const titleInputs = await page
      .locator('input[name^="title."][name$=".text"]')
      .all();
    expect(titleInputs.length).toBeGreaterThan(1);
  });
});
