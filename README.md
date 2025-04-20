# FlowInquiry Server
[![Build Status](https://github.com/flowinquiry/flowinquiry/actions/workflows/ci.yml/badge.svg)](https://github.com/flowinquiry/flowinquiry/actions/workflows/ci.yml)
[![Contributors](https://img.shields.io/github/contributors/flowinquiry/flowinquiry.svg)](https://github.com/flowinquiry/flowinquiry/graphs/contributors)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/69704fb598fa40b5b053916ba4272797)](https://app.codacy.com/gh/flowinquiry/flowinquiry/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Frontend Docker Pulls](https://img.shields.io/docker/pulls/flowinquiry/flowinquiry-frontend?label=frontend%20pulls&logo=docker)](https://hub.docker.com/r/flowinquiry/flowinquiry-frontend)
[![Backend Docker Pulls](https://img.shields.io/docker/pulls/flowinquiry/flowinquiry-server?label=backend%20pulls&logo=docker)](https://hub.docker.com/r/flowinquiry/flowinquiry-server)
[![GitHub stars](https://img.shields.io/github/stars/flowinquiry/flowinquiry-server.svg?style=social)](https://github.com/flowinquiry/flowinquiry-server/stargazers)
![License](https://img.shields.io/badge/License-AGPLv3-blue)

<div style="display: flex; justify-content: center; align-items: center;">
  <a href="https://flowinquiry.io" target="_blank">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="assets/logo-dark.svg" type="image/svg+xml">
      <img alt="FlowInquiry Logo" src="assets/logo-light.svg"/>
    </picture>
  </a>
</div>

<div style="display: flex; justify-content: center; gap: 10px;">
  <br />
  <a href="https://flowinquiry.io" rel="dofollow"><strong>Main page</strong></a> 
  | 
  <a href="https://docs.flowinquiry.io" rel="dofollow"><strong>Explore the docs »</strong></a>
  |
  <a href="https://github.com/orgs/flowinquiry/projects/4/views/3" rel="dofollow"><strong>Backlogs</strong></a>
  |
  <a href="https://hub.docker.com/r/flowinquiry/flowinquiry-server" rel="dofollow"><strong>Docker</strong></a>
  <br />
</div>


## What is FlowInquiry

FlowInquiry is a free, open-source solution that embraces transparency as an alternative to Jira, ServiceNow, and Zendesk. Designed for managing cases, tickets, and requests, it streamlines communication, ensures timely resolutions, and supports customizable workflows with SLAs. By eliminating vendor lock-in and costly subscriptions, FlowInquiry enhances efficiency, accountability, and collaboration for teams handling internal and external inquiries.

![FlowInquiry](assets/flowinquiry_slide.gif)

## ✅ Real-World Use Cases Solved by FlowInquiry
FlowInquiry helps teams manage requests, projects, and collaboration with clarity and control. Here are common ways organizations use it:

* Project & Task Management
Organize tasks, set priorities, track progress, and ensure deadlines with workflow automation and SLAs.

* Cross-Team Collaboration
Centralize communication across teams, reduce misalignment, and route requests through well-defined workflows.

* IT & Internal Support
Manage service desk tickets like access requests or software issues with automated handling and clear ownership.

* Incident & On-Call Management
Track incidents with SLAs, escalation rules, and accountability—ideal for rotating shifts and critical operations.

* CRM Case Handling
Customize workflows for handling customer requests, complaints, or inquiries to improve resolution time and satisfaction.

## ⚙️ Key Features of FlowInquiry

* 🧩 Custom Workflows – Tailor request lifecycles with state transitions and actions

* ⏱ SLA Enforcement – Track deadlines, escalate overdue items

* 👥 Comments & Watchers – Collaborate with full visibility

* 📅 Timeline View – Visualize request history and changes

* 📂 Projects & Iterations – Group work into structured cycles

* 🧵 Change History – Full audit trail of updates

* 🔐 Role-Based Access – Secure, granular permissions

* 🌍 Multilingual Support – Serve global teams and users

* 🔄 Integrations – Connect with Slack, Email, and more

* 🚀 Flexible Deployment – Run on Docker, Kubernetes, or your own infra

## Built With

<div style="display: flex; justify-content: left; gap: 20px; align-items: center;">
    <img src="assets/spring-boot.svg" alt="Spring Boot" width="80" height="80" title="Acts as the backbone of the back-end, orchestrating various components. It handles the creation and management of REST APIs, service layers, and controllers to facilitate business logic. Spring Boot also integrates seamlessly with the database through JPA and Hibernate and provides hooks for adding essential services like logging, tracing, and monitoring to ensure a well-rounded and maintainable application architecture">
    <img src="assets/hibernate.svg" alt="Hibernate" width="80" height="80" title="Serves as the ORM (Object-Relational Mapping) framework, facilitating seamless interaction between Java objects and the database">
    <img src="assets/postgresql.svg" alt="PostgreSQL" width="80" height="80" title="Acts as the primary relational database, offering reliability, scalability, and robust support for complex queries">
    <img src="assets/liquibase.svg" alt="Liquibase" width="80" height="80" title="Manages database schema changes through version-controlled migration scripts, ensuring consistency across environments">
    <img src="assets/docker.svg" alt="Docker" width="80" height="80" title="Provides containerization for consistent application deployment across environments, enabling scalability and portability">
    <img src="assets/nextjs.svg" alt="Docker" width="80" height="80" title="Used as the primary framework to structure the application, managing routing and integrating client-side rendering powered by React.js. Next.js facilitates seamless communication with the FlowInquiry back-end via REST APIs, ensuring a smooth data exchange and interactive user experience">
    <img src="assets/tailwind-css.svg" alt="Tailwind Css" width="80" height="80" title="Used in combination with ShadCN and FlowInquiry's custom components to deliver flexible layouts and customizable themes">
    <img src="assets/shadcn-ui.svg" alt="Shadcn" width="80" height="80" title="Serves as the foundation of the FlowInquiry UI, providing a consistent and accessible design system. All FlowInquiry components are built on top of ShadCN, ensuring a cohesive and extensible user interface across the application">
</div>

## Getting Started

FlowInquiry uses a [monorepo](https://monorepo.tools/) structure to manage all parts of the application — including the backend, frontend, and documentation — in a single repository. This approach ensures consistency, shared tooling, and easier cross-service collaboration.

All core services are located in the apps/ directory:

* apps:
  * backend: The Spring Boot service that powers the API layer, business logic, database integrations, workflows, and backend features of FlowInquiry.
  * frontend: The Next.js web application that provides the user interface for the platform. It integrates with the backend via REST APIs, handles authentication, and supports both freemium and premium features through dynamic configuration.
  * ops: the central repository that provides artifacts and configuration files to help customers deploy FlowInquiry using Docker, Kubernetes, and other environments.
  * docs: A documentation site built with a [Nextra](https://nextra.site/) static site generator, providing guides, and setup instructions for developers and users.

To get started with setting up the frontend and backend locally, follow the official developer guides:

* [Frontend Setup Guide](https://docs.flowinquiry.io/developer_guides/frontend/getting_started)

* [Backend Setup Guide](https://docs.flowinquiry.io/developer_guides/backend/getting_started)

* [Documentation Setup Guide](https://docs.flowinquiry.io/developer_guides/documentation)

These guides provide step-by-step instructions to help you configure your environment, install dependencies, and run the services in development mode.

## Deploy FlowInquiry
To ensure a smooth deployment process, we provide detailed guidelines for deploying FlowInquiry in various environments. These instructions cover setup steps, configuration details, and best practices for deploying the service effectively. You can find the deployment documentation [here](https://docs.flowinquiry.io/developer_guides/deployment)

## Stay Up-to-Date

![Follow FlowInquiry](assets/github_project_star.gif)

## Project Statistics
![Alt](https://repobeats.axiom.co/api/embed/a4b95deb1b8371f68316459561be3df65e4e0e89.svg "Repobeats analytics image")

## License
This project is licensed under the [AGPLv3](LICENSE) License.

## How to Contribute

We welcome contributions of all kinds — not just code!

You can:
- Star the project ⭐
- Share it on social media 📢
- Create a tutorial or video 🎥
- Report bugs or suggest improvements 🐛
- Submit a pull request 🛠️

Read the full guide: [How to Contribute to FlowInquiry](https://docs.flowinquiry.io/how_to_contributes/your_action_is_meaningful_to_us)


## 💪 Contributors

Thanks to all the contributors! 🙌  

[![Contributors](https://contrib.rocks/image?repo=flowinquiry/flowinquiry-server)](https://github.com/flowinquiry/flowinquiry-server/graphs/contributors)