export default {
  index: {
    title: "Introduction",
    type: "page",
    display: "hidden",
  },
  user_guides: {
    type: "page",
    title: "User Guides",
    items: {
      introduction: "Introduction",
      getting_started: "Get started",
      workflow_management: "Workflow management",
      working_with_requests: "Working with requests",
      administrator: {
        title: "Administrator",
        items: {
          smtp_server: "Email Setup Guide (SMTP Configuration)",
        },
      },
    },
  },
  developer_guides: {
    type: "page",
    title: "Developer Guides",
    items: {
      index: "Programming languages and development tools",
      frontend: {
        title: "Frontend",
        items: {
          getting_started: "Get started",
          project_structure: "Project Structure",
          localization: "Localization",
        },
      },
      backend: {
        title: "Backend",
        items: {
          getting_started: "Getting-started",
          overview: {
            title: "Overview",
            items: {
              high_level_architect: "High-level architect",
              data_layer: "Data Layer",
            },
          },
          database_migration: "Database migration",
          integration_testing: "Integration Testing", //added the integrating section
        },
      },
      deployment: {
        title: "Deployment",
        items: {
          build_docker_image: "Build FlowInquiry Docker Images (Optional)",
          docker: "Docker",
          kubernetes: "Kubernetes",
        },
      },
      documentation: "Documentation",
    },
  },
  how_to_contributes: {
    title: "How to Contribute",
    type: "page",
    items: {
      your_action_is_meaningful_to_us: "Your Action Is Meaningful to Us",
      your_first_pr: "Your first PR",
    },
  },
  about: {
    type: "page",
    title: "About",
  },
};
