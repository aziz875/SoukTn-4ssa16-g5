# SoukTn Microservices Architecture - Verification Checklist

## Project Requirements Status

### ✅ COMPLETED

#### 1. **Discovery Server (Eureka)**
- [x] Eureka Server created and functional (Spring Boot)
- [x] Runs on port 8761
- [x] Configured to NOT register itself
- [x] Dashboard available at: `http://localhost:8761/eureka`
- [x] Actuator endpoints exposed: health, info, metrics, loggers, env, beans, mappings
- [x] Service name: `discovery-server` (semantic naming)

#### 2. **Config Server**
- [x] Spring Cloud Config Server functional
- [x] Runs on port 8888
- [x] Registered as Eureka client (`config-server`)
- [x] Uses native profile with file-based config repo (`./Backend/config-repo/`)
- [x] Actuator endpoints exposed
- [x] Management configuration:
  - health, info, metrics, loggers, env, beans, mappings, configprops

#### 3. **Delivery Service (Microservice - Team Member)**
- [x] Microservice created with entities and associations
- [x] CRUD operations implemented
- [x] Runs on port 8085
- [x] Registered as Eureka client (`delivery`)
- [x] Connected to Config Server
- [x] Uses MySQL database (or H2 for testing)
- [x] Actuator endpoints exposed
- [x] Application name: `delivery`

#### 4. **Payment Service (Microservice - Team Member)**
- [x] Microservice created with entities and associations
- [x] CRUD operations implemented
- [x] Runs on port 8084
- [x] Registered as Eureka client (`payment`)
- [x] Connected to Config Server
- [x] Uses H2 in-memory database
- [x] Actuator endpoints exposed
- [x] Application name: `payment`

#### 5. **Eureka Client Configuration**
- [x] All microservices configured as Eureka clients
- [x] Registration enabled: `register-with-eureka: true`
- [x] Registry fetch enabled: `fetch-registry: true`
- [x] Health check enabled
- [x] IP address preference: `prefer-ip-address: true`

#### 6. **Actuator & Observability**
- [x] Spring Boot Actuator integrated in all services
- [x] Management endpoints exposed:
  - `/actuator/health` - Service health status
  - `/actuator/info` - Service information
  - `/actuator/metrics` - Performance metrics
  - `/actuator/loggers` - Logger configuration
  - `/actuator/env` - Environment properties
  - `/actuator/beans` - Spring beans list
  - `/actuator/mappings` - Request mappings
  - `/actuator/configprops` - Configuration properties
- [x] Health details visibility: `show-details=always`
- [x] Environment info enabled

#### 7. **Environment Variables Support**
- [x] `SERVER_PORT` - Configure service port dynamically
- [x] `EUREKA_URI` - Configure Eureka discovery URL
- [x] `SPRING_CONFIG_IMPORT` - Configure config server URL

---

### ⏳ TODO / OPTIONAL

#### Multiple Instances Setup
- [ ] Discovery Server Instance 2 (port 8762)
- [ ] Delivery Service Instance 2 (port 8086)
- [ ] Payment Service Instance 2 (port 8085) - optional, another service preferred
- [ ] Eureka high availability configuration (peer-to-peer replication)

#### Documentation & Evidence
- [ ] Screenshots of Eureka Dashboard showing all services
- [ ] Screenshots of Actuator endpoints (health, metrics, etc.)
- [ ] README.md with team member names
- [ ] Architecture diagram
- [ ] Setup and run instructions

#### GitHub Integration
- [ ] Project pushed to GitHub
- [ ] Team member names in README
- [ ] Screenshots in repository

---

## Running the Services

### 1. Start Discovery Server
```bash
cd Backend/discovery-server
java -jar target/discovery-server-1.0.0-SNAPSHOT.jar
# Or: mvn spring-boot:run
# Access dashboard: http://localhost:8761
```

### 2. Start Config Server
```bash
cd Backend/config-server
java -jar target/config-server-1.0.0-SNAPSHOT.jar
# Or: EUREKA_URI=http://localhost:8761/eureka mvn spring-boot:run
# Access: http://localhost:8888
```

### 3. Start Delivery Service
```bash
cd Backend/delivery-service
java -jar target/delivery-service-1.0.0-SNAPSHOT.jar
# Or: mvn spring-boot:run
# Port: 8085
```

### 4. Start Payment Service
```bash
cd Backend/payment-service
java -jar target/payment-service-1.0.0-SNAPSHOT.jar
# Or: mvn spring-boot:run
# Port: 8084
```

---

## Testing Endpoints

### Discovery Server (Eureka Dashboard)
- Dashboard: `http://localhost:8761`
- API: `http://localhost:8761/eureka/apps`
- Actuator Health: `http://localhost:8761/actuator/health`
- Actuator Info: `http://localhost:8761/actuator/info`

