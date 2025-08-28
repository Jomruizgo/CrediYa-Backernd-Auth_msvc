# Auth Microservice Deployment

## Local Development

### Prerequisites
- Docker & Docker Compose
- Java 21
- Gradle

### Quick Start

1. **Start PostgreSQL database:**
   ```bash
   cd deployment
   docker compose up -d
   ```

2. **Run the application:**
   ```bash
   cd ..
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

3. **Access the application:**
   - API: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui.html
   - Health: http://localhost:8081/actuator/health

### Environment Variables

Create `deployment/.env` to customize:
```bash
DB_NAME=mydatabase
DB_USERNAME=myuser
DB_PASSWORD=secret
DB_PORT=5433
```

## Production Deployment

### Environment Variables for Production
```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=production-db-host
DB_PORT=5432
DB_NAME=crediya_auth
DB_SCHEMA=public
DB_USERNAME=auth_user
DB_PASSWORD=secure_password
```