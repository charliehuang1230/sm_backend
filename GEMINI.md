# Project: Todolist (Dynamic DB Backend)

## Overview
This is a Spring Boot application designed to act as a backend service with a unique "Dynamic Data Source" capability. While named `todolist`, the domain model reflects a comprehensive e-commerce or ERP system (Users, Orders, Products, Inventory, etc.).

The core functionality allows the application to dynamically connect to external databases (MySQL, PostgreSQL) at runtime via API requests. Clients can establish a connection, receive a session ID, and then perform queries against that specific database connection without restarting the service.

## Tech Stack
*   **Language:** Java 17
*   **Framework:** Spring Boot 3.3.3
*   **Build Tool:** Gradle
*   **ORM:** MyBatis-Plus 3.5.7
*   **Database:**
    *   **Primary:** MySQL (default connection)
    *   **Dynamic Support:** MySQL, PostgreSQL (drivers included)
*   **Utilities:** Lombok

## Key Features
*   **Dynamic Database Connection:** Connect to arbitrary databases at runtime using the `/api/db/connect` endpoint.
*   **Session Management:** Each dynamic connection is identified by a UUID.
*   **Commerce Domain Model:** Pre-defined entities and APIs for:
    *   App Users (`AppUser`)
    *   Customers (`Customer`)
    *   Products & Categories (`Product`, `Category`)
    *   Orders & Order Items (`OrderEntity`, `OrderItem`)
    *   Inventory Movements (`InventoryMovement`)
    *   Payments & Returns (`Payment`, `ReturnEntry`)

## Project Structure
*   `src/main/java/com/demo/todolist/`
    *   `config/`: Configuration for Dynamic Data Sources (`DynamicRoutingDataSource`, `DynamicDataSourceConfig`).
    *   `controller/`: REST controllers exposing the dynamic DB API (`DynamicDbController`, `DynamicDbQueryController`).
    *   `dto/`: Data Transfer Objects for API requests/responses (Connect requests, Query requests).
    *   `entity/`: JPA/MyBatis entities representing the database tables.
    *   `mapper/`: MyBatis mappings for database interaction.
    *   `service/`: Business logic for managing dynamic connections and executing queries (`DynamicDataSourceRegistry`, `DynamicCommerceService`).

## Getting Started

### Prerequisites
*   JDK 17
*   MySQL Database (for the default connection)

### Configuration
1.  Verify database settings in `src/main/resources/application.yaml`.
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/sm_db
        username: root
        password: 123456
    ```

### Building and Running
*   **Build:** `./gradlew build`
*   **Run:** `./gradlew bootRun`
*   **Test:** `./gradlew test`

## API Usage
Refer to `api-test.http` for concrete examples of usage. The general flow is:

1.  **Connect:**
    *   `POST /api/db/connect` with DB credentials.
    *   Response contains a `connectionId` (UUID).
2.  **Query:**
    *   Use endpoints like `POST /api/db/products`.
    *   Include the `connectionId` in the request body.
3.  **Close:**
    *   `POST /api/db/close` with the `connectionId` to release resources.
