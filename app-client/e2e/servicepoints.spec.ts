import { expect, test } from "@playwright/test";

test.describe("Servicepoints page", () => {
  test("operator should be able to create new service point", async ({
    page,
  }) => {
    const baseURL = process.env.BASE_URL || "http://localhost:7080";
    await page.goto(`${baseURL}/list-service-point`);
    await page.waitForLoadState("domcontentloaded");

    const servicePointsTable = page.locator(
      '[data-testid="service-points-table"]'
    );
    await servicePointsTable.waitFor({ state: "visible" });

    const tableRowsCount = await page.locator("tr").count();

    const createNewServicePointButton = page.locator(
      'button[aria-label="create new service point"]'
    );
    await createNewServicePointButton.waitFor({ state: "visible" });
    await createNewServicePointButton.click();

    const servicePointNameField = page.locator(
      'input[placeholder="Service point name"]'
    );

    await servicePointNameField.fill(`Test Service Point ${Date.now()}`);

    const servicePointIdentifierOwnerField = page.locator(
      'input[placeholder="Identifier owner"]'
    );

    await servicePointIdentifierOwnerField.fill("Test Identifier Owner");

    const createServicePointButton = page.locator(
      'button[aria-label="Create service point"]'
    );
    await createServicePointButton.waitFor({ state: "visible" });
    await createServicePointButton.click();

    await page.goto(`${baseURL}/list-service-point`);
    await page.waitForLoadState("domcontentloaded");

    const servicePointsTableAfterOperation = page.locator(
      '[data-testid="service-points-table"]'
    );
    await servicePointsTableAfterOperation.waitFor({ state: "visible" });

    const tableRowsCountAfterOperation = await page.locator("tr").count();

    expect(tableRowsCountAfterOperation).toBe(tableRowsCount + 1);
  });
});
