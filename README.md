# Expense Manager API

A RESTful backend for tracking personal expenses, built with **Spring Boot 4**, **Java 25** and **PostgreSQL**. It supports full CRUD on expenses, filtering by category and date range, and a monthly spending summary broken down by category.

> Built as a learning project to practise clean layered architecture, validation, error handling and JPA query design in a modern Spring stack.

## Features

- Create, read, update and delete expenses
- Filter expenses by **category** and/or **date range** (all filters optional and combinable)
- **Monthly summary**: total spend plus per-category totals for any month
- Input validation with clear, structured error responses
- Consistent error format using **RFC 7807 Problem Details**
- Money stored as `BigDecimal` (`NUMERIC(12,2)`), never floating point

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.0 (Spring MVC) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Build | Maven (wrapper included) |
| Boilerplate reduction | Lombok |

## Architecture

The code follows a classic layered design under `org.example.expensemanager`:

```
controller  ->  service  ->  repository  ->  PostgreSQL
   (HTTP)     (business)      (data)
```

| Package | Responsibility |
|---|---|
| `controller` | HTTP only. Accepts and returns DTOs, never entities. |
| `dto` | Immutable Java `record`s for requests/responses (`ExpenseRequest`, `ExpenseResponse`, `SummaryResponse`). |
| `service` | Business logic and transaction boundaries. Class-level `@Transactional`, with read-only transactions for queries. |
| `repository` | Spring Data JPA repository with custom JPQL for filtering and aggregation. |
| `model` | JPA entity (`Expense`) and the `Category` enum. |
| `exception` | `ExpenseNotFoundException` and a `@RestControllerAdvice` that maps errors to HTTP responses. |

### Design decisions worth noting

- **DTOs instead of exposing entities**, so the API contract is decoupled from the database schema.
- **Dirty checking for updates**: `update` modifies the managed entity inside a transaction instead of calling `save`, letting Hibernate flush only what changed.
- **Single flexible search query**: one JPQL query with optional parameters (`:param is null or ...`) handles every filter combination, including an explicit `cast(:from as date)` so PostgreSQL can type null date parameters.
- **Interface projection for aggregation**: the monthly summary uses a `GROUP BY` query returning a projection (`CategoryTotal`), so totals are computed in the database, not in Java.
- **Centralised error handling**: `404` for missing expenses and `400` with a field-by-field `errors` map for validation failures, both as `ProblemDetail` JSON.

## API Reference

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/expenses` | Create an expense (`201 Created`) |
| `GET` | `/api/expenses` | List expenses; optional `category`, `from`, `to` filters |
| `GET` | `/api/expenses/{id}` | Get one expense (`404` if missing) |
| `PUT` | `/api/expenses/{id}` | Replace an expense (`404` if missing) |
| `DELETE` | `/api/expenses/{id}` | Delete an expense (`204 No Content`) |
| `GET` | `/api/expenses/summary?month=yyyy-MM` | Total and per-category totals for a month |
| `GET` | `/api/categories` | List the available categories |

**Categories:** `FOOD`, `TRANSPORT`, `RENT`, `UTILITIES`, `ENTERTAINMENT`, `HEALTH`, `OTHER`

### Example requests

Create an expense:

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"description": "Lunch", "amount": 12.50, "date": "2025-01-15", "category": "FOOD"}'
```

```json
{
  "id": 1,
  "description": "Lunch",
  "amount": 12.50,
  "date": "2025-01-15",
  "category": "FOOD"
}
```

Filter by category and date range:

```bash
curl "http://localhost:8080/api/expenses?category=FOOD&from=2025-01-01&to=2025-01-31"
```

Monthly summary:

```bash
curl "http://localhost:8080/api/expenses/summary?month=2025-01"
```

Validation failure (`400`) returns a Problem Detail with per-field messages:

```json
{
  "title": "Bad Request",
  "status": 400,
  "errors": {
    "amount": "must be greater than 0.00",
    "description": "must not be blank"
  }
}
```

### Validation rules

| Field | Rule |
|---|---|
| `description` | required, not blank |
| `amount` | required, greater than 0 |
| `date` | required, ISO format `yyyy-MM-dd` |
| `category` | required, must be one of the categories above |

## Getting Started

### Prerequisites

- JDK 25
- PostgreSQL running locally on port `5432`

### Setup

1. Create the database:

   ```bash
   createdb expense_manager_db
   ```

2. Check the connection settings in `src/main/resources/application.properties`. By default the app connects to `jdbc:postgresql://localhost:5432/expense_manager_db` with no username or password. Add `spring.datasource.username` and `spring.datasource.password` if your setup needs them.

3. Run the app (tables are created automatically on first start):

   ```bash
   ./mvnw spring-boot:run
   ```

The API is now available at `http://localhost:8080`.

### Useful commands

```bash
./mvnw test                              # run all tests
./mvnw test -Dtest=ClassName#methodName  # run a single test
./mvnw -q -DskipTests compile            # compile only
```

## Project Structure

```
src/main/java/org/example/expensemanager/
├── ExpenseManagerApplication.java
├── controller/   ExpenseController, CategoryController
├── dto/          ExpenseRequest, ExpenseResponse, SummaryResponse
├── exception/    ExpenseNotFoundException, GlobalExceptionHandler
├── model/        Expense, Category
├── repository/   ExpenseRepository
└── service/      ExpenseService
```

## Roadmap

- [ ] Automated tests (unit tests for the service layer, integration tests for the API)
- [ ] Database migrations with Flyway (replacing Hibernate `ddl-auto=update`)
- [ ] Authentication and per-user expenses
- [ ] AI-powered automatic expense categorisation using Spring AI and an LLM
- [ ] Auto-capture of UPI transactions
- [ ] Containerisation and CI pipeline

## Author

Built by Shardool Pandey.
