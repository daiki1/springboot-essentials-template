<p style="color:red;"><strong>⚠️ Work in Progress:</strong> This project is currently under development. Features and structure may change frequently until it's stable.</p>
 
# A. Spring Boot API Starter Template

Production-ready Spring Boot REST API project to kickstart any backend application with modern essentials — built with simplicity, scalability, and maintainability in mind.

⚠️ *This is a personal portfolio project. You are free to view and learn from the code. Please do not use it as-is in commercial projects.*


---

## Features

- Spring Boot 3.4.4 + Java 21
- Maven-based project structure
- MySQL Database Integration
- JWT Authentication & Role-Based Access Control
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
- Sample Data: Countries, States, Cities API with Pagination & Sorting

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
- User table will be created automatically. The first user is admin with role `ROLE_ADMIN` and password `admin123`.

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run the application

The application can be run using Maven. Make sure you have the required dependencies installed.
``` bash
mvn spring-boot:run
```

# B. While Developing
<details>
<summary>Click to open: This section includes helpful notes, practices, and important instructions to consider during development:</summary>

## Table Management
- This project uses Liquibase for managing database schema changes.
- The project could use JPA hibernate to create tables automatically during development, however, this is not recommended for production and liquidbase requires to create all the tables manually to work on production. 
- So, for development if you dont want to use liquibase, you can set the following property in `application.properties`:

``` properties
spring.jpa.hibernate.ddl-auto=update
spring.liquibase.enabled=false
```
- Remember that once in production, you need to create all the tables manually in the changelog, and set the property `spring.jpa.hibernate.ddl-auto` to `none` and `spring.liquibase.enabled` to `true`.

## File Structure Highlights
- src/main/java/.../entity: All JPA entities
- src/main/java/.../dto: DTOs to decouple API from database models
- src/main/java/.../mapper: Uses MapStruct for mapping entities <-> DTOs
- src/main/java/.../controller: API endpoints
- src/main/resources/db/changelog: Liquibase changelogs
- src/main/resources/messages_{lang}.properties: Internationalization files (i18n)

## Cors
- CORS is enabled for all origins in `CorsConfig.java`.
- You can customize it to restrict access to specific domains.

## Internationalization
- Messages are loaded from messages_en.properties, messages_es.properties, etc.
- Customize Spring messages (like validation or login errors) based on user locale.

## Testing
- Use JUnit 5 for unit and integration tests.
- Mock services and repositories where applicable.
- Add tests for critical logic (authentication, CRUD, mappers).

## Logs
- Logs are written to logs/application.log.
- Logging is configured in application.properties.
- You can adjust levels (INFO, DEBUG, ERROR) as needed.

## Swagger / API Docs
- API documentation is generated using SpringDoc OpenAPI UI.
- Access via: http://localhost:8080/swagger-ui.html (or /swagger-ui/index.html)

## Using Profiles

### Application uses Spring profiles:
- dev (default): for local development
- prod: for production environment

### Set active profile using:
```bash
--spring.profiles.active=dev
```
```bash
--spring.profiles.active=prod
```
</details>

# C. Once in Production
<details>
<summary>Click to open: This section includes helpful notes, practices, and important instructions to consider once the application is in production.</summary>

## Set active profile to prod
- Set the active profile to `prod` in your production environment.
- This will ensure that the application uses production-specific configurations.
- Change the database URL, username, and password in `application-prod.properties` to point to your production database.

```bash
--spring.profiles.active=prod
```

## Avoid using JPA hibernate to create tables automatically
- In production, you should not use JPA hibernate to create tables automatically.
- Instead, use Liquibase to manage your database schema.
- Make sure to create all the tables manually in the changelog.
- Using ddl-auto=true in production can lead to data loss or corruption.

## Avoid using default passwords
- Do not use default passwords in production.

## Avoid using "*" for CORS
- In production, you should restrict CORS to specific domains.
- Using "*" allows any domain to access your API, which can be a security risk.
- Update the CORS configuration CorsConfig.java to allow only trusted domains.


</details>
