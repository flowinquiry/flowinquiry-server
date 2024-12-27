# FlowInquiry Server
[![Build Status](https://github.com/flowinquiry/flowinquiry-server/actions/workflows/gradle.yml/badge.svg)](https://github.com/flowinquiry/flowinquiry-server/actions/workflows/gradle.yml)
![License](https://img.shields.io/badge/License-AGPLv3-blue)

## What is FlowInquiry

FlowInquiry is a service designed to streamline the management of cases, tickets, and requests for teams handling both internal and external inquiries. It bridges communication gaps across teams and ensures timely resolution of customer or interdepartmental requests. By enabling organizations to define custom workflows with tailored Service Level Agreements (SLAs) for each state, FlowInquiry ensures teams can meet deadlines and respond promptly to requests. This structured approach enhances accountability, efficiency, and satisfaction for all parties involved, fostering smoother collaboration and better outcomes.

### Problems FlowInquiry Solves with Specific Use Cases

FlowInquiry addresses several challenges faced by organizations in managing cases, tickets, and team communication. Here are some specific use cases:

**On-Call System Management**
In an on-call system, teams often face challenges in managing incoming requests or incidents, particularly when multiple shifts or team members are involved. FlowInquiry ensures that each request follows a well-defined workflow, with SLAs for escalation and resolution. This helps reduce response times, avoids missed escalations, and provides clear accountability for handling incidents.

**Case Management in CRM Applications**
CRM applications often struggle to manage customer cases effectively, especially when handling inquiries, complaints, or service requests. FlowInquiry enables teams to define custom workflows tailored to specific case types, such as refunds, escalations, or product inquiries. SLAs for each workflow stage ensure customers receive timely updates and resolutions, enhancing customer satisfaction and loyalty.

**Team Communication and Collaboration**
Effective communication within and across teams can be difficult in large organizations, especially when requests involve multiple departments or external stakeholders. FlowInquiry acts as a centralized platform where requests are logged, tracked, and routed through clearly defined workflows. This reduces miscommunication, prevents delays, and ensures all parties are aligned on priorities.

**Service Request Tracking for IT Teams**
IT teams managing internal service requests often encounter bottlenecks due to unclear processes or manual tracking. FlowInquiry allows IT departments to automate workflows for common requests such as software installation, access management, or issue resolution. The system ensures each request is assigned, processed, and resolved within agreed SLAs.

By tailoring workflows to these and other scenarios, FlowInquiry empowers teams to streamline operations, meet deadlines, and deliver exceptional service to both internal and external stakeholders.

### Screenshots

<table>
  <tr>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/team_dashboard.png">
        <img src="assets/team_dashboard_thumbnail.png" alt="Team Dashboard">
      </a>
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/team_members.png">
        <img src="assets/team_members_thumbnail.png" alt="Team Members">
      </a>
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/ticket_view.png">
        <img src="assets/ticket_view_thumbnail.png" alt="Ticket View">
      </a>
    </td>
  </tr>
  <tr>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/user_view.png">
        <img src="assets/user_view_thumbnail.png" alt="User View">
      </a>
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/workflow_customization.png">
        <img src="assets/workflow_customization_thumbnail.png" alt="Workflow Customization">
      </a>
    </td>
    <td style="padding: 10px; text-align: center;">
      <a href="assets/workspace_dashboard.png">
        <img src="assets/workspace_dashboard_thumbnail.png" alt="Workspace Dashboard">
      </a>
    </td>
  </tr>
</table>

## FlowInquiry Server

FlowInquiry Server serves as the back-end component of the FlowInquiry service, a Java-based platform designed to manage workflows and enhance team collaboration. Developed with Spring Boot, the server provides a reliable and scalable REST API to support the FlowInquiry front-end application. It also facilitates workflow management, runs scheduler programs for task automation, handles data caching for efficient performance, and ensures seamless data persistence to the database

### Technologies

* **Spring Boot:** Acts as the backbone of the back-end, orchestrating various components. It handles the creation and management of REST APIs, service layers, and controllers to facilitate business logic. Spring Boot also integrates seamlessly with the database through JPA and Hibernate and provides hooks for adding essential services like logging, tracing, and monitoring to ensure a well-rounded and maintainable application architecture.

* **Hibernate:** Serves as the ORM (Object-Relational Mapping) framework, facilitating seamless interaction between Java objects and the database.

* **PostgreSQL:** Acts as the primary relational database, offering reliability, scalability, and robust support for complex queries.

* **Liquibase:** Manages database schema changes through version-controlled migration scripts, ensuring consistency across environments.

* **MapStruct:** Simplifies object mapping by generating type-safe and efficient mappers between Java objects (e.g., DTOs and entities).

* **Docker:** Provides containerization for consistent application deployment across environments, enabling scalability and portability.


## Getting Started

### Prerequisites
* Java 21 or higher
* Gradle 8 or higher
* PostgreSQL (or compatible database)

### Setup Instructions

#### 1. Clone the repository:
```
   git clone git@github.com:flowinquiry/flowinquiry-server.git
   cd flowinquiry-server
```
#### 2. Configure application parameters

   Set up the application secrets by running the following script:
```bash
scripts/create_secrets.sh
```
This script generates the secrets for the PostgreSQL database and the JWT_BASE64_SECRET, storing them in the local .env.local file.

**Important**: For security reasons, ensure that .env.local is excluded from version control (e.g., by adding it to .gitignore).

#### 3. Build and run application

##### Run the postgres database

FlowInquiry utilizes PostgreSQL as its database and comes with pre-configured PostgreSQL settings. We recommend using Docker as a virtualized container for PostgreSQL. Docker makes it easy to test and run the same PostgreSQL version the FlowInquiry team uses daily, reducing the risk of database compatibility issues.

Ensure Docker is installed on your machine, then start the database by running the following command:
```bash
docker compose -f docker/services.yml up
```

##### Run the server

From the root folder, run the command:
```bash
./gradlew :server:bootRun
```
It may take some time before the server APIs are accessible on the default port 8080
```bash
➜  flowinquiry-server git:(main) ./gradlew :server:bootRun
INFO in org.springframework.boot.logging.logback.SpringBootJoranConfigurator@5618d5e5 - Registering current configuration as safe fallback point

______ _             _____                  _
|  ___| |           |_   _|                (_)
| |_  | | _____      _| | _ __   __ _ _   _ _ _ __ _   _
|  _| | |/ _ \ \ /\ / / || '_ \ / _` | | | | | '__| | | |
| |   | | (_) \ V  V /| || | | | (_| | |_| | | |  | |_| |
\_|   |_|\___/ \_/\_/\___/_| |_|\__, |\__,_|_|_|   \__, |
                                   | |              __/ |
                                   |_|             |___/



:: FlowInquiry 🤓  :: Running Spring Boot 3.4.0 :: Startup profile(s) dev ::
:: https://www.flowinquiry.io ::

INFO 35926 --- [  restartedMain] io.flowinquiry.FlowInquiryApp.logStarting:53 : Starting FlowInquiryApp using Java 21.0.5 with PID 35926 
DEBUG 35926 --- [  restartedMain] io.flowinquiry.FlowInquiryApp.logStarting:54 : Running with Spring Boot v3.4.0, Spring v6.2.0 
INFO 35926 --- [  restartedMain] io.flowinquiry.FlowInquiryApp.logStartupProfileInfo:658 : The following 1 profile is active: "dev" 
DEBUG 35926 --- [  restartedMain] io.flowinquiry.config.WebConfigurer.corsFilter:88 : Registering CORS filter 
```

## Deploy FlowInquiry
To ensure a smooth deployment process, we provide detailed guidelines for deploying FlowInquiry in various environments. These instructions cover setup steps, configuration details, and best practices for deploying the service effectively. You can find the deployment documentation [here](https://docs.flowinquiry.io/developer_guides/deployment)

## Related Information
- [FlowInquiry document](https://docs.flowinquiry.io): The centralized document for FlowInquiry products
- [FlowInquiry Server](https://github.com/flowinquiry/flowinquiry-server): Back-end services for FlowInquiry.
- [FlowInquiry Client](https://github.com/flowinquiry/flowinquiry-frontend): Front-end application.
- [FlowInquiry Ops](https://github.com/flowinquiry/flowinquiry-ops): Deployment and operational scripts.


## Discussions
For any inquiries about the project, including questions, proposals, or suggestions, please start a new discussion in the [Discussions](https://github.com/flowinquiry/flowinquiry-server/discussions) section. This is the best place to engage with the community and the FlowInquiry team

## License
This project is licensed under the [AGPLv3](LICENSE) License.