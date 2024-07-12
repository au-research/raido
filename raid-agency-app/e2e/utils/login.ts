import { Page } from "@playwright/test";
import "dotenv/config";

const SELECTORS = {
  loginButton: '[data-testid="login-button"]',
  usernameField: 'input[name="username"]',
  passwordField: 'input[name="password"]',
  submitButton: 'input[type="submit"]',
};

export default async function login(page: Page) {
  const { BASE_URL, VITE_KEYCLOAK_E2E_USER, VITE_KEYCLOAK_E2E_PASSWORD } =
    process.env;

  if (!VITE_KEYCLOAK_E2E_USER || !VITE_KEYCLOAK_E2E_PASSWORD || !BASE_URL) {
    throw new Error("All environment variables must be set.");
  }

  await page.goto(BASE_URL);

  await page.locator(SELECTORS.loginButton).click();
  await page.waitForSelector(SELECTORS.usernameField);

  await page.locator(SELECTORS.usernameField).fill(VITE_KEYCLOAK_E2E_USER);
  await page.locator(SELECTORS.passwordField).fill(VITE_KEYCLOAK_E2E_PASSWORD);

  await page.locator(SELECTORS.submitButton).click();
}
