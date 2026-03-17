# IoT Cold-Chain Monitoring System Ethiopian Pharmaceuticals Supply Service (EPSS)

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20CQRS-blue.svg)
![DDD](https://img.shields.io/badge/Design-Domain%20Driven%20Design-purple.svg)

An enterprise-grade Spring Boot microservice designed to manage the temperature monitoring of pharmaceutical shipping containers (e.g., vaccines) for the Ethiopian Pharmaceuticals Supply Service (EPSS).

This project was built to demonstrate absolute mastery of Clean Architecture, Domain-Driven Design (DDD), and the CQRS Pattern.

---

## Academic Information
* Course Code: SE413 - Introduction To Enterprise Systems
* Term: Winter 2026
* Batch: DRBSE2202 | Section: A
* Group Members:
    * Robel Yitbarek
    * Zedingle Biniyam
    * Kaleab Tilahun
    * Nahom Alehegn
    * Eyosiyas Tewodros
    * Natnael Girma

---

## System Architecture

The project strictly enforces the Dependency Inversion Principle (inner layers never depend on outer layers or frameworks) and is divided into four decoupled layers:

### 1. Domain Layer (The Core)
Contains 100% pure Java business logic with zero framework dependencies (No Spring, No JPA).
* Aggregate Root (`Container`): The master entity that guarantees the consistency of the shipping truck. It acts as a factory for its children and cascades business rules (e.g., ruining cargo if the temperature spikes).
* Child Entities (`CargoItem`, `SensorDevice`): Encapsulated entities with `protected` constructors. They can only be instantiated and modified by the Aggregate Root.
* Value Object (`TemperatureThreshold`): An immutable object that enforces physical laws (e.g., Absolute Zero checks) and business thresholds.

### 2. Application Layer (CQRS & Hexagonal Ports)
Orchestrates user intents using the Command Query Responsibility Segregation (CQRS) pattern.
* Inbound Ports (Use Cases): Interfaces defining the system's capabilities, ensuring the API layer is decoupled from implementation details.
* Write Handlers (Commands): Execute complex domain logic and state mutations under `@Transactional` safety.
* Read Handlers (Queries): Bypass the rich domain model entirely to return fast, flattened Data Transfer Objects (DTOs).

### 3. Infrastructure Layer (Anti-Corruption Layer)
Handles external concerns and database persistence.
* JPA Adapters: Translates pure Domain Aggregates into `JpaEntity` data structures.
* Aggregate Persistence: Utilizes `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)` to ensure the database schema perfectly mirrors the DDD Aggregate boundaries.

### 4. API / Presentation Layer
* REST Controllers: Dumb traffic routers that map HTTP requests to Application Use Cases.
* Global Exception Handler: A `@RestControllerAdvice` component that catches pure Java exceptions (`IllegalStateException`, `IllegalArgumentException`) and translates them into standard HTTP `400` and `422` JSON responses, protecting the system from crashes.

---

## API Reference & Use Cases

### Write Operations (Commands)
These endpoints mutate the state of the system and enforce strict Domain business rules.

| HTTP Method | Endpoint | Description | Business Rule Guard |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/containers/register` | Registers a new EPSS truck. | `minTemp` must be `< maxTemp`. Cannot be below absolute zero. |
| `POST` | `/api/containers/sensors` | Installs an IoT Sensor into the truck. | N/A |
| `POST` | `/api/containers/cargo` | Loads a box of vaccines (`CargoItem`). | Fails if the Container is already `SPOILED`. |
| `POST` | `/api/containers/telemetry` | Records a new sensor reading. | If limits are violated, Root marks itself AND all Cargo as `SPOILED`. |
| `POST` | `/api/containers/deliver` | Removes a box of vaccines for a hospital. | FATAL: Blocked if Container is `SPOILED` (Protects patient lives). |
| `PUT` | `/api/containers/range` | Updates the safe temperature range. | Blocked if Container is already `SPOILED`. |
| `DELETE` | `/api/containers/{id}` | Scraps/Deletes a container from DB. | Blocked if Container is `SAFE`. Only ruined trucks can be deleted. |

### Read Operations (Queries)
These endpoints read data rapidly using DTO projections.

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/containers/{id}` | Fetches the real-time status, temperature limits, and arrays of loaded Cargo & Sensors. |
| `GET` | `/api/containers/spoiled` | A dashboard query returning a list of all ruined shipments. |

---

## How to Run and Test

### 1. Start the Application
Run the `ColdchainApplication.java` main class via your IDE, or use the Maven wrapper:
```bash
./mvnw spring-boot:run