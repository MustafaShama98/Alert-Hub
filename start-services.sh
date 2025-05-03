#!/bin/bash

# Set paths to your Spring Boot microservices
EUREKA_PATH=./eureka-discovery-service-server
SECURITY_PATH=./security-service
GATEWAY_PATH=./api-gateway

echo "🚀 Starting Eureka Server..."
cd $EUREKA_PATH
./mvnw spring-boot:run &
EUREKA_PID=$!
cd -

echo "⏳ Waiting 8 seconds for Eureka to register..."
sleep 8

echo "🔐 Starting Security Service..."
cd $SECURITY_PATH
./mvnw spring-boot:run &
SECURITY_PID=$!
cd -

echo "⏳ Waiting 5 seconds for Security Service to register..."
sleep 5

echo "🌐 Starting API Gateway..."
cd $GATEWAY_PATH
./mvnw spring-boot:run &
GATEWAY_PID=$!
cd -

echo "✅ All services started. (PIDs: $EUREKA_PID, $SECURITY_PID, $GATEWAY_PID)"

# Optional: Wait for processes to keep terminal open
wait
