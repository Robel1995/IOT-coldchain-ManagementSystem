# Title :  IoT Cold-Chain Monitoring System
# Description
This project is a Spring Boot application built to demonstrate Clean Architecture, CQRS, and Domain-Driven Design (DDD) principles. It manages the temperature monitoring of pharmaceutical shipping containers.

# Architecture 

The project strictly follows the Dependency Rule (Inner layers do not depend on outer layers) and is separated into four distinct layers:

1. Domain Layer: Contains pure Java business logic, Invariants, Entities (`Container` - Aggregate Root), and Value Objects (`TemperatureReading`). It has zero dependencies on Spring Framework or the database.
2. Application Layer: Orchestrates use cases using the CQRS Pattern. It contains Ports (Interfaces) and handles the routing of Commands and Queries.
3. Infrastructure Layer: Implements the database Adapters using Spring Data JPA and H2 in-memory database.
4. API / Presentation Layer: Contains REST Controllers that simply map HTTP requests to the Application use cases.

#  Design Patterns Used

* CQRS (Command Query Responsibility Segregation):
    * Commands (Write): Handle complex domain logic and state mutation.
    * Queries (Read): Bypass domain logic and return simple, flat DTOs for fast UI rendering.
* Factory Pattern: The `ContainerFactory` ensures that domain entities are always created in a valid state (e.g., minimum temperature cannot exceed maximum temperature).

# Use Cases Implemented

### Commands (Write Operations)
* `POST /api/containers/register` - Registers a new container with safe temperature limits.
* `POST /api/containers/telemetry` - Records a new sensor reading. If the reading violates safe limits, the domain permanently marks the container as `SPOILED`.
* `PUT /api/containers/range` - Updates the temperature range (fails if already spoiled).
* `DELETE /api/containers/{id}` - Deletes a container (Business Rule: Can only delete if the container is spoiled).

### Queries (Read Operations)
* `GET /api/containers/{id}` - Retrieves the current status of a specific container.
* `GET /api/containers/spoiled` - Dashboard query returning a list of all ruined shipments.

# How to Run and Test

1. Start the Spring Boot application (`ColdchainApplication.java`).
2. The H2 Database Console is available at: `http://localhost:8080/h2-console` // in-memory databse system
    * JDBC URL: `jdbc:h2:mem:testdb`
    * User Name: `sa`
    * Password: `robel`
3. Use Postman to hit the API endpoints listed above.

##    Batch :       DRBSE2202   Section A
##    Term :        Winter_2026
##    Course code :  SE413    Introduction To Enterprise Systems  
##   Group Members: 
                    Robel Yitbarek
                    Zedingle Biniyam
                    Kaleab Ashenafi
                    Nahom alehegn
                    Eyosiyas  Tewodros
                    Natnael Girma
