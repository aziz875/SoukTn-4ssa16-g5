# SoukTn Microservices - Configuration & Access Points

## 🏗️ Architecture Overview
- **API Gateway** (Port 8081): Single entry point with round-robin load balancing
- **Config Server** (Port 8888): Centralized configuration management
- **Discovery Server** (Port 8761): Eureka service registry with health checks
- **Microservices**: Auto-discovered via Eureka & load-balanced through API Gateway

---

## 📡 Port Allocation & Services

| Service | Port | Service Name (Eureka) | Via Gateway | Status |
|---------|------|----------------------|-------------|--------|
| Discovery Server | 8761 | discovery-server | N/A | ✅ Running |
| Config Server | 8888 | config-server | N/A | ✅ Running |
| API Gateway | 8081 | api-gateway | N/A | ✅ Running |
| Payment Service | 8084 | payment | `/api/payments/**` | ✅ Exists |
| Delivery Service | 8085 | delivery | `/api/deliveries/**` | ✅ Exists |
| Catalog Service | 8082 | catalog | `/api/catalog/**` | ⏳ To be created |
| Order Service | 8083 | order | `/api/orders/**` | ⏳ To be created |
| User Service | 8089 | user | `/api/users/**` | ⏳ To be created |
| Notification Service | 8087 | notification | `/api/notifications/**` | ⏳ To be created |
| Dispute Service | 8086 | dispute | `/api/disputes/**` | ⏳ To be created |

---

## 🌐 Access URLs

### Gateway & Discovery
```
API Gateway Main:          http://localhost:8081
API Gateway Swagger UI:    http://localhost:8081/swagger-ui.html
API Gateway Actuator:      http://localhost:8081/actuator
API Gateway Health:        http://localhost:8081/actuator/health

Eureka Dashboard:          http://localhost:8761
Eureka Services:           http://localhost:8761/eureka/apps/
Eureka Health:             http://localhost:8761/actuator/health

Config Server:             http://localhost:8888
Config Server Health:      http://localhost:8888/actuator/health
```

### Payment Service (via API Gateway)
```
Direct Access:             http://localhost:8084
Direct Swagger:            http://localhost:8084/swagger-ui.html
Direct Actuator:           http://localhost:8084/actuator
Direct Health:             http://localhost:8084/actuator/health

Via Gateway:               http://localhost:8081/api/payments
Via Gateway (Swagger):     http://localhost:8081/swagger-ui.html?urls.primaryName=Payment
Via Gateway (API Docs):    http://localhost:8081/api/payments/v3/api-docs
```

### Delivery Service (via API Gateway)
```
Direct Access:             http://localhost:8085
Direct Swagger:            http://localhost:8085/swagger-ui.html
Direct Actuator:           http://localhost:8085/actuator
Direct Health:             http://localhost:8085/actuator/health

Via Gateway:               http://localhost:8081/api/deliveries
Via Gateway (Swagger):     http://localhost:8081/swagger-ui.html?urls.primaryName=Delivery
Via Gateway (API Docs):    http://localhost:8081/api/deliveries/v3/api-docs
```

---

## 🔄 Load Balancing Configuration

### Round-Robin Load Balancer
- **Type**: Spring Cloud Load Balancer (Round-Robin)
- **Configuration Location**: `Backend/api-gateway/src/main/resources/application.yml`
- **Retry Policy**: 3 retries on `SERVICE_UNAVAILABLE` or `BAD_GATEWAY`
- **Cache TTL**: 35 seconds for service discovery

```yaml
spring.cloud.loadbalancer:
  ribbon.enabled: false           # Disabled old Ribbon LB
  round-robin.enabled: true       # Round-Robin enabled
  cache.ttl: 35s                  # Service cache validity
```

### Gateway Routing (Load Balanced)
Each route uses `lb://<service-name>` for automatic load balancing:
- `lb://payment` → Payment Service(s) on 8084
- `lb://delivery` → Delivery Service(s) on 8085
- `lb://catalog` → Catalog Service(s) on 8082
- `lb://order` → Order Service(s) on 8083
- `lb://user` → User Service(s) on 8089
- `lb://notification` → Notification Service(s) on 8087
- `lb://dispute` → Dispute Service(s) on 8086

