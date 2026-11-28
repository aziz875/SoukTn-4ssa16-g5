# SoukTn Microservices - Setup & Installation Guide

**Cross-Platform Compatible** ✓ Linux • macOS • Windows

---

## 📋 Prerequisites

### Required (All Platforms)

1. **Java Development Kit (JDK) 21**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version` (should show Java 21)

2. **Maven 3.x**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **MySQL 8.0+**
   - Download: https://dev.mysql.com/downloads/mysql/
   - **Default Setup:**
     - Host: `localhost`
     - Port: `3306`
     - Root User: `root`
     - Root Password: `1234`
   - Verify: `mysql -u root -p1234` (should connect)

---

## 🚀 Quick Start

### **Linux & macOS**
```bash
./run-services.sh
```

### **Windows**
```cmd
run-services.bat
```

---

## 🔧 Manual Build & Run

### Step 1: Build All Services
```bash
cd Backend
mvn clean install -DskipTests
cd ..
```

**Expected Output:** `BUILD SUCCESS` with all 6 modules

### Step 2: Verify JAR Files
Ensure these JAR files exist:
- `Backend/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar`
- `Backend/config-server/target/config-server-1.0.0-SNAPSHOT.jar`
- `Backend/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar`
- `Backend/delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar`
- `Backend/payment-service/target/payment-service-1.0.0-SNAPSHOT.jar`

### Step 3: Start Services (Manual)

**Terminal 1 - Discovery Server:**
```bash
java -jar Backend/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar --SERVER_PORT=8761
```

**Terminal 2 - Config Server:**
```bash
java -jar Backend/config-server/target/config-server-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8888 \
  --EUREKA_URI=http://localhost:8761/eureka
```

**Terminal 3 - API Gateway:**
```bash
java -jar Backend/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8081 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

**Terminal 4 - Delivery Service:**
```bash
java -jar Backend/delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=delivery_db \
  --DB_USER=root \
  --DB_PASSWORD=1234 \
  --SERVER_PORT=8085 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

**Terminal 5 - Payment Service:**
```bash
java -jar Backend/payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=localhost \
  --DB_PORT=3306 \
  --DB_NAME=payment_db \
  --DB_USER=root \
  --DB_PASSWORD=1234 \
  --SERVER_PORT=8084 \
  --EUREKA_URI=http://localhost:8761/eureka \
  --SPRING_CONFIG_IMPORT=http://localhost:8888
```

---

## 🌐 Access URLs

| Component | URL |
|-----------|-----|
| **API Gateway** | http://localhost:8081 |
| **Swagger UI** | http://localhost:8081/swagger-ui.html |
| **Eureka Dashboard** | http://localhost:8761 |
| **Payment API** | http://localhost:8081/api/payments |
| **Delivery API** | http://localhost:8081/api/deliveries |

---

## 🩺 Health Checks

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

## ⚠️ Troubleshooting

### **Port Already in Use**
```bash
# Linux/macOS: Find process using port
lsof -i :8081

# Windows: Find process using port
netstat -ano | findstr :8081

# Kill process
# Linux/macOS: kill -9 <PID>
# Windows: taskkill /PID <PID> /F
```

### **MySQL Connection Failed**
```bash
# Verify MySQL is running
# Linux/macOS:
sudo systemctl status mysql

# Windows:
sc query MySQL

# Start MySQL if stopped
# Linux: sudo systemctl start mysql
# macOS: brew services start mysql
# Windows: net start MySQL80
```

### **Java Not Found**
```bash
# Check Java installation
java -version

# Add to PATH if needed
# Linux/macOS: export PATH=$PATH:/path/to/java/bin
# Windows: Set PATH environment variable
```

### **Build Fails**
```bash
# Clean cache and retry
mvn clean
mvn install -DskipTests
```

### **Services Don't Start**
1. Verify Java 21 is installed: `java -version`
2. Verify MySQL is running and accessible
3. Check if ports 8761, 8888, 8081, 8084, 8085 are available
4. Check logs for errors:
   - Linux/macOS: See output in terminal
   - Windows: Errors visible in separate command windows

---

## 🔄 Database Initialization

Databases are created automatically:
- **On first run:** Databases created with `createDatabaseIfNotExist=true`
- **Schema:** Auto-created by Hibernate (`ddl-auto: update`)
- **User:** root / 1234

No manual database setup required! ✓

---

## 📊 Service Ports

| Service | Port | Type |
|---------|------|------|
| Discovery Server | 8761 | Registry |
| Config Server | 8888 | Configuration |
| API Gateway | 8081 | Load Balancer |
| Payment Service | 8084 | Microservice |
| Delivery Service | 8085 | Microservice |
| MySQL | 3306 | Database |

**All ports must be available on target system**

---

## 🐳 Docker Alternative (Optional)

For complete containerization:
```bash
# Docker Compose (future)
docker-compose up -d
```

---

## 📝 Configuration Files

- **Global Config:** `Backend/config-repo/application.yml`
- **Service Configs:** `Backend/config-repo/{service-name}.yml`
- **Gateway Config:** `Backend/api-gateway/src/main/resources/application.yml`

Modify before running services to change:
- Ports
- Database credentials
- Eureka URL
- Logging levels

---

## ✅ Verification Checklist

Before running in production:
- [ ] Java 21 installed
- [ ] Maven installed
- [ ] MySQL running with root/1234
- [ ] Ports 8761, 8888, 8081, 8084, 8085 available
- [ ] Build succeeds: `mvn clean install`
- [ ] All 5 JAR files created
- [ ] Services start without errors
- [ ] Health endpoints respond

---

## 🚨 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Port 8081 already in use | Kill process on port 8081 or change `--SERVER_PORT` |
| MySQL connection refused | Start MySQL service |
| Java version error | Install JDK 21 (not JRE) |
| Config server not found | Ensure Config Server starts first |
| Services won't register | Verify Eureka server is running |

---

## 📞 Support

For issues, check:
1. MICROSERVICES_CONFIG.md - Complete configuration reference
2. Terminal logs - Detailed error messages
3. Actuator endpoints - Health and diagnostics
4. Config files - Verify settings match environment

---

## 🎯 Next Steps

1. **Build:** `mvn clean install`
2. **Start:** `./run-services.sh` (or `run-services.bat` on Windows)
3. **Test:** Open http://localhost:8081
4. **Deploy:** Push changes to production infrastructure

---

**All OS Supported:** Linux • macOS • Windows  
**Latest Versions:** Spring Boot 3.3.4 • Spring Cloud 2024.0.0 • Java 21
