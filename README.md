# Dispatch Load Balancer

Spring Boot backend service that allocates delivery orders to vehicles using:
- priority-first assignment (`HIGH -> MEDIUM -> LOW`)
- capacity constraints
- nearest feasible vehicle based on Haversine distance

## Features
- REST APIs for order ingestion, vehicle ingestion, and dispatch plan retrieval
- Greedy dispatch optimization algorithm
- Haversine utility with unit tests
- Request validation with structured error responses
- Unit and integration test coverage

## Tech Stack
- Java 24
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- MySQL (default runtime database)
- H2 (test/local profile)
- Lombok
- Jakarta Validation
- JUnit 5, Mockito, MockMvc

## Project Structure
`src/main/java/com/assignment/dispatch`
- `controller` - REST controllers
- `service` - service interfaces
- `service/impl` - service implementations and dispatch logic
- `repository` - JPA repositories
- `model` - JPA entities and enums
- `dto` - request/response payloads
- `util` - utility classes (Haversine)
- `exception` - global exception handling

## Prerequisites
- Java 24
- Maven 3.9+
- MySQL 8+ (for default profile)

Verify tools:
```bash
java -version
mvn -version
```

## Database Configuration
Default config file: `src/main/resources/application.properties`

Important properties:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dispatch_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Update username/password if your local MySQL credentials are different.

## Run Application
From the project root directory:

### Option 1: Run with MySQL (default)
1. Start MySQL.
2. Run:
```bash
mvn spring-boot:run
```

### Option 2: Run with H2 profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Application base URL:
`http://localhost:8080`

H2 console (when `h2` profile is active):
`http://localhost:8080/h2-console`

## Run Tests
```bash
mvn clean test -Dspring.profiles.active=h2
```

## API Endpoints

### 1. Create Orders
`POST /api/dispatch/orders`

Request:
```json
{
  "orders": [
    {
      "orderId": "ORD001",
      "latitude": 12.9716,
      "longitude": 77.5946,
      "address": "MG Road, Bangalore, Karnataka, India",
      "packageWeight": 10,
      "priority": "HIGH"
    },
    {
      "orderId": "ORD002",
      "latitude": 12.9352,
      "longitude": 77.6245,
      "address": "Koramangala, Bangalore, Karnataka, India",
      "packageWeight": 15,
      "priority": "MEDIUM"
    }
  ]
}
```

Success response (`201 Created`):
```json
{
  "message": "Delivery orders accepted.",
  "status": "success"
}
```

### 2. Create Vehicles
`POST /api/dispatch/vehicles`

Request:
```json
{
  "vehicles": [
    {
      "vehicleId": "VEH001",
      "capacity": 100,
      "currentLatitude": 12.9716,
      "currentLongitude": 77.6413,
      "currentAddress": "Indiranagar, Bangalore, Karnataka, India"
    },
    {
      "vehicleId": "VEH002",
      "capacity": 80,
      "currentLatitude": 12.9352,
      "currentLongitude": 77.6245,
      "currentAddress": "Koramangala, Bangalore, Karnataka, India"
    }
  ]
}
```

Success response (`201 Created`):
```json
{
  "message": "Vehicle details accepted.",
  "status": "success"
}
```

### 3. Get Dispatch Plan
`GET /api/dispatch/plan`

Response:
```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 25.0,
      "totalDistance": "7.91 km",
      "assignedOrders": [
        {
          "orderId": "ORD001",
          "latitude": 12.9716,
          "longitude": 77.5946,
          "address": "MG Road, Bangalore, Karnataka, India",
          "packageWeight": 10.0,
          "priority": "HIGH"
        }
      ]
    }
  ],
  "unassignedOrders": []
}
```

## Dispatch Algorithm
1. Fetch all orders and vehicles.
2. Sort orders by priority (`HIGH`, `MEDIUM`, `LOW`).
3. For each order:
   - filter vehicles with enough remaining capacity
   - compute distance from vehicle current location to order location
   - choose vehicle with minimum distance
4. Assign order and update vehicle state:
   - remaining capacity
   - total distance
   - current location (moved to assigned order location)
5. If no feasible vehicle is found, store order in `unassignedOrders`.

## Validation and Error Handling
Validation examples:
- missing required fields
- negative/zero weight or capacity
- invalid `priority` values
- duplicate `orderId` or `vehicleId` inside same request

Error response format:
```json
{
  "timestamp": "2026-02-10T16:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/dispatch/orders",
  "details": [
    "orders[0].packageWeight: packageWeight must be positive"
  ]
}
```

## Postman
Import:
- `postman/Dispatch-Load-Balancer.postman_collection.json`

Collection variable:
- `baseUrl` default value: `http://localhost:8080`

Recommended call order:
1. `Create Vehicles`
2. `Create Orders`
3. `Get Dispatch Plan`

## Notes
- The project uses MySQL by default for runtime.
- Tests are designed to run with the H2 profile for portability.
