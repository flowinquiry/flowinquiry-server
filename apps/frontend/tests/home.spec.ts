import { test } from "@playwright/test";

import { HomePage } from "./pages/home-page";

test.describe("Home Page", () => {
  test.use({ storageState: "playwright/.auth/admin.json" });
  test("should navigate to home page and login successfully", async ({
    page,
  }) => {
    const homePage = new HomePage(page);

    // Navigate to the home page
    await homePage.goto();

    // Since we're using the admin storage state, we should be already logged in
    // and redirected to the dashboard
    await homePage.expectPageLoaded();
  });

  test("should navigate and login in one step", async ({ page }) => {
    const homePage = new HomePage(page);

    // Navigate to home page and login with admin credentials in one step
    await homePage.navigateAndLogin();
  });

  test("should stay on login page when using incorrect credentials", async ({
    page,
  }) => {
    const homePage = new HomePage(page);

    // Navigate to the home page
    await homePage.goto();

    // Verify redirection to login page
    await homePage.expectRedirectToLogin();

    // Login with incorrect credentials
    await homePage.login("wrong@example.com", "wrongpassword");

    // Verify error message is displayed and still on login page
    await homePage.expectLoginError();
  });
});
