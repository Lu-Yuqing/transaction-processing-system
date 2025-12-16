# Architecture

Event-driven microservice that processes financial transactions asynchronously using Apache Kafka.

## System Overview

```
External Systems (Kafka Producer, Incentive API)
         ↓
Midas Core Service
  ├─ TransactionListener (Kafka consumer)
  ├─ TransactionService (business logic)
  ├─ IncentiveService (external API integration)
  ├─ Repositories (data access)
  └─ BalanceController (REST API)
         ↓
   PostgreSQL/H2
```

## Components

### TransactionListener
- Consumes messages from Kafka topic `transactions`
- Delegates to TransactionService for processing

### TransactionService
- Validates sender and recipient exist
- Validates sufficient balance
- Calls IncentiveService for incentive amount
- Atomically updates balances
- Creates transaction records

### IncentiveService
- Calls external incentive API
- Returns zero incentive on failure (graceful degradation)

### BalanceController
- `GET /balance?userId={id}` endpoint
- Returns user balance from database

## Data Flow

1. Transaction arrives via Kafka
2. TransactionListener receives and deserializes
3. TransactionService validates and processes
4. IncentiveService fetches incentive (if available)
5. Balances updated atomically
6. Transaction record saved

## Database

- **Development**: H2 in-memory
- **Production**: PostgreSQL with Flyway migrations
- Schema managed via Flyway in `src/main/resources/db/migration/`

## Design Patterns

- Event-driven architecture (Kafka)
- Repository pattern (Spring Data JPA)
- Service layer pattern
- Dependency injection (Spring)

## Error Handling

- Validation failures: Transaction rejected, logged
- API failures: Default to zero incentive, continue processing
- Database errors: Automatic rollback via @Transactional
