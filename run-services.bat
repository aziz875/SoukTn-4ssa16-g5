@echo off
REM SoukTn Microservices - Startup Script for Windows
REM MySQL: root / 1234

setlocal enabledelayedexpansion

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=1234
set EUREKA_URI=http://localhost:8761/eureka
set CONFIG_SERVER_URI=http://localhost:8888

set BACKEND_DIR=Backend

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   SoukTn Microservices - MySQL (root/1234)                    ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Start Discovery Server
echo ▶ Starting Discovery Server (8761)...
start "Discovery Server" java -jar %BACKEND_DIR%\discovery-server\target\discovery-server-1.0.0-SNAPSHOT.jar ^
  --SERVER_PORT=8761
timeout /t 5 /nobreak > nul
echo   Started (PID: auto-managed by Windows)
echo.

REM Start Config Server
echo ▶ Starting Config Server (8888)...
start "Config Server" java -jar %BACKEND_DIR%\config-server\target\config-server-1.0.0-SNAPSHOT.jar ^
  --SERVER_PORT=8888 ^
  --EUREKA_URI=%EUREKA_URI%
timeout /t 5 /nobreak > nul
echo   Started (PID: auto-managed by Windows)
echo.

REM Start API Gateway
echo ▶ Starting API Gateway (8081)...
start "API Gateway" java -jar %BACKEND_DIR%\api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar ^
  --SERVER_PORT=8081 ^
  --EUREKA_URI=%EUREKA_URI% ^
  --SPRING_CONFIG_IMPORT=%CONFIG_SERVER_URI%
timeout /t 5 /nobreak > nul
echo   Started (PID: auto-managed by Windows)
echo.

REM Start Delivery Service
echo ▶ Starting Delivery Service (8085)...
start "Delivery Service" java -jar %BACKEND_DIR%\delivery-service\target\delivery-service-1.0.0-SNAPSHOT.jar ^
  --DB_HOST=%MYSQL_HOST% ^
  --DB_PORT=%MYSQL_PORT% ^
  --DB_NAME=delivery_db ^
  --DB_USER=%MYSQL_USER% ^
  --DB_PASSWORD=%MYSQL_PASSWORD% ^
  --SERVER_PORT=8085 ^
  --EUREKA_URI=%EUREKA_URI% ^
  --SPRING_CONFIG_IMPORT=%CONFIG_SERVER_URI%
timeout /t 5 /nobreak > nul
echo   Started (PID: auto-managed by Windows)
echo.

REM Start Payment Service
echo ▶ Starting Payment Service (8084)...
start "Payment Service" java -jar %BACKEND_DIR%\payment-service\target\payment-service-1.0.0-SNAPSHOT.jar ^
  --DB_HOST=%MYSQL_HOST% ^
  --DB_PORT=%MYSQL_PORT% ^
  --DB_NAME=payment_db ^
  --DB_USER=%MYSQL_USER% ^
  --DB_PASSWORD=%MYSQL_PASSWORD% ^
  --SERVER_PORT=8084 ^
  --EUREKA_URI=%EUREKA_URI% ^
  --SPRING_CONFIG_IMPORT=%CONFIG_SERVER_URI%
timeout /t 5 /nobreak > nul
echo   Started (PID: auto-managed by Windows)
echo.

echo ╔════════════════════════════════════════════════════════════════╗
echo ║   All Services Started!                                        ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo ═══════════════════════════════════════════════════════════════
echo GATEWAY ^& DISCOVERY
echo ═══════════════════════════════════════════════════════════════
echo   API Gateway (8081):
echo     Main: http://localhost:8081
echo     Swagger: http://localhost:8081/swagger-ui.html
echo     Actuator: http://localhost:8081/actuator
echo     Health: http://localhost:8081/actuator/health
echo     Metrics: http://localhost:8081/actuator/metrics
echo.
echo   Eureka Discovery (8761):
echo     Dashboard: http://localhost:8761
echo     Registered Services: http://localhost:8761/eureka/apps/
echo     Actuator: http://localhost:8761/actuator
echo     Health: http://localhost:8761/actuator/health
echo.
echo   Config Server (8888):
echo     Actuator: http://localhost:8888/actuator
echo     Health: http://localhost:8888/actuator/health
echo.
echo ═══════════════════════════════════════════════════════════════
echo MICROSERVICES (via API Gateway)
echo ═══════════════════════════════════════════════════════════════
echo.
echo   Payment Service (8084):
echo     Direct: http://localhost:8084
echo     via Gateway: http://localhost:8081/api/payments
echo     Swagger (direct): http://localhost:8084/swagger-ui.html
echo     Swagger (via gateway): http://localhost:8081/swagger-ui.html?urls.primaryName=Payment
echo     Actuator: http://localhost:8084/actuator
echo     Health: http://localhost:8084/actuator/health
echo.
echo   Delivery Service (8085):
echo     Direct: http://localhost:8085
echo     via Gateway: http://localhost:8081/api/deliveries
echo     Swagger (direct): http://localhost:8085/swagger-ui.html
echo     Swagger (via gateway): http://localhost:8081/swagger-ui.html?urls.primaryName=Delivery
echo     Actuator: http://localhost:8085/actuator
echo     Health: http://localhost:8085/actuator/health
echo.
echo ═══════════════════════════════════════════════════════════════
echo QUICK TEST COMMANDS
echo ═══════════════════════════════════════════════════════════════
echo.
echo   curl http://localhost:8081/actuator/health
echo   curl http://localhost:8761/actuator/health
echo   curl http://localhost:8888/actuator/health
echo   curl http://localhost:8084/actuator/health
echo   curl http://localhost:8085/actuator/health
echo.
echo Services are running in separate windows. Close window(s) to stop services.
echo ═══════════════════════════════════════════════════════════════
echo.
pause
