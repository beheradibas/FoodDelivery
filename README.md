# Food Delivery Order Management System

Spring Boot backend for a food delivery order management system, built incrementally.

## Technology

- Java 21 and Spring Boot 3.x
- Gradle Wrapper, Spring Web, Spring Data JPA, Hibernate
- PostgreSQL and Flyway
- Spring Security and JWT dependencies (security implementation pending)
- Jakarta Validation, Lombok, JUnit 5, Mockito
- Swagger / OpenAPI

## Package structure

```text
com.fooddelivery
├── config       # Framework configuration
├── controller   # HTTP endpoints (future iterations)
├── dto          # Request and response DTOs (future iterations)
├── entity       # JPA entities (future iterations)
├── exception    # Global error handling
├── mapper       # DTO mappers (future iterations)
├── repository   # Persistence access (future iterations)
├── security     # JWT components (future iterations)
└── service      # Business logic (future iterations)
```

## Configuration

The default datasource points to a local PostgreSQL database named `food_delivery`.
Override it with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`.

Flyway migrations belong in `src/main/resources/db/migration` and Hibernate validates the schema; it does not generate it.

## Run

```bash
./gradlew bootRun
```

## API documentation

After starting the application, Swagger UI is available at `/swagger-ui.html` and the OpenAPI document at `/v3/api-docs`.
