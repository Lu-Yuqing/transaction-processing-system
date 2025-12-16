# Midas Core - Transaction Processing System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.1.4-blue.svg)](https://kafka.apache.org/)

Event-driven transaction processing system built with Spring Boot. Processes financial transactions asynchronously via Kafka, manages user balances, and integrates with external incentive services.

## Features

- Event-driven architecture with Apache Kafka
- RESTful API for balance queries
- Transaction validation and atomic processing
- External incentive API integration
- PostgreSQL for production, H2 for development
- Database migrations with Flyway

## Architecture

```
Kafka Producer → Midas Core → Incentive API
                      ↓
                 PostgreSQL/H2
                      ↓
                  REST API
```

Key components:
- **TransactionListener**: Consumes transactions from Kafka
- **TransactionService**: Validates and processes transactions
- **IncentiveService**: Integrates with external incentive API
- **BalanceController**: REST endpoint for balance queries

See [ARCHITECTURE.md](ARCHITECTURE.md) for details.

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose (recommended)

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/Lu-Yuqing/transaction-processing-system.git
   cd transaction-processing-system
   ```

2. Start infrastructure
   ```bash
   docker-compose up -d
   ```

3. Build and run
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   For production mode with PostgreSQL:
   ```bash
   SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
   ```

Application runs on `http://localhost:33400`

## API

### Get Balance

```bash
GET /balance?userId=1
```

Response:
```json
{
  "balance": 1500.50
}
```

### Transaction Processing

Send transactions to Kafka topic `transactions`:

```json
{
  "senderId": 1,
  "recipientId": 2,
  "amount": 100.0
}
```

## Configuration

- **Development** (`application-dev.yml`): H2 in-memory database
- **Production** (`application-prod.yml`): PostgreSQL with Flyway migrations

Environment variables:
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=midasdb
DB_USERNAME=midasuser
DB_PASSWORD=midaspass
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_PROFILES_ACTIVE=prod
```

## Testing

```bash
./mvnw test
```

## Technologies

- Spring Boot 3.2.5
- Spring Kafka
- Spring Data JPA
- PostgreSQL / H2
- Flyway
- Apache Kafka
- Maven

## Acknowledgments

Built as part of the JPMC Advanced Software Engineering Forage program.
