/**
 * Project Fixtures
 *
 * This file provides utility functions for creating and managing projects in tests.
 * It helps ensure consistent project creation across different test files,
 * making tests more maintainable and reducing code duplication.
 */

import { Page } from "@playwright/test";

import { ProjectsPage } from "../pages/projects-page";

/**
 * Creates a new project within a team.
 *
 * This function:
 * 1. Navigates to the team's projects page
 * 2. Clicks on the "New project" button
 * 3. Fills in the project details form
 * 4. Saves the project
 * 5. Verifies the project was created successfully
 *
 * @param page - The Playwright Page object
 * @param teamId - The ID of the team (either raw or already encoded)
 * @param projectName - The name of the project to create
 * @param projectDescription - The description of the project to create
 * @param projectShortName - The short name of the project to create
 * @param isTeamIdEncoded - Whether the teamId is already encoded
 * @returns An object containing the project details and teamId
 */
export async function createProject(
  page: Page,
  teamId: string,
  projectName: string = "Test Project",
  projectDescription: string = "Test project description",
  projectShortName: string = "TEST",
  isTeamIdEncoded: boolean = true,
): Promise<{
  name: string;
  description: string;
  shortName: string;
  teamId: string;
}> {
  // Initialize page objects
  const projectsPage = new ProjectsPage(page);

  // Navigate to the team's projects page
  console.log(
    `[DEBUG_LOG] Navigating to team projects page for team ID: ${teamId}`,
  );
  await projectsPage.gotoTeamProjects(teamId, isTeamIdEncoded);
  await projectsPage.expectProjectsPageLoaded(teamId, isTeamIdEncoded);

  // Click on "New project" button
  console.log("[DEBUG_LOG] Clicking on New project button");
  await projectsPage.clickNewProjectButton();

  // Fill in project details
  console.log(`[DEBUG_LOG] Filling project details: ${projectName}`);
  await projectsPage.fillProjectDetails(
    projectName,
    projectDescription,
    projectShortName,
  );

  // Save the project
  console.log("[DEBUG_LOG] Saving project");
  await projectsPage.saveProject();

  // Verify the project was created successfully
  console.log(`[DEBUG_LOG] Verifying project ${projectName} exists`);
  await projectsPage.expectProjectExists(projectName, teamId, isTeamIdEncoded);
  console.log(`[DEBUG_LOG] Project created successfully: ${projectName}`);

  // Return project details
  return {
    name: projectName,
    description: projectDescription,
    shortName: projectShortName,
    teamId: teamId,
  };
}
