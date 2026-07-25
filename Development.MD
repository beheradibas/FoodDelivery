# Development History

## Problem Space

This project is a food-delivery order management system designed to support multiple restaurants across multiple cities. It will eventually cover restaurant and menu management, customer ordering, payment, the order lifecycle, delivery-partner assignment, stock safety under concurrent orders, asynchronous status notifications, and post-delivery ratings and reviews.

The main roles are:

- Admin: manages cities, restaurants, restaurant owners, and delivery partners.
- Restaurant owner: manages a restaurant's menu and order handling.
- Customer: browses menus, places orders, tracks delivery, and submits ratings.
- Delivery partner: accepts assignments and updates delivery status.

The system is being built incrementally so each domain capability can be reviewed and verified before the next one is introduced.

## Iterative Development

### Iteration 1 — Project skeleton

Created the Spring Boot foundation and switched the build to the Gradle Wrapper. Added the package structure, application configuration, PostgreSQL/Flyway configuration, Swagger/OpenAPI setup, global exception handling, a temporary permit-all security configuration, JWT dependencies for future use, and project documentation.

No domain entities or business endpoints were created in this iteration.

### Iteration 2 — User management

Added the `User` entity, repository, DTOs, service, controller, validation, password encoding, CRUD APIs, and the first Flyway migration. User responses deliberately omit passwords.

### Iteration 3 — Restaurant-owner role

Extended the role model with `RESTAURANT_OWNER` and added focused test coverage. No unrelated user fields or security rules were introduced.

### Iteration 4 — Restaurant management

Added restaurants with a city field, restaurant-owner relationship, admin CRUD APIs, owner-scoped view APIs, DTO mapping, validation, tests, and the restaurant migration.

### Iteration 5 — Menu items

Added menu items related to restaurants. Menu items include price, availability, and stock quantity. Added owner CRUD APIs, customer menu-view APIs, validation, mapping, tests, and the menu-item migration.

Stock reservation and concurrency control were intentionally deferred to the order-placement work.

### Iteration 6 — Customer addresses

Added customer-owned address records, customer-scoped CRUD APIs, DTOs, mapping, validation, tests, and the address migration.

### Iteration 7 — Delivery partners

Added delivery-partner profiles linked to `DELIVERY_PARTNER` users, including operating city, admin CRUD APIs, validation, mapping, tests, and the delivery-partner migration.

Assignment and delivery-status workflows were intentionally deferred.

### Iteration 8 — Payments

Added standalone payments linked to customers, payment status values (`PENDING`, `SUCCESS`, and `FAILED`), payment APIs, validation, mapping, tests, and the payment migration.

Payment processing integrations and order integration were intentionally excluded at this stage.

### Iteration 9 — Orders

Added orders related to customers, restaurants, and payments, with lifecycle statuses from placement through delivery. Added order APIs, validation, mapping, tests, and the order migration.

Order items, stock mutation, payment atomicity, partner assignment, and asynchronous notifications were intentionally deferred.

### Iteration 10 — Order items

Added order items related to orders and menu items. Each item stores its quantity and unit-price snapshot, validates that the menu item belongs to the order's restaurant, and prevents duplicate menu items within one order at the database level.

Stock decrement, oversell prevention, and concurrent order coordination remain future work.

### Iteration 12 — Order lifecycle validation

Restricted order status changes to the valid lifecycle sequence `PLACED → ACCEPTED → PREPARING → OUT_FOR_DELIVERY → DELIVERED`. Restaurant rejection is represented by terminal `REJECTED`, and customer cancellation by terminal `CANCELLED`; both are allowed from `PLACED`, `ACCEPTED`, and `PREPARING` only. Skipped, repeated, backward, delivery-stage rejection, and delivery-stage cancellation transitions are rejected with validation errors. Added parameterized unit coverage for both outcomes.

### Iteration 13 — Delivery assignment

Added one-to-one order assignment with a delivery partner. The order stores an optimistic-lock version, the assignment has a unique order constraint, and concurrent assignment conflicts are converted into a resource conflict instead of allowing two partners to claim the same order. Added assignment APIs, DTOs, mapping, Flyway migration, and unit tests for first assignment, duplicate assignment, and optimistic-lock conflict handling.

### Iteration 14 — Asynchronous order notifications

Added a Spring `OrderStatusChangedEvent` published when a valid order status transition occurs. Customer, restaurant, and assigned delivery-partner listeners consume the event with `@Async`, using a dedicated notification executor so notification dispatch does not run on the request thread. The notification service is transport-neutral and currently provides the integration boundary for real channels. Added listener tests, including the unassigned-order case.

### Iteration 15 — Ratings and reviews

Added customer ratings and reviews linked to delivered orders. The service verifies that the order is delivered, belongs to the requesting customer, and has not already been reviewed by that customer. Added validation, DTO mapping, review APIs, Flyway migration, and unit tests for successful and rejected review attempts.

### Iteration 16 — JWT authentication and authorization

Added customer registration, login, signed JWT issuance and validation, stateless Spring Security configuration, and role-based endpoint authorization for admins, restaurant owners, customers, and delivery partners. Passwords continue to be stored only as encoded values, and public access is limited to authentication and API documentation endpoints. Added JWT and authentication service tests.

Role authorization is enforced with controller-level `@PreAuthorize` annotations. The security configuration now focuses on JWT filtering, stateless sessions, and public endpoint exceptions, while ownership and resource-level checks remain in services.

### Iteration 11 — Order placement integration

Added a transactional order-placement workflow that accepts a customer, restaurant, payment reference, and requested menu items in one request. The service validates the customer, restaurant, item ownership, item availability, duplicate items, and payment-reference uniqueness; locks menu rows for update; deducts stock; calculates the payment total; creates the payment; creates the order; and creates all order items.

The entire workflow runs in one transaction, so a validation or persistence failure rolls back stock changes, payment creation, order creation, and order-item creation together. Menu items now also carry a JPA version for optimistic version tracking, while placement uses pessimistic row locking to serialize stock deductions for the same item.

Added placement DTOs, response mapping, a placement endpoint, unit tests, and Spring integration tests using the shared H2 test profile in `src/test/resources/application-test.yml`. `AbstractIntegrationTest` activates that profile for all future integration tests. Order-item stock reservation and payment processing remain separate from this workflow's external integrations.

## Verification Approach

Each implementation iteration was kept narrowly scoped and verified with the Gradle test suite:

```bash
./gradlew test
```

The project uses constructor injection, DTO-only controller contracts, Jakarta Validation, service-layer business logic, repository-only persistence access, Flyway migrations, and global exception handling throughout the implemented slices.

## Design Decisions

### Production database and test database

Production uses PostgreSQL with Flyway-managed migrations. H2 is used only for automated integration tests through the `test` profile in `src/test/resources/application-test.yml`.

This keeps the production runtime aligned with the required PostgreSQL deployment while allowing integration tests to run locally without Docker or an externally managed database. The H2 test URL runs in PostgreSQL compatibility mode, and the same Flyway migrations are applied during the test context. PostgreSQL-specific behavior should still be verified separately when database-specific functionality is introduced.
