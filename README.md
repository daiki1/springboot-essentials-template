<p style="color:red;"><strong>⚠️ Work in Progress:</strong> This project is currently under development. Features and structure may change frequently until it's stable.</p>
 
# Spring Boot API Starter Template

Production-ready Spring Boot REST API project to kickstart any backend application with modern essentials — built with simplicity, scalability, and maintainability in mind.

⚠️ *This is a personal portfolio project. You are free to view and learn from the code. Please do not use it as-is in commercial projects.*

---

## Table of Contents
- [Features](#features)
- [Tools & Technologies](#tools--technologies)
- [Getting Started](#getting-started)
- [Development Notes](#developing-notes)
- [Production Considerations](#production-considerations)

## Features

- Spring Boot 3.4.4 + Java 21
- Maven-based project structure
- MySQL Database Integration
- JWT Authentication & Role-Based Access Control
- Configurable one session per user or multiple sessions 
- Configurable session management: single or multiple logins per user in `application.properties` 
- DTO Mapping with MapStruct
- RESTful API with CRUD examples
- Auditing for all database operations (create, update, delete, login)
- Internationalization (i18n) with English and Spanish messages
- Liquibase for database versioning/migrations
- API Documentation with Swagger / Springdoc OpenAPI
- Centralized Exception Handling
- Custom Logging (`application.log`)
- Environment-based configuration: `dev`, `prod`
- Unit & Integration Tests (JUnit, Mockito)
- Clean Architecture: Controller, Service, Repository, DTO, Mapper
- Sample Data: Countries, States, Cities API with Pagination & Sorting (Importing this data is optional and may take a while)

---

## Tools & Technologies

- **IDE**: IntelliJ IDEA
- **Java**: Java JDK 21
- **Plugins**:
  - **Lombok**: For reducing boilerplate code.
  - **MapStruct**: For mapping between DTOs and entities.
  - **Spring Boot**: Framework for building the backend API.
  - **Maven**: Build tool for dependency management and project lifecycle.
  - **Swagger (Springdoc OpenAPI)**: API documentation and UI.
- **Database**: MySQL
- **Other**:
  - **Liquibase**: For database migration/versioning
  - **Spring Security**: Authentication & Authorization
  - **JWT**: Token-based security
  - **JPA (Hibernate)**: ORM
  - **JUnit / Mockito**: Testing

---

## Acknowledgements

- This project was developed with the assistance of [ChatGPT](https://chat.openai.com) for guidance on various aspects such as project setup, configuration, and best practices.
- This project uses the **[Countries States Cities Database by Dr5hn](https://github.com/dr5hn/countries-states-cities-database)** for country, state, and city data.
- **GitHub Copilot** was used for code suggestions and assistance during development.

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8+
- Git

### 1. Clone the Repository

``` bash
git clone https://github.com/daiki1/springboot-essentials-template.git
cd springboot-essentials-template
```

### 2. Configure the Database
- Database will be created automatically if it does not exist.
- Update the `application.properties` file with your database credentials.
- User table will be created automatically. The first user is admin with role `ROLE_ADMIN` and no password.
- Use the reset password endpoint to set the password for the first user. Use: `/api/auth/request-password-reset` check the token generated in `password_reset_tokens` table, and use `/api/auth/reset-password` to set the password. 
``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configurable session login for users
- You can configure the session login in `application.properties` file.
- By default, the application is configured to allow only one session per user. If you want to allow multiple sessions, set the following property to `false`:
- When login in with a new session, the old session will be invalidated if the following property is `true`
``` properties
app.oneSingleSignOn=false
```

### 4. Run the application

The application can be run using Maven. Make sure you have the required dependencies installed.
``` bash
mvn spring-boot:run
```
### 5. Refresh token
- The application uses JWT for authentication. The token is valid for 15 minutes by default.
- You can refresh the token using the `/api/auth/refresh` endpoint.
- The refresh token is valid for 7 days by default.
- You can change the expiration time in `application.properties` file.
``` properties
app.jwtExpirationInMs=900000
app.jwtRefreshExpirationMs=604800000
```
- Everytime the token expires, call the `/api/auth/refresh` endpoint to get a new token using the refresh token.
- If the refresh token expires, you will need to log in again to get a new access token and refresh token.

## Developing Notes
<details>
<summary>Click to open: This section includes helpful notes, practices, and important instructions to consider during development:</summary>

### Unit tests
- Use the command below to run all unit tests:
``` bash
mvn test
```

### User for testing
- The first user is admin with role `ROLE_ADMIN` and password `admin`.

### Table Management
- This project uses Liquibase for managing database schema changes.
- JPA Hibernate can create tables automatically during development, but this is not recommended for production. 
- So, for development if you don't want to use liquibase, you can set the following property in `application.properties`:

``` properties
spring.jpa.hibernate.ddl-auto=update
spring.liquibase.enabled=false
```
- Remember that once in production, you need to create all the tables manually in the changelog, and set the property `spring.jpa.hibernate.ddl-auto` to `none` and `spring.liquibase.enabled` to `true`.

### File Structure Highlights
- src/main/java/.../entity: All JPA entities
- src/main/java/.../dto: DTOs to decouple API from database models
- src/main/java/.../mapper: Uses MapStruct for mapping entities <-> DTOs
- src/main/java/.../controller: API endpoints
- src/main/resources/db/changelog: Liquibase changelogs
- src/main/resources/messages_{lang}.properties: Internationalization files (i18n)

### Cors
- CORS is enabled for all origins in `CorsConfig.java`.
- You can customize it to restrict access to specific domains.

### Internationalization
- Messages are loaded from messages_en.properties, messages_es.properties, etc.
- Customize Spring messages (like validation or login errors) based on user locale.

### Testing
- Use JUnit 5 for unit and integration tests.
- Mock services and repositories where applicable.
- Add tests for critical logic (authentication, CRUD, mappers).

### Logs
- Logs are written to logs/application.log.
- Logging is configured in application.properties.
- You can adjust levels (INFO, DEBUG, ERROR) as needed.

### Swagger / API Docs
- API documentation is generated using SpringDoc OpenAPI UI.
- Access via: http://localhost:8080/swagger-ui.html (or /swagger-ui/index.html)
- With the login get a token and use it in the Swagger UI to test the endpoints, press the "Authorize" button and enter the token in the input field.
- You can enable/disable the Swagger UI in production by setting the following property in `application-prod.properties`:
``` properties
springdoc.api-docs.enabled=false
```

### Using Profiles

#### Application uses Spring profiles:
- dev (default): for local development
- prod: for production environment

#### Set active profile using:
```bash
--spring.profiles.active=dev
```
```bash
--spring.profiles.active=prod
```
</details>

## Production Considerations
<details>
<summary>Click to open: This section includes helpful notes, practices, and important instructions to consider once the application is in production.</summary>

### Set active profile to prod
- Set the active profile to `prod` in your production environment.
- This will ensure that the application uses production-specific configurations.
- Change the database URL, username, and password in `application-prod.properties` to point to your production database.

```bash
--spring.profiles.active=prod
```

### Avoid using JPA hibernate to create tables automatically
- In production, you should not use JPA hibernate to create tables automatically.
- Instead, use Liquibase to manage your database schema.
- Make sure to create all the tables manually in the changelog.
- Using ddl-auto=true in production can lead to data loss or corruption.

### Avoid using default passwords
- Do not use default passwords in production.

### Avoid using "*" for CORS
- In production, you should restrict CORS to specific domains.
- Using "*" allows any domain to access your API, which can be a security risk.
- Update the CORS configuration CorsConfig.java to allow only trusted domains.


</details>
