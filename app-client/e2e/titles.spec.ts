import { expect, test } from "@playwright/test";

test.describe("Titles component", () => {
  test("title field should not accept empty value", async ({ page }) => {
    const baseURL = process.env.BASE_URL!;
    await page.goto(`${baseURL}/mint-raid-new/20000000`);
    await page.waitForLoadState("domcontentloaded");

    // locate save button
    const saveRaidButton = page.locator('[data-testid="save-raid-button"]');

    // locate title field
    const titleField = page.locator('input[placeholder="RAiD title"]');
    await titleField.waitFor({ state: "visible" });

    // fill title field with valid title and verify save button is enabled
    await titleField.fill("foo");
    expect(await saveRaidButton.isEnabled()).toBe(true);

    // fill title field with empty value and verify save button is disabled
    await titleField.fill("");
    expect(await saveRaidButton.isEnabled()).toBe(false);
  });

  test("Clicking on the `add title` button should create new title field", async ({
    page,
  }) => {
    const baseURL = process.env.BASE_URL!;
    await page.goto(`${baseURL}/mint-raid-new/20000000`);
    await page.waitForLoadState("domcontentloaded");

    expect(await page.locator('input[placeholder="RAiD title"]').count()).toBe(
      1
    );

    // locate add title button and click
    const addTitleButton = page.locator('button[aria-label="Add title"]');
    await addTitleButton.waitFor({ state: "visible" });
    await addTitleButton.click();

    expect(await page.locator('input[placeholder="RAiD title"]').count()).toBe(
      2
    );
  });
});
