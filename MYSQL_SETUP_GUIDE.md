# MySQL Setup Guide - SoukTn Microservices

## Prerequisites

- MySQL Server running
- MySQL credentials:
  - **Host:** localhost
  - **Port:** 3306
  - **Username:** root
  - **Password:** root1234

## MySQL Configuration

All services are configured to use MySQL with the following environment variables:

```bash
DB_HOST=localhost          # MySQL server host
DB_PORT=3306               # MySQL server port
DB_NAME=<service_db>       # Database name (auto-created)
DB_USER=root               # MySQL username
DB_PASSWORD=root1234       # MySQL password
```

## Service Database Configuration

### Discovery Server (8761)
- No database required
- Pure Eureka service registry

### Config Server (8888)
- No database required
- File-based configuration from `/Backend/config-repo/`

### Delivery Service (8085)
- **Database:** delivery_db
- **Tables:** delivery, delivery_tracking, shipment, etc.
- **Command to run:**
```bash
java -jar Backend/delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=delivery_db \
  --DB_USER=root \
  --DB_PASSWORD=root1234 \
  --SERVER_PORT=8085 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

### Payment Service (8084)
- **Database:** payment_db
- **Tables:** payment, invoice, invoice_line, refund, etc.
- **Command to run:**
```bash
java -jar Backend/payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=payment_db \
  --DB_USER=root \
  --DB_PASSWORD=root1234 \
  --SERVER_PORT=8084 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

## Quick Start - All Services

### Terminal 1: Discovery Server
```bash
cd Backend
java -jar discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8761
```

### Terminal 2: Config Server
```bash
cd Backend
java -jar config-server/target/config-server-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8888 \
  --EUREKA_URI=http://localhost:8761/eureka
```

### Terminal 3: Delivery Service
```bash
cd Backend
java -jar delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=delivery_db \
  --DB_USER=root \
  --DB_PASSWORD=root1234 \
  --SERVER_PORT=8085 \
  --EUREKA_URI=http://localhost:8761/eureka
```

### Terminal 4: Payment Service
```bash
cd Backend
java -jar payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=payment_db \
  --DB_USER=root \
  --DB_PASSWORD=root1234 \
  --SERVER_PORT=8084 \
  --EUREKA_URI=http://localhost:8761/eureka
```

## Verification

After all services start (wait 10-15 seconds for registration):

### Check Eureka Dashboard
```bash
curl http://localhost:8761
# Or open in browser: http://localhost:8761
```

### Check Registered Services
```bash
curl http://localhost:8761/eureka/apps/
# Shows: CONFIG-SERVER, DELIVERY, PAYMENT
```

### Check Service Health
```bash
curl http://localhost:8085/actuator/health  # Delivery
curl http://localhost:8084/actuator/health  # Payment
curl http://localhost:8888/actuator/health  # Config Server
```

### Check Database Connection
```bash
curl http://localhost:8085/actuator/health | jq '.components.db'
curl http://localhost:8084/actuator/health | jq '.components.db'
```

## Databases Created

After first run, MySQL will have:

```sql
SHOW DATABASES;
-- Output:
-- information_schema
-- mysql
-- performance_schema
-- sys
-- delivery_db        (new)
-- payment_db         (new)

SHOW TABLES IN delivery_db;
-- delivery
-- delivery_tracking
-- shipment
-- (and others)

SHOW TABLES IN payment_db;
-- payment
-- invoice
-- invoice_line
-- refund
-- (and others)
```

## Default Values (if not specified)

```
DB_HOST: localhost
DB_PORT: 3306
DB_USER: root
DB_PASSWORD: (empty)
SERVER_PORT: 8761/8888/8085/8084
EUREKA_URI: http://localhost:8761/eureka
SPRING_CONFIG_IMPORT: http://localhost:8888
```

## Troubleshooting

### Connection Refused
- Check MySQL is running
- Verify credentials (root / root1234)
- Check port 3306 is open

### Database Already Exists
- Hibernate will update existing tables (ddl-auto: update)
- No data loss, only schema updates

### Services Not Registering
- Wait 5-10 seconds after all services start
- Check Eureka dashboard: http://localhost:8761
- Check logs for registration errors

### Application.yml Not Loading
- Ensure config-server starts before microservices
- Wait 5 seconds for config-server to initialize

## Service Ports

| Service | Port | Database |
|---------|------|----------|
| Discovery Server | 8761 | No DB |
| Config Server | 8888 | No DB |
| Delivery Service | 8085 | delivery_db |
| Payment Service | 8084 | payment_db |

---

**All microservices are now configured to use MySQL!**
