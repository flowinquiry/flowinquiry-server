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