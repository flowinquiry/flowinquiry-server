# FlowInquiry DevOps

FlowInquiry Ops provides the deployment and operational setup for FlowInquiry, a request and workflow management platform. This repository simplifies deployment for local environments and outlines plans for future scalability and flexibility.

## Current Features
Local Deployment: Quickly set up FlowInquiry using Docker Compose for development or small-scale testing.


## Planned Features
We aim to expand the deployment capabilities of FlowInquiry Ops to include:

* **Cloud-Ready Deployments:** Support for major cloud platforms, including AWS, Azure, and GCP, leveraging managed Kubernetes services.

* **On-Premises Support:** Compatibility with Kubernetes clusters in customer-managed data centers.

* **Scalable Architecture:** Tools and configurations for seamless scaling across large infrastructures.

* **Customizable Configurations:** Enhanced support for environment-specific setups to simplify integration with diverse infrastructure.

## Getting Started

### Prerequisites
* Docker

### Local Deployment with Docker Compose

1. Clone the repository:

```bash
git clone https://github.com/your-org/flowinquiry-ops.git
cd flowinquiry-ops/flowinquiry-docker
```

2. Set up the pre-defined environment variables

```bash
scripts/all.sh
```

Fill all inputs
```bash
➜  flowinquiry-docker git ✗ scripts/all.sh
Running frontend_config.sh...
Environment variables have been written to .frontend.env
frontend_config.sh succeeded.
Running backend_create_secrets.sh...
Enter your database password: 
Sensitive data has been written to ./.backend.env with restricted permissions.
backend_create_secrets.sh succeeded.
Running backend_mail_config.sh...
Enter your SMTP host: smtp.google.com
Enter your SMTP port: 587
Enter your username: <your_email>
Enter your password: Does SMTP require STARTTLS (y/n)? y
Please enter the email address that will be used as the sender for outgoing emails: noreply@flowinquiry.io
Please enter the base URL that will be used for the email template: https://flowinquiry.io
Configuration has been saved to .env.local
backend_mail_config.sh succeeded.
```

3. Start the services:

```bash
docker compose -f services.yml up
```

3. Access the application

Open your browser and navigate to https://localhost. FlowInquiry uses Caddy to automatically generate an SSL certificate. If desired, you can configure a custom DNS name to replace `localhost`

## Related Information

- [FlowInquiry document](https://docs.flowinquiry.io): The centralized document for FlowInquiry products
- [FlowInquiry Server](https://github.com/flowinquiry/flowinquiry-server): Back-end services for FlowInquiry.
- [FlowInquiry Client](https://github.com/flowinquiry/flowinquiry-frontend): Front-end application.
- [FlowInquiry Ops](https://github.com/flowinquiry/flowinquiry-ops): Deployment and operational scripts.


## Discussions

For any inquiries about the project, including questions, proposals, or suggestions, please start a new discussion in the [Discussions](https://github.com/flowinquiry/flowinquiry-ops/discussions) section. This is the best place to engage with the community and the FlowInquiry team

## License
This project is licensed under the [AGPLv3](LICENSE) License.