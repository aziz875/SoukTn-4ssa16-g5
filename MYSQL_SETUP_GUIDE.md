# MySQL Setup Guide - SoukTn Microservices

## MySQL Credentials (All Services)

```
Host:     localhost
Port:     3306
User:     root
Password: 1234
```

## Databases Created Automatically

- **delivery_db** - Delivery service database
- **payment_db** - Payment service database

## Quick Start

### Option 1: Using Startup Script (Easiest)
```bash
./run-services.sh
```

### Option 2: Manual Terminal 1 - Discovery Server
```bash
cd Backend
java -jar discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar --SERVER_PORT=8761
```

Terminal 2 - Config Server (after Terminal 1 starts)
```bash
cd Backend
java -jar config-server/target/config-server-1.0.0-SNAPSHOT.jar --SERVER_PORT=8888 --EUREKA_URI=http://localhost:8761/eureka
```

Terminal 3 - Delivery Service (after Terminal 2 starts)
```bash
cd Backend
java -jar delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost --DB_PORT=3306 --DB_NAME=delivery_db \
  --DB_USER=root --DB_PASSWORD=1234 \
  --SERVER_PORT=8085 --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

Terminal 4 - Payment Service (after Terminal 3 starts)
```bash
cd Backend
java -jar payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost --DB_PORT=3306 --DB_NAME=payment_db \
  --DB_USER=root --DB_PASSWORD=1234 \
  --SERVER_PORT=8084 --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

## Verify Services Running

```bash
# Check Eureka Dashboard
curl http://localhost:8761

# Check all registered services
curl http://localhost:8761/eureka/apps/

# Check health
curl http://localhost:8085/actuator/health
curl http://localhost:8084/actuator/health
```

## Service Ports

| Service | Port | Database |
|---------|------|----------|
| Discovery Server | 8761 | None |
| Config Server | 8888 | None |
| Delivery Service | 8085 | delivery_db |
| Payment Service | 8084 | payment_db |

---

**MySQL Credentials: root / 1234**
