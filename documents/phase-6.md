## Phase-6

```text
                           CLIENT
                              |
                              v
                       API GATEWAY
                         :8080
                              |
                              v
                    ┌──────────────────┐
                    │ Booking Service  │
                    │      :9003       │
                    └────────┬─────────┘
                             |
                 ┌───────────┼────────────┐
                 |           |            |
                 v           v            v
              Redis       Hotel        Room
                 |        Service      Service
                 |                       |
        ┌────────┼────────┐              v
        |        |        |           Room DB
        v        v        v
      Lock    Cache   Idempotency
                 |
                 |
                 v
            Booking DB
                 |
                 v
           Outbox Events
                 |
                 v
               Kafka
                 |
                 v
          Payment Service
                 |
          ┌──────┴──────┐
          v             v
       Payment DB     Outbox
                        |
                        v
                      Kafka
                        |
                        v
                 Booking Service
                        |
             ┌──────────┴──────────┐
             v                     v
          CONFIRMED             CANCELLED
                                   |
                                   v
                              Release Room
```