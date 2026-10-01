# Spring Microservices — Docker Deployment Guide

## 1. Project Structure

```text
spring_microservice/
├── eureka-server/       # 8761
├── api-gateway/         # 8080
├── product-service/     # 8081
├── order-service/       # 8082
├── client-service/      # 8083
└── docker-compose.yml
```

### Services

| Service         | Port | Database   |
| --------------- | ---: | ---------- |
| Eureka Server   | 8761 | —          |
| API Gateway     | 8080 | —          |
| Product Service | 8081 | PostgreSQL |
| Order Service   | 8082 | PostgreSQL |
| Client Service  | 8083 | MongoDB    |

---

# 2. Important Docker Hostnames

Inside Docker, **do not use `localhost`** for another container.

Use:

```text
Eureka:
http://eureka-server:8761/eureka/

Product PostgreSQL:
jdbc:postgresql://postgres-product:5432/dbproduct

Order PostgreSQL:
jdbc:postgresql://postgres-order:5432/dborder

Client MongoDB:
mongodb://mongodb-client:27017/dbclient
```

`localhost` means the current container.

---

# 3. Spring Service Configuration

## Client Service

```yaml
server:
  port: 8083

spring:
  application:
    name: client-service

  data:
    mongodb:
      uri: mongodb://mongodb-client:27017/dbclient

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

  instance:
    prefer-ip-address: true
```

## Product Service

```yaml
server:
  port: 8081

spring:
  application:
    name: product-service

  datasource:
    url: jdbc:postgresql://postgres-product:5432/dbproduct
    username: postgres
    password: admin123

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

  instance:
    prefer-ip-address: true
```

## Order Service

```yaml
server:
  port: 8082

spring:
  application:
    name: order-service

  datasource:
    url: jdbc:postgresql://postgres-order:5432/dborder
    username: postgres
    password: admin123

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

  instance:
    prefer-ip-address: true
```

