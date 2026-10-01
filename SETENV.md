# Environment Variables — `set-env.sh`

## 1. Create `set-env.sh`

Create this file in the project root:

```bash
set-env.sh
```

Add:

```bash
#!/bin/bash

export DB_PRODUCT_PASSWORD=admin123
export DB_ORDER_PASSWORD=admin123
export MONGODB_URI=mongodb://localhost:27017/dbclient
```

## 2. Make the script executable

```bash
chmod +x set-env.sh
```

## 3. Load environment variables

From the project root:

```bash
source set-env.sh
```

Or:

```bash
. set-env.sh
```

## 4. Check the variables

```bash
echo $DB_PRODUCT_PASSWORD
echo $DB_ORDER_PASSWORD
echo $MONGODB_URI
```

Expected:

```text
admin123
admin123
mongodb://localhost:27017/dbclient
```

## 5. Start Product Service

After loading the environment variables:

```bash
cd product-service
./mvnw spring-boot:run
```

## 6. Start Order Service

Open another terminal, go to the project root, and load the variables again:

```bash
source set-env.sh
```

Then:

```bash
cd order-service
./mvnw spring-boot:run
```

## 7. Start Client Service

```bash
source set-env.sh
cd client-service
./mvnw spring-boot:run
```

## Important

The environment variables exist only in the current terminal session.

For example:

```bash
source set-env.sh
echo $DB_PRODUCT_PASSWORD
```

works.

But if you open a **new terminal**, you need to run:

```bash
source set-env.sh
```

again.

## Spring Boot Configuration

Your Config Server configuration can safely use:

```yaml
spring:
  datasource:
    password: ${DB_PRODUCT_PASSWORD}
```

Spring Boot reads `DB_PRODUCT_PASSWORD` from the environment when the service starts.

## Security

Do **not** commit real production passwords to GitHub.

Add the environment file to `.gitignore` if it contains real secrets:

```gitignore
set-env.sh
.env
```

For production deployment, provide the environment variables through your deployment platform, Docker secrets/environment variables, Kubernetes Secrets, or another secret-management system.
