# E-Commerce Microservices Platform

A Java / Spring Boot microservices backend for an e-commerce platform —
built to explore service decomposition, inter-service communication, and
JWT-based security across independently deployable services.

## Architecture

Seven Maven modules, each with its own database and its own security
boundary, registered against a shared Eureka discovery server:

```
                    ┌─────────────────────┐
                    │  Discovery Service   │  (Eureka)
                    └───────────▲──────────┘
                                │ register / discover
     ┌──────────────┬──────────┼──────────┬──────────────┬──────────────┐
     │              │          │          │              │              │
 UserAuth       Product    Inventory     Cart          Order        Payment
 Service        Service     Service    Service        Service       Service
     │              │          ▲          │              │
     │              └──────────┘          │              │
     │              (create inventory     └──────────────┘
     │               on new product)      (fetch cart to build order)
```

Every service independently validates incoming JWTs (RSA-signed, issued by
User Auth Service) rather than relying on a central gateway to gate access —
each service is a self-contained security boundary.

## Services

| Service | Responsibility |
|---|---|
| **Discovery Service** | Eureka registry — service discovery for the whole system |
| **User Auth Service** | Signup/login, JWT issuing & refresh, user profiles, address book, seller onboarding |
| **Product Service** | Product catalog, categories (hierarchical), product images and attributes |
| **Inventory Service** | Stock tracking per seller/product, with a reservation lifecycle (reserve → confirm/release/expire) |
| **Cart Service** | Per-user shopping cart, with live stock validation via Inventory Service |
| **Order Service** | Order placement and order history, built from the user's cart |
| **Payment Service** | Payment link generation via Razorpay |

## Tech stack

- **Language / framework:** Java, Spring Boot 3, Spring Data JPA / Hibernate
- **Service discovery:** Netflix Eureka
- **Inter-service communication:** OpenFeign
- **Database:** PostgreSQL (isolated schema per service)
- **Auth:** Stateless JWT (RSA-signed), role-based access control (`USER`, `SELLER`, `ADMIN`) enforced per-service via `@PreAuthorize`
- **API documentation:** springdoc-openapi (Swagger UI) per service
- **Payments:** Razorpay Java SDK
- **Build:** Maven multi-module reactor

## Key design decisions

- **Independent databases per service** — each service owns its data exclusively and exposes it only through its own API, avoiding shared-database coupling between services.
- **Stateless, self-validating auth** — Auth Service is the only service holding the RSA private key; every other service holds only the public key and verifies JWTs locally, without a round-trip to Auth Service on every request.
- **Stock reservation as a first-class concept** — Inventory Service models reservations (`RESERVED → CONFIRMED / RELEASED / EXPIRED`) as their own lifecycle, separate from raw stock counts, to support a checkout flow where stock is held temporarily rather than deducted immediately on cart-add.
- **Feign for synchronous inter-service calls** — used where a service genuinely needs another service's data to complete its own request (e.g. Order Service pulling the current cart before placing an order).

## Running locally

Each module is a standard Spring Boot application with its own
`application.properties`. Start `discovery-service` first, then the
remaining services in any order — they'll register with Eureka on startup.

```bash
# from the repo root
cd discovery-service && mvn spring-boot:run
# in separate terminals
cd EcomUserAuthService && mvn spring-boot:run
cd EcomProductService && mvn spring-boot:run
cd EcomInventoryService && mvn spring-boot:run
cd EcomCartService && mvn spring-boot:run
cd EcomOrderService && mvn spring-boot:run
cd EcomPaymentService && mvn spring-boot:run
```

Each service exposes Swagger UI at `/swagger-ui.html` once running.

## Roadmap

- [ ] Wire order placement to inventory reservation, so stock is held atomically at checkout
- [ ] Close the payment → order confirmation loop (webhook-driven status updates)
- [ ] Add an API Gateway as a single entry point in front of all services
- [ ] Move secrets (JWT keys, payment provider credentials) out of `application.properties` into environment-based config

## License

This is a personal/portfolio project.
