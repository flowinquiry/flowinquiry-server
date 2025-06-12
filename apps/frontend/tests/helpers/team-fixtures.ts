/**
 * Team Fixtures
 *
 * This file provides utility functions for creating and managing teams in tests.
 * It helps ensure consistent team creation across different test files,
 * making tests more maintainable and reducing code duplication.
 */

import { Page } from "@playwright/test";

import { TeamsPage } from "../pages/teams-page";

/**
 * Creates a new team with the specified name and description.
 *
 * This function:
 * 1. Navigates to the teams page
 * 2. Clicks on the "New team" button
 * 3. Fills in the team details form
 * 4. Verifies redirection to the new team dashboard page
 * 5. Handles the manager dialog
 *
 * @param page - The Playwright Page object
 * @param teamName - The name of the team to create
 * @param teamDescription - The description of the team to create
 * @returns The URL of the newly created team's dashboard
 */
export async function createTeam(
  page: Page,
  teamName: string = "Test Team",
  teamDescription: string = "Test team description",
): Promise<string> {
  // Initialize page objects
  const teamsPage = new TeamsPage(page);

  // Navigate to teams page
  console.log("[DEBUG_LOG] Navigating to teams page");
  await teamsPage.goto();
  await teamsPage.expectPageLoaded();

  // Click on "New team" button
  console.log("[DEBUG_LOG] Clicking on New team button");
  await teamsPage.clickNewTeamButton();
  await teamsPage.expectNewTeamEditPage();

  // Fill in team details
  console.log("[DEBUG_LOG] Filling team details");
  await teamsPage.fillTeamDetails(teamName, teamDescription);

  // Verify redirection to the new team dashboard page
  const teamUrl = await teamsPage.expectRedirectToTeamDashboard();
  console.log(`[DEBUG_LOG] Redirected to team dashboard: ${teamUrl}`);

  // Handle manager dialog
  console.log("[DEBUG_LOG] Checking for manager dialog");
  await teamsPage.expectManagerDialogDisplayed();
  console.log("[DEBUG_LOG] Closing manager dialog");
  await teamsPage.closeManagerDialog();

  return teamUrl;
}
