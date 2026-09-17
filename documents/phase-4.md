## Phase 4 — Kafka + Payment Service + Booking Events

Now we move from a mostly synchronous architecture to an event-driven booking workflow.

The important business flow becomes:

```text
Customer
   │
   ▼
API Gateway
   │
   ▼
Booking Service
   │
   ├── OpenFeign ──► Hotel Service
   │
   ├── OpenFeign ──► Room Service
   │                    │
   │                    └── Reserve Room
   │
   ▼
Booking = PAYMENT_PENDING
   │
   │ publish booking-created
   ▼
  Kafka
   │
   ▼
Payment Service
   │
   ├───────────────┐
   │               │
 SUCCESS          FAILURE
   │               │
   ▼               ▼
Kafka             Kafka
   │               │
payment-success   payment-failed
   │               │
   └───────┬───────┘
           ▼
    Booking Service
       │          │
       ▼          ▼
   CONFIRMED   CANCELLED
                  │
                  ▼
             Room Service
                  │
                  ▼
             Release Room
```

This phase introduces:

- Kafka
- booking-created
- payment-success
- payment-failed
- Payment Service
- Kafka producer
- Kafka consumers
- Payment database
- Idempotent payment processing
- Booking confirmation
- Compensation on payment failure
- Retry/DLQ foundation