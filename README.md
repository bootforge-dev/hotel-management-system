# Hotel Booking Platform 🏩

## API's

### Hotel
```text
POST   /api/v1/hotels
GET    /api/v1/hotels/{id}
GET    /api/v1/hotels/search
PUT    /api/v1/hotels/{id}
DELETE /api/v1/hotels/{id}
```

### Room
```text
POST /api/v1/rooms
GET  /api/v1/rooms/{id}
GET  /api/v1/rooms/hotel/{hotelId}
GET  /api/v1/rooms/{id}/availability
POST /api/v1/rooms/{id}/reserve
POST /api/v1/rooms/{id}/release
```

### Booking
```text
POST   /api/v1/bookings
GET    /api/v1/bookings/{reference}
GET    /api/v1/bookings/user/{userId}
POST   /api/v1/bookings/{reference}/cancel
```

### Payment
```text
GET /api/v1/payments/{paymentId}
```

### Review
```text
POST /api/v1/reviews
GET  /api/v1/hotels/{hotelId}/reviews
PUT  /api/v1/reviews/{id}
DELETE /api/v1/reviews/{id}
```

### ProjectStructure

```text
hotel-booking-platform
│
├── api-gateway
│
├── service-registry
│
├── config-server
│
├── hotel-service
│
├── room-service
│
├── booking-service
│
├── payment-service
│
├── review-service
│
├── notification-service
│
├── docker-compose.yml
│
├── README.md
│
└── docs
    ├── architecture
    ├── api
    ├── database
    └── kafka
```