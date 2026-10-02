# Spring Microservices — Run Commands

## Project Location

```bash
/Users/testing/Documents/Project Testing/spring_microservice
```

## Microservices

| Service         |   Port | Purpose                   |
| --------------- | -----: | ------------------------- |
| Config Server   | `8888` | Centralized configuration |
| Eureka Server   | `8761` | Service discovery         |
| Product Service | `8081` | Product management        |
| Order Service   | `8082` | Order management          |
| Client Service  | `8083` | Client management         |
| Auth Service    | `8084` | Authentication & JWT      |
| API Gateway     | `8080` | Single entry point        |

---

# 1. Start Config Server

Open **Terminal 1**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/config-server"

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8888
```

---

# 2. Start Eureka Server

Open **Terminal 2**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/eureka-server"

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

Eureka Dashboard:

```text
http://localhost:8761
```

---

# 3. Start Auth Service

Open **Terminal 3**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/auth-service"

source ../set-env.sh

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8084
```

---

# 4. Start Product Service

Open **Terminal 4**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/product-service"

source ../set-env.sh

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8081
```

---

# 5. Start Client Service

Open **Terminal 5**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/client-service"

source ../set-env.sh

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8083
```

---

# 6. Start Order Service

Open **Terminal 6**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/order-service"

source ../set-env.sh

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8082
```

---

# 7. Start API Gateway

Open **Terminal 7**:

```bash
cd "/Users/testing/Documents/Project Testing/spring_microservice/api-gateway"

./mvnw spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

---

# Startup Order

Start the services in this order:

```text
1. Config Server
       ↓
2. Eureka Server
       ↓
3. Auth Service
4. Product Service
5. Client Service
6. Order Service
       ↓
7. API Gateway
```

The service architecture is:

```text
                    ┌──────────────────┐
                    │   Config Server  │
                    │      :8888       │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │  Eureka Server   │
                    │      :8761       │
                    └────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
   Auth Service       Product Service     Client Service
      :8084                :8081              :8083
          │                  │                  │
          └──────────────────┼──────────────────┘
                             │
                             ▼
                       Order Service
                           :8082
                             │
                             ▼
                    ┌──────────────────┐
                    │   API Gateway    │
                    │      :8080       │
                    └──────────────────┘
```

---

# API Gateway URLs

The application should normally access services through the API Gateway.

## Auth

```text
POST http://localhost:8080/api/auth/register
POST http://localhost:8080/api/auth/login
```

## Products

```text
GET    http://localhost:8080/api/products
POST   http://localhost:8080/api/products
PUT    http://localhost:8080/api/products/{id}
DELETE http://localhost:8080/api/products/{id}
```

## Clients

```text
GET    http://localhost:8080/api/clients
POST   http://localhost:8080/api/clients
PUT    http://localhost:8080/api/clients/{id}
DELETE http://localhost:8080/api/clients/{id}
```

## Orders

```text
GET    http://localhost:8080/api/orders
POST   http://localhost:8080/api/orders
PUT    http://localhost:8080/api/orders/{id}
DELETE http://localhost:8080/api/orders/{id}
```

---

# Authentication Flow

Register first:

```text
POST /api/auth/register
```

Then login:

```text
POST /api/auth/login
```

The login response returns:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "email": "test@example.com",
  "role": "USER"
}
```

For protected APIs, use:

```text
Authorization
    ↓
Bearer Token
    ↓
<accessToken>
```

Example:

```text
GET http://localhost:8080/api/orders
```

with:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

# Environment Variables

The project uses:

```bash
set-env.sh
```

Example:

```bash
export DB_PRODUCT_PASSWORD=admin123
export DB_ORDER_PASSWORD=admin123
export DB_AUTH_PASSWORD=admin123

export MONGODB_URI=mongodb://localhost:27017/dbclient

export EUREKA_SERVER_URL=http://localhost:8761/eureka/

export JWT_SECRET="my-super-secret-key-for-jwt-authentication-2026"
```

For services that require environment variables:

```bash
source ../set-env.sh
```

---

# Stop a Service

Inside the terminal running the service:

```bash
Ctrl + C
```

---

# Clean and Restart

If a service has a build or configuration problem:

```bash
Ctrl + C

./mvnw clean

./mvnw spring-boot:run
```

---

# Important

For normal application requests, use:

```text
API Gateway :8080
```

instead of calling each microservice directly.

For example:

```text
✅ http://localhost:8080/api/orders
```

instead of:

```text
http://localhost:8082/api/orders
```

The Gateway uses Eureka to discover the services:

```text
lb://auth-service
lb://product-service
lb://client-service
lb://order-service
```
