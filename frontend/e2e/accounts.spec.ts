import { expect, test } from "@playwright/test";

test.skip("creates and displays an account", async ({ page }) => {
  // Enabled when the full application Compose profile is added.
  await page.goto("/accounts");
  await expect(page.getByRole("heading", { name: "Accounts" })).toBeVisible();
});
