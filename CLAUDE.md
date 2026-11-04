# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Expense Manager: an API-only Spring Boot 4.0.0 backend (Java 25, Maven, Postgres via Spring Data JPA). It is a learning project; the user wants each part explained as it is written. No auth yet (planned later), and Spring AI auto-categorization of expenses via an LLM is planned for a later stage.

## Commands

```bash
./mvnw spring-boot:run                      # run on :8080
./mvnw test                                 # all tests
./mvnw test -Dtest=ClassName#methodName     # single test
./mvnw -q -DskipTests compile               # compile only
```

Requires a local Postgres with database `expense_manager_db` on `localhost:5432`. The datasource has no username/password configured (the local connection needs none). There is no Docker on this machine, so Testcontainers is not an option.

## Architecture

Layered under `org.example.expensemanager`: `controller` -> `service` -> `repository` -> Postgres.

- Controllers only handle HTTP and exchange DTO records (`dto/`), never entities. `ExpenseResponse.from(Expense)` is the entity-to-DTO mapping.
- `ExpenseService` is class-level `@Transactional` (read-only on queries) and owns the 404 behavior via `ExpenseNotFoundException`. `update` mutates the managed entity, relying on dirty checking rather than calling `save`.
- `ExpenseRepository` has two `@Query` methods: `search` (optional category/from/to filters using `:param is null or ...`, with `cast(:from as date)` needed so Postgres can type null date params) and `totalsByCategory` (a projection interface, `CategoryTotal`, for the monthly summary).
- `GlobalExceptionHandler` returns RFC 7807 `ProblemDetail` JSON: 404 for not found, 400 with an `errors` field map for bean validation failures.
- Schema is created by Hibernate (`ddl-auto=update`); there are no migrations yet (Flyway is planned).
- `Category` is a fixed enum stored as a string; `GET /api/categories` just returns its values.
- Lombok is used (annotation processor configured in `pom.xml`).

## API

`/api/expenses` (POST, GET with `category`/`from`/`to` filters), `/api/expenses/{id}` (GET, PUT, DELETE), `/api/expenses/summary?month=yyyy-MM`, `/api/categories`. Tested manually with Postman; no automated tests exist yet beyond the default context-load test.
