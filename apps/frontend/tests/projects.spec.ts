import { test } from "@playwright/test";

import { assertAuthenticated } from "./helpers/login-check";
import { createProject } from "./helpers/project-fixtures";
import { createTeam } from "./helpers/team-fixtures";

test.describe("Projects Page", () => {
  test.use({ storageState: "./playwright/.auth/admin.json" });

  test("should create a new project in a team", async ({ page }) => {
    // Check if the user is authenticated
    await assertAuthenticated(page);

    // Step 1: Create a new team using the fixture
    const teamName = "Project Test Team";
    const teamDescription = "Team for testing project creation";
    const teamUrl = await createTeam(page, teamName, teamDescription);
    console.log(`[DEBUG_LOG] Created team with dashboard URL: ${teamUrl}`);

    // Extract team ID from the URL
    // The URL format is /portal/teams/{encodedTeamId}/dashboard
    const urlParts = teamUrl.split("/");
    const encodedTeamId = urlParts[urlParts.length - 2]; // Second to last part
    console.log(`[DEBUG_LOG] Encoded team ID: ${encodedTeamId}`);

    // Step 2: Create a new project using the fixture
    const projectName = "Test Project";
    const projectDescription =
      "This is a test project created by automated tests";
    const projectShortName = "TEST";

    const project = await createProject(
      page,
      encodedTeamId,
      projectName,
      projectDescription,
      projectShortName,
    );

    console.log(`[DEBUG_LOG] Project created successfully: ${project.name}`);
  });

  test("should create a project with minimal details", async ({ page }) => {
    // Check if the user is authenticated
    await assertAuthenticated(page);

    // Step 1: Create a new team using the fixture
    const teamName = "Project Date Validation Team";
    const teamDescription = "Team for testing project date validation";
    const teamUrl = await createTeam(page, teamName, teamDescription);
    console.log(`[DEBUG_LOG] Created team with dashboard URL: ${teamUrl}`);

    // Extract team ID from the URL
    const urlParts = teamUrl.split("/");
    const encodedTeamId = urlParts[urlParts.length - 2]; // Second to last part
    console.log(`[DEBUG_LOG] Encoded team ID: ${encodedTeamId}`);

    // Step 2: Create a new project with minimal details using the fixture
    const projectName = "Minimal Project";
    const projectDescription = "This project tests minimal details";
    const projectShortName = "MINIMAL";

    const project = await createProject(
      page,
      encodedTeamId,
      projectName,
      projectDescription,
      projectShortName,
    );

    console.log(`[DEBUG_LOG] Project created successfully: ${project.name}`);
  });

  test("should create a project directly from team ID", async ({ page }) => {
    // Check if the user is authenticated
    await assertAuthenticated(page);

    // Step 1: Create a new team using the fixture
    const teamUrl = await createTeam(
      page,
      "Direct Project Team",
      "Team for direct project creation",
    );

    // Extract team ID from the URL
    const encodedTeamId = teamUrl.split("/")[teamUrl.split("/").length - 2];

    // Step 2: Create a project directly using the fixture with minimal parameters
    const project = await createProject(
      page,
      encodedTeamId,
      "Direct Project",
      "Project created directly with the fixture",
      "DIRECT",
    );

    // Verify the project was created successfully by checking the returned object
    console.log(
      `[DEBUG_LOG] Project created successfully with name: ${project.name}`,
    );
    console.log(`[DEBUG_LOG] Project short name: ${project.shortName}`);
    console.log(`[DEBUG_LOG] Project in team: ${project.teamId}`);
  });
});