### Config Server
- Actuator Health: `http://localhost:8888/actuator/health`
- Actuator Info: `http://localhost:8888/actuator/info`
- Actuator Env: `http://localhost:8888/actuator/env`

### Delivery Service
- Health: `http://localhost:8085/actuator/health`
- Info: `http://localhost:8085/actuator/info`
- Metrics: `http://localhost:8085/actuator/metrics`
- Beans: `http://localhost:8085/actuator/beans`

### Payment Service
- Health: `http://localhost:8084/actuator/health`
- Info: `http://localhost:8084/actuator/info`
- Metrics: `http://localhost:8084/actuator/metrics`
- H2 Console: `http://localhost:8084/h2-console`

---

## Service Architecture Overview

```
┌─────────────────────────────────────────┐
│          Discovery Server               │
│  (Eureka - Service Registry)            │
│  Port: 8761                             │
│  Knows all services & their locations   │
└─────────────────────────────────────────┘
              ↑                   ↑
              │ registers        │ queries
              │ heartbeat        │
              │                  │
    ┌─────────┴──────────────────┴────────────────────┐
    │                                                   │
┌───┴────────────────┐  ┌──────────────────────────┐  │
│   Config Server    │  │  Delivery Service        │  │
│   Port: 8888       │  │  Port: 8085              │  │
│   (application.yml)│  │  (MySQL/H2 DB)          │  │
└────────────────────┘  └──────────────────────────┘  │
    ↑                           ↑                      │
    │ imports config            │ registered           │
    │                           │                      │
    └──────────────┬────────────┘                      │
                   │                                   │
                   ▼                                   │
        ┌──────────────────────┐                      │
        │  Payment Service     │                      │
        │  Port: 8084          │                      │
        │  (H2 in-memory DB)   │◄─────────────────────┘
        └──────────────────────┘ registered
            ↑
            │ imports config
            │
            ▼
    ┌──────────────────────┐
    │   Config Repo        │
    │  (YAML files)        │
    │  - application.yml   │
    │  - delivery.yml      │
    │  - payment.yml       │
    └──────────────────────┘
```

---

## Configuration Files Summary

| File | Service | Purpose |
|------|---------|---------|
| `discovery-server/src/main/resources/application.properties` | Discovery | Eureka configuration |
| `config-server/src/main/resources/application.yml` | Config | Config server settings |
| `delivery-service/src/main/resources/application.yml` | Delivery | Delivery service config |
| `payment-service/src/main/resources/application.yml` | Payment | Payment service config |
| `config-repo/application.yml` | Shared | Common config for all services |
| `config-repo/delivery.yml` | Delivery | Delivery-specific config |
| `config-repo/payment.yml` | Payment | Payment-specific config |

---

## Team Members & Responsibilities

Each team member should understand:
1. ✓ How Eureka discovery works (service registration & discovery)
2. ✓ How Spring Cloud Config Server distributes configurations
3. ✓ How microservices communicate via service discovery
4. ✓ How Actuator endpoints provide observability
5. ✓ How to run and debug microservices
6. ✓ The complete architecture and data flow

---

## Verification Steps

```bash
# 1. Build all services
mvn clean package -DskipTests=true

# 2. Start services (in separate terminals)
# Terminal 1: Discovery Server (8761)
java -jar Backend/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar

# Terminal 2: Config Server (8888)
java -jar Backend/config-server/target/config-server-1.0.0-SNAPSHOT.jar

# Terminal 3: Delivery Service (8085)
java -jar Backend/delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar

# Terminal 4: Payment Service (8084)
java -jar Backend/payment-service/target/payment-service-1.0.0-SNAPSHOT.jar

# 3. Wait 5-10 seconds for registration
# 4. Check Eureka Dashboard
curl http://localhost:8761/eureka/apps/

# 5. Test services are discoverable
curl http://localhost:8761/eureka/apps/DELIVERY
curl http://localhost:8761/eureka/apps/PAYMENT

# 6. Test Actuator endpoints
curl http://localhost:8085/actuator/health  # Delivery health
curl http://localhost:8084/actuator/info    # Payment info
curl http://localhost:8888/actuator/env     # Config env
```

---

## Common Issues & Solutions

### Issue: Services not showing in Eureka
**Solution:** Check that:
- Discovery Server is running (port 8761)
- Services have `register-with-eureka: true`
- Services can connect to Eureka URL
- Check service logs for connection errors

### Issue: Config server not loading configs
**Solution:** Verify:
- Config Server is running (port 8888)
- Config repo path is correct
- Services have correct `spring.config.import` URL

### Issue: Actuator endpoints return 404
**Solution:** Check:
- Actuator dependency is present in pom.xml
- `management.endpoints.web.exposure.include` is configured
- Endpoint name is correct (e.g., `/actuator/health` not `/health`)

---

**Last Updated:** 2026-11-27
**Status:** Ready for team testing and validation
