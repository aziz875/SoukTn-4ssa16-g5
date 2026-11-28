#!/bin/bash

# SoukTn Microservices - Startup Script with MySQL
# MySQL: root / 1234

set -e

MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="1234"
EUREKA_URI="http://localhost:8761/eureka"
CONFIG_SERVER_URI="http://localhost:8888"

BACKEND_DIR="Backend"

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   SoukTn Microservices - MySQL (root/1234)                    ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Start Discovery Server
echo "▶ Starting Discovery Server (8761)..."
cd "$BACKEND_DIR"
timeout 300 java -jar discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8761 > /tmp/discovery-server.log 2>&1 &
DISCOVERY_PID=$!
cd ..
sleep 5
echo "  Started (PID: $DISCOVERY_PID)"
echo ""

# Start Config Server
echo "▶ Starting Config Server (8888)..."
cd "$BACKEND_DIR"
timeout 300 java -jar config-server/target/config-server-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8888 \
  --EUREKA_URI=$EUREKA_URI > /tmp/config-server.log 2>&1 &
CONFIG_PID=$!
cd ..
sleep 5
echo "  Started (PID: $CONFIG_PID)"
echo ""

# Start API Gateway
echo "▶ Starting API Gateway (8081)..."
cd "$BACKEND_DIR"
timeout 300 java -jar api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar \
  --SERVER_PORT=8081 \
  --EUREKA_URI=$EUREKA_URI \
  --SPRING_CONFIG_IMPORT=$CONFIG_SERVER_URI > /tmp/api-gateway.log 2>&1 &
API_GATEWAY_PID=$!
cd ..
sleep 5
echo "  Started (PID: $API_GATEWAY_PID)"
echo ""

# Start Delivery Service
echo "▶ Starting Delivery Service (8085)..."
cd "$BACKEND_DIR"
timeout 300 java -jar delivery-service/target/delivery-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=$MYSQL_HOST \
  --DB_PORT=$MYSQL_PORT \
  --DB_NAME=delivery_db \
  --DB_USER=$MYSQL_USER \
  --DB_PASSWORD=$MYSQL_PASSWORD \
  --SERVER_PORT=8085 \
  --EUREKA_URI=$EUREKA_URI \
  --SPRING_CONFIG_IMPORT=$CONFIG_SERVER_URI > /tmp/delivery-service.log 2>&1 &
DELIVERY_PID=$!
cd ..
sleep 5
echo "  Started (PID: $DELIVERY_PID)"
echo ""

# Start Payment Service
echo "▶ Starting Payment Service (8084)..."
cd "$BACKEND_DIR"
timeout 300 java -jar payment-service/target/payment-service-1.0.0-SNAPSHOT.jar \
  --DB_HOST=$MYSQL_HOST \
  --DB_PORT=$MYSQL_PORT \
  --DB_NAME=payment_db \
  --DB_USER=$MYSQL_USER \
  --DB_PASSWORD=$MYSQL_PASSWORD \
  --SERVER_PORT=8084 \
  --EUREKA_URI=$EUREKA_URI \
  --SPRING_CONFIG_IMPORT=$CONFIG_SERVER_URI > /tmp/payment-service.log 2>&1 &
PAYMENT_PID=$!
cd ..
sleep 5
echo "  Started (PID: $PAYMENT_PID)"
echo ""

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   All Services Started!                                        ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "GATEWAY & DISCOVERY"
echo "═══════════════════════════════════════════════════════════════"
echo "  API Gateway (8081):"
echo "    Main: http://localhost:8081"
echo "    Swagger: http://localhost:8081/swagger-ui.html"
echo "    Actuator: http://localhost:8081/actuator"
echo "    Health: http://localhost:8081/actuator/health"
echo "    Metrics: http://localhost:8081/actuator/metrics"
echo ""
echo "  Eureka Discovery (8761):"
echo "    Dashboard: http://localhost:8761"
echo "    Registered Services: http://localhost:8761/eureka/apps/"
echo "    Actuator: http://localhost:8761/actuator"
echo "    Health: http://localhost:8761/actuator/health"
echo ""
echo "  Config Server (8888):"
echo "    Actuator: http://localhost:8888/actuator"
echo "    Health: http://localhost:8888/actuator/health"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "MICROSERVICES (via API Gateway)"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "  Payment Service (8084):"
echo "    Direct: http://localhost:8084"
echo "    via Gateway: http://localhost:8081/api/payments"
echo "    Swagger (direct): http://localhost:8084/swagger-ui.html"
echo "    Swagger (via gateway): http://localhost:8081/swagger-ui.html?urls.primaryName=Payment"
echo "    Actuator: http://localhost:8084/actuator"
echo "    Health: http://localhost:8084/actuator/health"
echo ""
echo "  Delivery Service (8085):"
echo "    Direct: http://localhost:8085"
echo "    via Gateway: http://localhost:8081/api/deliveries"
echo "    Swagger (direct): http://localhost:8085/swagger-ui.html"
echo "    Swagger (via gateway): http://localhost:8081/swagger-ui.html?urls.primaryName=Delivery"
echo "    Actuator: http://localhost:8085/actuator"
echo "    Health: http://localhost:8085/actuator/health"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "QUICK TEST COMMANDS (via API Gateway LoadBalancer)"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "  curl http://localhost:8081/actuator/health"
echo "  curl http://localhost:8761/actuator/health"
echo "  curl http://localhost:8888/actuator/health"
echo "  curl http://localhost:8084/actuator/health"
echo "  curl http://localhost:8085/actuator/health"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "Stop: Press Ctrl+C"
echo "═══════════════════════════════════════════════════════════════"
echo ""

trap "echo 'Stopping...'; kill $DISCOVERY_PID $CONFIG_PID $API_GATEWAY_PID $DELIVERY_PID $PAYMENT_PID 2>/dev/null; exit" INT TERM

wait
