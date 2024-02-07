import { chromium, type FullConfig } from "@playwright/test";
require("dotenv").config({ path: "./.env.local" });

const { RAIDO_ACCESS_TOKEN } = process.env;

if (!RAIDO_ACCESS_TOKEN) {
  throw new Error("RAIDO_ACCESS_TOKEN environment variable is not set.");
}
const isLocalEnv = process.env.REACT_APP_RAIDO_ENV === "dev";
async function globalSetup(config: FullConfig) {
  process.env.BASE_URL = isLocalEnv
    ? "http://localhost:7080"
    : "https://app.test.raid.org.au";
  const { baseURL, storageState } = config.projects[0].use;
  const browser = await chromium.launch();
  const page = await browser.newPage();
  await page.goto(baseURL!);
  await page.evaluate((token) => {
    localStorage.setItem("raidoAccessToken", token!);
  }, RAIDO_ACCESS_TOKEN);
  process.env.RAIDO_ACCESS_TOKEN = RAIDO_ACCESS_TOKEN;
  await page.context().storageState({ path: storageState as string });
  await browser.close();
}

export default globalSetup;
