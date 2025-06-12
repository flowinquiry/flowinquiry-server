import { test } from "@playwright/test";

import { assertAuthenticated } from "./helpers/login-check";
import { TeamsPage } from "./pages/teams-page";

test.describe("Teams Page", () => {
  test.use({ storageState: "./playwright/.auth/admin.json" });
  test("should create a new team and handle manager dialog", async ({
    page,
    context,
  }) => {
    // Check if the user is authenticated
    await assertAuthenticated(page);

    // Initialize page objects
    const teamsPage = new TeamsPage(page);

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

    // Check if the "New team" button is visible
    const newTeamButtonVisible = await teamsPage.newTeamButton.isVisible();
    console.log(`[DEBUG_LOG] New team button visible: ${newTeamButtonVisible}`);

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

    // Check if the form inputs are visible
    const nameInputVisible = await teamsPage.teamNameInput
      .isVisible()
      .catch(() => false);
    const descInputVisible = await teamsPage.teamDescriptionInput
      .isVisible()
      .catch(() => false);
    console.log(`[DEBUG_LOG] Name input visible: ${nameInputVisible}`);
    console.log(`[DEBUG_LOG] Description input visible: ${descInputVisible}`);

    // Check if the save button is visible
    const saveButtonVisible = await teamsPage.saveButton
      .isVisible()
      .catch(() => false);
    console.log(`[DEBUG_LOG] Save button visible: ${saveButtonVisible}`);

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
