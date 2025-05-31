# Weather Service

A Spring Boot service that fetches weather data from multiple providers, caches results in memory for **3 seconds**, and returns **stale data if all providers fail**

---

## Features

- Returns temperature and wind speed.
- Supports multiple weather providers:
    - [WeatherStack](https://weatherstack.com/)
    - [OpenWeatherMap](https://openweathermap.org/)
- Automatic **failover** between providers if one is down.
- **Caching** of results for up to 3 seconds to reduce API calls.
- Gracefully serves **stale data** when all providers are down.
- The architecture is easy to extend for adding more providers by following the Strategy pattern. To add a new provider, simply define its order and integrate it into the service.
- The application is launchable via the `./run.sh` script for easy local execution.
- The service is fully unit tested with over 80% test coverage.

---

## Technologies

- Java 17
- Spring Boot 3
- WebClient
- Docker
- JUnit + Mockito for testing

## Requirement 
> “Weather results are fine to be cached for up to 3 seconds on the server in normal behaviour to prevent hitting weather providers. Those results must be served as stale if all weather providers are down.”

| Scenario                         | Behavior                   |
|----------------------------------|----------------------------|
| Provider returns success         | Cached + returned          |
| Fresh cache (< 3s) available     | Returned without provider call |
| Providers fail, cache expired    | Stale cache returned       |
| Providers fail, no cache         | Exception thrown           |
---

## How to Run

### 1. Create `.env` file or use `sample.env`

```env
WEATHERSTACK_API_KEY=your_key
OPENWEATHERMAP_API_KEY=your_key
SERVER_PORT=availble_port
```
### 2. Start service
```
chmod +x run.sh
./run.sh
```

## Project Structure
```
.
├── run.sh
├── sample.env
├── pom.xml
├── Dockerfile
├── README.md
└── src/
    ├── main/
    │   ├── java/com/zai/weather/
    │   │   ├── WeatherApplication.java
    │   │   ├── controller/              # REST endpoint
    │   │   ├── service/                 # Core logic & cache
    │   │   ├── provider/                # Weather APIs
    │   │   ├── model/                   # Response + cache wrapper
    │   │   └── exception/               # Custom exception handling
    │   └── resources/application.yml
    └── test/java/com/zai/weather/
```

## Running Tests
``` mvn clean install test```

## API Usage

### GET `/v1/weather?city=Melbourne`

Returns:

```json
{
  "temperature_degrees": 25.0,
  "wind_speed": 10.0
}
```

### Example Requests

#### 1. Local (Default Port 8080)

```
curl "http://localhost:8080/v1/weather?city=Melbourne"
```

#### 2. Hosted (Free-tier Platform)
```
curl "https://weather-api-dh7q.onrender.com/v1/weather?city=Melbourne"
```

##### Note: This service is hosted on a free-tier platform. Request delays may occur. Service may also sleep after periods of inactivity.


##  Trade-offs & What Could Be Improved
| Area                    | Status / Comment                                                                                                                                       |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Authentication & Authorization** | Not implemented. In a real-world setup, users should be authenticated via API keys, OAuth2, or JWT tokens before accessing weather data.               |
| **Rate Limiting**  | Not included. APIs should enforce request limits per user/IP to prevent abuse. |
| **Usage Metrics**  | Not implemented. No Prometheus/Grafana or logs to track fresh vs stale cache hits, request count per provider, etc. |
| **API Documentation**       | Swagger/OpenAPI UI not exposed. Could be auto-generated with `springdoc-openapi` for easy testing and integration.                                     |
| **Error Response Standardization** | Minimal. Uses custom exception class but lacks fully customization.                                                                                    |
| **Retry / Circuit Breaker**  | Not added. For provider failure handling, a circuit breaker (e.g., Resilience4j) would protect from downstream overloads.                              |
| **Scalability (Cache Sharing)** | Limited. Current cache is in-memory per instance. In multi-node setups, a distributed cache (e.g., Redis) would be required.                           |
| **Security Headers / HTTPS** | Assumes internal or local testing. In real deployment, would need TLS, secure headers, and gateway enforcement.                                        |
| **Localization / Units**     | Responses are static (e.g., temperature in °C). Should allow customization via query params or headers.                                                |