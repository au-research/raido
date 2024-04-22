import { expect, test } from "@playwright/test";
import "dotenv/config";

const BASE_URL = "http://localhost:7080/";
const { VITE_KEYCLOAK_E2E_USER, VITE_KEYCLOAK_E2E_PASSWORD } = process.env;

if (!VITE_KEYCLOAK_E2E_USER) {
  throw new Error("Environment variable `VITE_KEYCLOAK_E2E_USER` must be set.");
}

if (!VITE_KEYCLOAK_E2E_PASSWORD) {
  throw new Error(
    "Environment variable `VITE_KEYCLOAK_E2E_PASSWORD` must be set."
  );
}

test.describe("login", () => {
  test("user should be able to login", async ({ page }) => {
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

    expect(await page.locator('[data-testid="signed-in-user"]')).toBeTruthy();
  });
});