## Eureka Server

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## API Gateway

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      routes:

        - id: client-service
          uri: lb://client-service
          predicates:
            - Path=/api/clients/**

        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

  instance:
    prefer-ip-address: true
```

---

# 4. Build Spring Boot JAR Files

From the project root:

```bash
cd eureka-server
./mvnw clean package -DskipTests

cd ../client-service
./mvnw clean package -DskipTests

cd ../product-service
./mvnw clean package -DskipTests

cd ../order-service
./mvnw clean package -DskipTests

cd ../api-gateway
./mvnw clean package -DskipTests

cd ..
```

Verify all JAR files:

```bash
find . -path "*/target/*.jar" -type f
```

Expected:

```text
./eureka-server/target/*.jar
./client-service/target/*.jar
./product-service/target/*.jar
./order-service/target/*.jar
./api-gateway/target/*.jar
```

---

# 5. Build Docker Images

From:

```text
spring_microservice/
```

run:

```bash
docker compose build
```

Build one service only:

```bash
docker compose build client-service
```

```bash
docker compose build product-service
```

```bash
docker compose build order-service
```

```bash
docker compose build eureka-server
```

```bash
docker compose build api-gateway
```

---

# 6. Start All Containers

```bash
docker compose up -d
```

Or rebuild and start:

```bash
docker compose up -d --build
```

Force recreate:

```bash
docker compose up -d --force-recreate
```

---

# 7. Check Container Status

```bash
docker compose ps
```

Expected:

```text
eureka-server       Up
mongodb-client      Up
postgres-product    Up
postgres-order      Up
client-service      Up
product-service     Up
order-service       Up
api-gateway         Up
```

---

# 8. View All Logs

```bash
docker compose logs
```

Last 100 lines:

```bash
docker compose logs --tail=100
```

Follow logs:

```bash
docker compose logs -f
```

---

# 9. View Individual Service Logs

## Eureka

```bash
docker compose logs -f eureka-server
```

## Gateway

```bash
docker compose logs -f api-gateway
```

## Client

```bash
docker compose logs -f client-service
```

## Product

```bash
docker compose logs -f product-service
```

## Order

```bash
docker compose logs -f order-service
```

## MongoDB

```bash
docker compose logs -f mongodb-client
```

## Product PostgreSQL

```bash
docker compose logs -f postgres-product
```

## Order PostgreSQL

```bash
docker compose logs -f postgres-order
```

---

# 10. Restart One Service

```bash
docker compose restart client-service
```

```bash
docker compose restart product-service
```

```bash
docker compose restart order-service
```

---

# 11. Rebuild One Service After Code Changes

For example, Product Service:

```bash
cd product-service
./mvnw clean package -DskipTests
cd ..
docker compose build product-service
docker compose up -d --force-recreate product-service
```

Client Service:

```bash
cd client-service
./mvnw clean package -DskipTests
cd ..
docker compose build client-service
docker compose up -d --force-recreate client-service
```

Order Service:

```bash
cd order-service
./mvnw clean package -DskipTests
cd ..
docker compose build order-service
docker compose up -d --force-recreate order-service
```

---

# 12. Stop Containers

```bash
docker compose stop
```

Start again:

```bash
docker compose start
```

---

# 13. Stop and Remove Containers

```bash
docker compose down
```

This removes containers and the Compose network.

Named volumes normally remain.

---

# 14. Remove Containers + Volumes

⚠️ This deletes database data stored in Compose volumes.

```bash
docker compose down -v
```

Use this only when you intentionally want a fresh database.

---

# 15. Remove Old Images

List images:

```bash
docker images
```

Remove a specific image:

```bash
docker rmi IMAGE_ID
```

Clean unused Docker resources:

```bash
docker system prune
```

More aggressive cleanup:

```bash
docker system prune -a
```

⚠️ Check what Docker wants to delete before confirming.

---

# 16. Check Docker Networks

List networks:

```bash
docker network ls
```

Inspect the microservices network:

```bash
docker network inspect spring_microservice_microservices-network
```

Containers should be connected to:

```text
microservices-network
```

---

# 17. Check Database Containers

MongoDB:

```bash
docker compose ps mongodb-client
```

PostgreSQL Product:

```bash
docker compose ps postgres-product
```

PostgreSQL Order:

```bash
docker compose ps postgres-order
```

---

# 18. Test MongoDB Connection

Enter MongoDB container:

```bash
docker exec -it mongodb-client mongosh
```

Show databases:

```javascript
show dbs
```

Use Client database:

```javascript
use dbclient
```

Show collections:

```javascript
show collections
```

Show clients:

```javascript
db.clients.find()
```

Exit:

```text
exit
```

---

# 19. Test PostgreSQL Product Database

```bash
docker exec -it postgres-product psql -U postgres -d dbproduct
```

Show tables:

```sql
\dt
```

Exit:

```sql
\q
```

---

# 20. Test PostgreSQL Order Database

```bash
docker exec -it postgres-order psql -U postgres -d dborder
```

Show tables:

```sql
\dt
```

Exit:

```sql
\q
```

---

# 21. Eureka

Open in browser:

```text
http://localhost:8761
```

Expected registered services:

```text
API-GATEWAY
CLIENT-SERVICE
PRODUCT-SERVICE
ORDER-SERVICE
```

---

# 22. API Gateway

External requests should normally go through:

```text
http://localhost:8080
```

Do not expose every microservice to the host unless needed.

Architecture:

```text
Postman / Frontend
        |
        v
API Gateway :8080
        |
        v
Eureka :8761
        |
        +----------------+
        |                |
        v                v
 Product :8081       Client :8083
        |
        v
 PostgreSQL          MongoDB
```

Order Service:

```text
API Gateway
     |
     v
Order Service :8082
     |
     +------> Client Service :8083
     |
     +------> Product Service :8081
     |
     v
PostgreSQL
```

---

# 23. Test APIs Through Gateway

## Client

```http
GET http://localhost:8080/api/clients
```

```http
GET http://localhost:8080/api/clients/{id}
```

Create:

```http
POST http://localhost:8080/api/clients
Content-Type: application/json
```

Example:

```json
{
  "clientCode": "CLI-001",
  "clientName": "Dara",
  "email": "dara@example.com",
  "phone": "012345678",
  "address": "Phnom Penh"
}
```

---

## Product

```http
GET http://localhost:8080/api/products
```

```http
GET http://localhost:8080/api/products/1
```

---

## Order

```http
GET http://localhost:8080/api/orders
```

```http
GET http://localhost:8080/api/orders/1
```

Create:

```http
POST http://localhost:8080/api/orders
Content-Type: application/json
```

Example:

```json
{
  "orderNumber": "ORDER-2026-001",
  "clientId": "YOUR_CLIENT_ID",
  "productId": 1,
  "totalAmount": 1299.00,
  "status": "PENDING"
}
```

---

# 24. Common Docker Problems

## Problem: MongoDB `localhost:27017`

Wrong inside Docker:

```yaml
uri: mongodb://localhost:27017/dbclient
```

Correct:

```yaml
uri: mongodb://mongodb-client:27017/dbclient
```

---

## Problem: PostgreSQL `localhost:5432`

Wrong inside Docker:

```yaml
url: jdbc:postgresql://localhost:5432/dbproduct
```

Correct:

```yaml
url: jdbc:postgresql://postgres-product:5432/dbproduct
```

For Order:

```yaml
url: jdbc:postgresql://postgres-order:5432/dborder
```

---

## Problem: Eureka `localhost:8761`

Wrong inside Docker:

```yaml
defaultZone: http://localhost:8761/eureka/
```

Correct:

```yaml
defaultZone: http://eureka-server:8761/eureka/
```

---

# 25. Dockerfile Pattern

For each Spring Boot service:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE SERVICE_PORT

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Examples:

Product:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Order:

```dockerfile
EXPOSE 8082
```

Client:

```dockerfile
EXPOSE 8083
```

Gateway:

```dockerfile
EXPOSE 8080
```

Eureka:

```dockerfile
EXPOSE 8761
```

---

# 26. Important Build Order

When source code changes:

```text
1. Change application code/config
        ↓
2. Build Spring Boot JAR
        ↓
3. Build Docker image
        ↓
4. Recreate container
        ↓
5. Check logs
        ↓
6. Test API
```

Commands:

```bash
./mvnw clean package -DskipTests
```

```bash
docker compose build
```

```bash
docker compose up -d
```

```bash
docker compose ps
```

```bash
docker compose logs --tail=100
```

---

# 27. Full Clean Deployment

Use this when you want to rebuild everything:

```bash
docker compose down
```

Build all JARs:

```bash
cd eureka-server
./mvnw clean package -DskipTests

cd ../client-service
./mvnw clean package -DskipTests

cd ../product-service
./mvnw clean package -DskipTests

cd ../order-service
./mvnw clean package -DskipTests

cd ../api-gateway
./mvnw clean package -DskipTests

cd ..
```

Build Docker images:

```bash
docker compose build
```

Start:

```bash
docker compose up -d
```

Check:

```bash
docker compose ps
```

Check logs:

```bash
docker compose logs --tail=100
```

Open Eureka:

```text
http://localhost:8761
```

Test Gateway:

```text
http://localhost:8080
```

---

# 28. Fresh Database Deployment

⚠️ Deletes existing MongoDB/PostgreSQL data.

```bash
docker compose down -v
```

Then:

```bash
docker compose build
```

Then:

```bash
docker compose up -d
```

Check:

```bash
docker compose ps
```

---

# 29. Troubleshooting Checklist

If a container shows:

```text
Exited (1)
```

run:

```bash
docker compose logs SERVICE_NAME
```

Examples:

```bash
docker compose logs product-service
```

```bash
docker compose logs order-service
```

```bash
docker compose logs client-service
```

Check whether the database is running:

```bash
docker compose ps
```

Check database logs:

```bash
docker compose logs postgres-product
docker compose logs postgres-order
docker compose logs mongodb-client
```

Check configuration for `localhost`:

```bash
grep -R "localhost" */src/main/resources/application.yml
```

Inside Docker, database/Eureka connections should use Docker service names instead of `localhost`.

---

# 30. Final Production-Like Flow

```text
                     Internet / Frontend / Postman
                                |
                                v
                     +---------------------+
                     |    API Gateway      |
                     |       :8080         |
                     +----------+----------+
                                |
                                v
                     +---------------------+
                     |   Eureka Server     |
                     |       :8761         |
                     +----------+----------+
                                |
             +------------------+------------------+
             |                  |                  |
             v                  v                  v
      +-------------+    +-------------+    +-------------+
      |   Product   |    |    Order    |    |   Client    |
      |   :8081     |    |    :8082    |    |   :8083     |
      +------+------+    +------+------+    +------+------+
             |                  |                  |
             v                  |                  v
       PostgreSQL              |               MongoDB
       dbproduct               |
                                |
                    +-----------+-----------+
                    |                       |
                    v                       v
             Client Service          Product Service
                :8083                   :8081
```

Key rules:

```text
Host → localhost:PORT

Container → service-name:PORT

Gateway → lb://service-name

Microservice → Eureka service-name

Client Service → mongodb-client:27017

Product Service → postgres-product:5432

Order Service → postgres-order:5432

All external API requests → API Gateway :8080
```
