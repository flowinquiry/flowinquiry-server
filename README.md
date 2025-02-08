# FlowInquiry Server
[![Build Status](https://github.com/flowinquiry/flowinquiry-server/actions/workflows/gradle.yml/badge.svg)](https://github.com/flowinquiry/flowinquiry-server/actions/workflows/gradle.yml)
![License](https://img.shields.io/badge/License-AGPLv3-blue)

## What is FlowInquiry

FlowInquiry is a service designed to streamline the management of cases, tickets, and requests for teams handling both internal and external inquiries. It bridges communication gaps across teams and ensures timely resolution of customer or interdepartmental requests. By enabling organizations to define custom workflows with tailored Service Level Agreements (SLAs) for each state, FlowInquiry ensures teams can meet deadlines and respond promptly to requests. This structured approach enhances accountability, efficiency, and satisfaction for all parties involved, fostering smoother collaboration and better outcomes.

![FlowInquiry](assets/flowinquiry_slide.gif)

### Problems FlowInquiry Solves with Specific Use Cases

FlowInquiry addresses several challenges faced by organizations in managing cases, tickets, and team communication. Here are some specific use cases:

* **On-Call System Management** In an on-call system, teams often face challenges in managing incoming requests or incidents, particularly when multiple shifts or team members are involved. FlowInquiry ensures that each request follows a well-defined workflow, with SLAs for escalation and resolution. This helps reduce response times, avoids missed escalations, and provides clear accountability for handling incidents.

* **Case Management in CRM Applications**  CRM applications often struggle to manage customer cases effectively, especially when handling inquiries, complaints, or service requests. FlowInquiry enables teams to define custom workflows tailored to specific case types, such as refunds, escalations, or product inquiries. SLAs for each workflow stage ensure customers receive timely updates and resolutions, enhancing customer satisfaction and loyalty.

* **Team Communication and Collaboration** Effective communication within and across teams can be difficult in large organizations, especially when requests involve multiple departments or external stakeholders. FlowInquiry acts as a centralized platform where requests are logged, tracked, and routed through clearly defined workflows. This reduces miscommunication, prevents delays, and ensures all parties are aligned on priorities.

* **Service Request Tracking for IT Teams** IT teams managing internal service requests often encounter bottlenecks due to unclear processes or manual tracking. FlowInquiry allows IT departments to automate workflows for common requests such as software installation, access management, or issue resolution. The system ensures each request is assigned, processed, and resolved within agreed SLAs.

* **Project management** Teams can use FlowInquiry as a project management tool to create, assign, and prioritize tasks, track progress with workflows, and ensure timely completion with SLAs and automation. Its collaboration features, real-time updates, and integration capabilities streamline workflows and boost productivity.

By tailoring workflows to these and other scenarios, FlowInquiry empowers teams to streamline operations, meet deadlines, and deliver exceptional service to both internal and external stakeholders.


## FlowInquiry Server

FlowInquiry Server serves as the back-end component of the FlowInquiry service, a Java-based platform designed to manage workflows and enhance team collaboration. Developed with Spring Boot, the server provides a reliable and scalable REST API to support the FlowInquiry front-end application. It also facilitates workflow management, runs scheduler programs for task automation, handles data caching for efficient performance, and ensures seamless data persistence to the database

### Built With

<div style="display: flex; justify-content: left; gap: 20px; align-items: center;">
    <img src="assets/spring-boot.svg" alt="Spring Boot" width="80" height="80" title="Acts as the backbone of the back-end, orchestrating various components. It handles the creation and management of REST APIs, service layers, and controllers to facilitate business logic. Spring Boot also integrates seamlessly with the database through JPA and Hibernate and provides hooks for adding essential services like logging, tracing, and monitoring to ensure a well-rounded and maintainable application architecture">
    <img src="assets/hibernate.svg" alt="Hibernate" width="80" height="80" title="Serves as the ORM (Object-Relational Mapping) framework, facilitating seamless interaction between Java objects and the database">
    <img src="assets/postgresql.svg" alt="PostgreSQL" width="80" height="80" title="Acts as the primary relational database, offering reliability, scalability, and robust support for complex queries">
    <img src="assets/liquibase.svg" alt="Liquibase" width="80" height="80" title="Manages database schema changes through version-controlled migration scripts, ensuring consistency across environments">
    <img src="assets/docker.svg" alt="Docker" width="80" height="80" title="Provides containerization for consistent application deployment across environments, enabling scalability and portability">
</div>

### Getting Started

#### Prerequisites
* Java 21 or higher
* PostgreSQL (or compatible database)

#### Setup Instructions

##### 1. Clone the repository:
```
   git clone git@github.com:flowinquiry/flowinquiry-server.git
   cd flowinquiry-server
```
#### 2. Configure application parameters

To run the FlowInquiry backend, users need to provide their input in the local configuration file .env.local. FlowInquiry offers two options for creating this configuration file:

* Using Bash Scripts: Users can run provided Bash scripts, which prompt for inputs, validate the information, and then automatically generate the configuration file.
* Editing a Template Manually: A template configuration file (.env.local.example) is provided, allowing users to manually edit and create their .env.local file. This option is recommended for Windows users who cannot run Bash scripts.

##### 2.1 Creating config file using script

Set up the application secrets by running the following script:
```bash
scripts/create_secrets.sh
```
This script generates the secrets for the PostgreSQL database and the JWT_BASE64_SECRET, storing them in the local .env.local file.

**Important**: For security reasons, ensure that .env.local is excluded from version control (e.g., by adding it to .gitignore).

##### 2.2 Edit config file manually

Rename the file [.env.local.example](./.env.local.example) to .env.local, and edit the file content per instructions inline

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