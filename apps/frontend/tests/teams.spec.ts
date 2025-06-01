import { expect, test } from "@playwright/test";

import { HomePage } from "./pages/home-page";
import { TeamsPage } from "./pages/teams-page";

test.describe("Teams Page", () => {
  test("should create a new team and handle manager dialog", async ({
    page,
  }) => {
    // Initialize page objects
    const homePage = new HomePage(page);
    const teamsPage = new TeamsPage(page);

    // Step 1: Login to the system with admin credentials
    console.log("[DEBUG_LOG] Navigating to home page and logging in");
    await homePage.navigateAndLogin();

    // Ensure we're logged in by checking URL
    const currentUrl = page.url();
    console.log(`[DEBUG_LOG] Current URL after login: ${currentUrl}`);

    await expect(page).toHaveURL(/\/portal/);

    // Step 2: Navigate to teams page
    console.log("[DEBUG_LOG] Navigating to teams page");
    await teamsPage.goto();
    await teamsPage.expectPageLoaded();
    console.log(`[DEBUG_LOG] Current URL after navigation: ${page.url()}`);

    // Step 3: Click on "New team" button
    console.log("[DEBUG_LOG] Clicking on New team button");

    // Add debug logging to help identify what's on the page
    console.log(
      "[DEBUG_LOG] Checking page content before clicking New team button",
    );

    // Log the page title and URL
    console.log(`[DEBUG_LOG] Page title: ${await page.title()}`);
    console.log(`[DEBUG_LOG] Page URL: ${page.url()}`);

    // Check if there are any buttons on the page
    const buttonCount = await page.locator("button").count();
    console.log(`[DEBUG_LOG] Number of buttons on page: ${buttonCount}`);

    // Check for elements with text containing "team"
    const teamElements = await page
      .locator("*:visible")
      .filter({ hasText: /team/i })
      .count();
    console.log(`[DEBUG_LOG] Elements containing 'team': ${teamElements}`);

    // Try to find the specific button we're looking for
    const newTeamButtonCount = await page
      .locator("button, a")
      .filter({ hasText: /new team|create team|add team/i })
      .count();
    console.log(
      `[DEBUG_LOG] Found ${newTeamButtonCount} potential New team buttons`,
    );

    // If we found potential buttons, log their text content
    if (newTeamButtonCount > 0) {
      const buttons = page
        .locator("button, a")
        .filter({ hasText: /new team|create team|add team/i });
      for (let i = 0; i < newTeamButtonCount; i++) {
        const buttonText = await buttons.nth(i).textContent();
        console.log(`[DEBUG_LOG] Button ${i} text: ${buttonText}`);
      }
    }

    // Debug screenshots are now only captured on test failures

    // Now try to click the button
    await teamsPage.clickNewTeamButton();

    // Step 4: Verify the URL is '/portal/teams/new/edit'
    await teamsPage.expectNewTeamEditPage();
    console.log(
      `[DEBUG_LOG] Current URL after clicking New team: ${page.url()}`,
    );

    // Step 5: Enter team name and description
    console.log("[DEBUG_LOG] Filling team details");

    // Add debug logging to help identify what's on the page
    console.log(
      "[DEBUG_LOG] Checking page content before filling team details",
    );

    // Log the page title and URL
    console.log(`[DEBUG_LOG] Page title: ${await page.title()}`);
    console.log(`[DEBUG_LOG] Page URL: ${page.url()}`);

    // Check if the input fields are visible
    const nameInputVisible = await page.getByLabel("Name").isVisible();
    const descInputVisible = await page.getByLabel("Description").isVisible();
    console.log(`[DEBUG_LOG] Name input visible: ${nameInputVisible}`);
    console.log(`[DEBUG_LOG] Description input visible: ${descInputVisible}`);

    // Check if there are any buttons on the page
    const editPageButtonCount = await page.locator("button").count();
    console.log(
      `[DEBUG_LOG] Number of buttons on page: ${editPageButtonCount}`,
    );

    // Try to find the specific button we're looking for
    const saveButtonCount = await page
      .locator('button, input[type="submit"]')
      .filter({ hasText: /save|create|submit|confirm/i })
      .count();
    console.log(`[DEBUG_LOG] Found ${saveButtonCount} potential Save buttons`);

    // If we found potential buttons, log their text content
    if (saveButtonCount > 0) {
      const buttons = page
        .locator('button, input[type="submit"]')
        .filter({ hasText: /save|create|submit|confirm/i });
      for (let i = 0; i < saveButtonCount; i++) {
        const buttonText = await buttons.nth(i).textContent();
        console.log(`[DEBUG_LOG] Save button ${i} text: ${buttonText}`);
      }
    }

    // Debug screenshots are now only captured on test failures

    // Now try to fill the team details
    await teamsPage.fillTeamDetails("Team ANC", "Team description");

    // Step 6: Verify redirection to the new team dashboard page
    const teamUrl = await teamsPage.expectRedirectToTeamDashboard();
    console.log(`[DEBUG_LOG] Redirected to team dashboard: ${teamUrl}`);

    // Step 7: Check if there is a dialog asking to enter at least one team manager
    console.log("[DEBUG_LOG] Checking for manager dialog");
    await teamsPage.expectManagerDialogDisplayed();

    // Step 8: Close the dialog
    console.log("[DEBUG_LOG] Closing manager dialog");
    await teamsPage.closeManagerDialog();
  });
});
