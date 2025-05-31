#!/bin/bash

set -e

echo "🚀 Loading environment variables..."
if [ ! -f .env ]; then
  echo "❌ .env file not found! Please create one from sample.env"
  exit 1
fi
export "$(grep -v '^#' .env | xargs)"

echo "✅ Running tests..."
./mvnw clean test

echo "📦 Building Spring Boot app..."
./mvnw clean package -DskipTests

echo "🐳 Building Docker image..."
docker build -t weather-service .

echo "🧹 Removing any existing container..."
docker rm -f weather-service-container 2>/dev/null || true

echo "🚀 Starting Docker container..."
docker run -d --name weather-service-container \
  -p "${SERVER_PORT}:${SERVER_PORT}" \
  -e WEATHERSTACK_API_KEY="${WEATHERSTACK_API_KEY}" \
  -e OPENWEATHERMAP_API_KEY="${OPENWEATHERMAP_API_KEY}" \
  -e SERVER_PORT="${SERVER_PORT}" \
  weather-service

echo "⏳ Waiting for container to initialize..."
sleep 10

echo "🌦️ Testing API at http://localhost:${SERVER_PORT}/v1/weather?city=Melbourne"
curl "http://localhost:${SERVER_PORT}/v1/weather?city=Melbourne"