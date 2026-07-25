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

Added orders related to customers, restaurants, and payments, with lifecycle statuses from placement through delivery and rejection. Added order APIs, validation, mapping, tests, and the order migration.

Order items, stock mutation, payment atomicity, partner assignment, and asynchronous notifications were intentionally deferred.

### Iteration 10 — Order items

Added order items related to orders and menu items. Each item stores its quantity and unit-price snapshot, validates that the menu item belongs to the order's restaurant, and prevents duplicate menu items within one order at the database level.

Stock decrement, oversell prevention, and concurrent order coordination remain future work.

## Verification Approach

Each implementation iteration was kept narrowly scoped and verified with the Gradle test suite:

```bash
./gradlew test
```

The project uses constructor injection, DTO-only controller contracts, Jakarta Validation, service-layer business logic, repository-only persistence access, Flyway migrations, and global exception handling throughout the implemented slices.
