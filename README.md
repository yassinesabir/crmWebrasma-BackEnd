# RankinDigital Mini CRM Application

This is a mini CRM application developed for RankinDigital and is participating in the EMSI Summer IT competition. The application is built using Spring Boot and integrates with Keycloak for authentication, PostgreSQL for database management, and SendGrid for sending emails.

## Features

- **Authentication**: Integrated with Keycloak for secure authentication.
- **Database**: Uses PostgreSQL for persistent storage.
- **Email**: Sends emails using SendGrid.

## Prerequisites

Ensure you have the following tools installed:

- Java 17 or later
- Maven 3.8 or later
- Docker (for running PostgreSQL)
- Keycloak server instance (for development, run using `kc.bat start-dev`)

## Configuration

### Keycloak

Keycloak is used for authentication. We use two Keycloak clients:

1. **Backend Client**: For server-side authentication.
2. 
For the backend client configuration in Keycloak, refer to the screenshots below for detailed setup instructions.

![Backend Client Configuration Screenshot](path/to/backend-client-configuration-screenshot.png)

### PostgreSQL

To use PostgreSQL, configure your `application.properties` file with the following details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
spring.jpa.hibernate.ddl-auto=update
```
```SMTP server
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=your_sendgrid_api_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true