---

## 📋 Configuration Files

### Central Config Locations
- **API Gateway Config**: `Backend/api-gateway/src/main/resources/application.yml`
- **Service Configs**: `Backend/config-repo/<service-name>.yml`
- **Shared Config**: `Backend/config-repo/application.yml`

### Service Configuration Details

**Payment Service** (`Backend/config-repo/payment.yml`)
```yaml
server.port: 8084
springdoc.swagger-ui.path: /swagger-ui.html
springdoc.api-docs.path: /v3/api-docs
```

**Delivery Service** (`Backend/config-repo/delivery.yml`)
```yaml
server.port: 8085
springdoc.swagger-ui.path: /swagger-ui.html
springdoc.api-docs.path: /v3/api-docs
```

**All Other Services** (`Backend/config-repo/{catalog,order,user,notification,dispute}.yml`)
```yaml
server.port: {8082,8083,8089,8087,8086}
springdoc.swagger-ui.path: /swagger-ui.html
springdoc.api-docs.path: /v3/api-docs
```

---

## 🔍 Actuator Endpoints (Observability)

### Exposed Endpoints (All Services)
```
/actuator                  → List of all endpoints
/actuator/health          → Service health status
/actuator/info            → Application info (version, team)
/actuator/metrics         → Performance metrics
/actuator/env             → Environment variables
/actuator/beans           → Spring beans
/actuator/mappings        → HTTP request mappings
/actuator/configprops     → Configuration properties
/actuator/loggers         → Logger management
```

### Quick Health Check Commands
```bash
# API Gateway
curl http://localhost:8081/actuator/health

# Discovery Server
curl http://localhost:8761/actuator/health

# Config Server
curl http://localhost:8888/actuator/health

# Payment Service
curl http://localhost:8084/actuator/health

# Delivery Service
curl http://localhost:8085/actuator/health
```

---

## 🚀 Starting Services

### Quick Start
```bash
./run-services.sh
```

This script will:
1. Start Discovery Server (8761)
2. Start Config Server (8888)
3. Start API Gateway (8081)
4. Start Payment Service (8084)
5. Start Delivery Service (8085)
6. Display all access URLs

### Individual Service Startup
```bash
# Payment Service
java -jar Backend/payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8084 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888

# Delivery Service
java -jar Backend/delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8085 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

---

## ✅ Verification Checklist

- [x] Port allocation fixed (user service: 8089 instead of 8081)
- [x] Round-robin load balancer configured in API Gateway
- [x] All services have Swagger UI endpoints
- [x] All services expose Actuator endpoints
- [x] Gateway routes configured for all 7 services
- [x] Service names match Eureka registration names
- [x] retry policy configured (3 retries)
- [x] Cache TTL set for load balancer (35s)
- [x] run-services.sh updated with all access URLs

---

## 🧪 Testing

### Test Gateway Routing
```bash
# Test Payment API through gateway
curl http://localhost:8081/api/payments/v1/payments

# Test Delivery API through gateway
curl http://localhost:8081/api/deliveries/v1/deliveries
```

### Test Load Balancing
```bash
# Check if payment service is registered in Eureka
curl http://localhost:8761/eureka/apps/payment | grep -i "instanceid"

# Check all registered services
curl http://localhost:8761/eureka/apps/ | grep -E '<name|<port'
```

### Test Actuator Endpoints
```bash
# Full health details
curl http://localhost:8081/actuator/health | jq .

# Metrics
curl http://localhost:8081/actuator/metrics | jq .

# Configuration properties
curl http://localhost:8081/actuator/configprops | jq .
```

---

## 📝 Notes

- **Service Discovery**: Services auto-register with Eureka on startup
- **Configuration Priority**: Service-specific config overrides common config
- **Load Balancing**: Round-robin distributes requests across multiple instances
- **Health Checks**: Eureka performs health checks every 30 seconds
- **API Documentation**: Swagger UI aggregates all service APIs through the gateway
