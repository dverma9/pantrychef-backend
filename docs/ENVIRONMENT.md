# PantryChef AI — Environment Configuration

> **Day 3** | All environment variables, tools, and configuration

---

## 1. Development Environment

| Variable | Value | Location |
|----------|-------|----------|
| JAVA_HOME | `C:\Program Files\Java\jdk-17` | System Environment |
| PATH (Maven) | `C:\Program Files\Maven\apache-maven-3.9.16\bin` | System PATH |
| PATH (PostgreSQL) | `C:\Program Files\PostgreSQL\16\bin` | System PATH |

---

## 2. Application Configuration

### application.properties (committed to Git)

```properties
# Application
spring.application.name=pantrychef-backend
spring.config.import=optional:file:./application-local.properties
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/pantrychef
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Claude API
claude.api.key=${CLAUDE_API_KEY:}
claude.api.url=https://api.anthropic.com/v1/messages
claude.model=claude-sonnet-4-6

# CORS
allowed.origins=http://localhost:5173
```

### application-local.properties (NOT committed to Git)

```properties
# Claude API Key — keep secret, never commit
claude.api.key=sk-ant-xxxxx
```

---

## 3. Environment Variables — Production (Railway.app)

These will be set on Day 9 during deployment:

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | Railway PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | Railway PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | Railway PostgreSQL password |
| `CLAUDE_API_KEY` | Anthropic API key |
| `ALLOWED_ORIGINS` | Vercel frontend URL |

---

## 4. Claude API

| Setting | Value |
|---------|-------|
| Endpoint | `https://api.anthropic.com/v1/messages` |
| Model | `claude-sonnet-4-6` |
| Max tokens | 1024 |
| API version header | `anthropic-version: 2023-06-01` |
| Auth header | `x-api-key: {your-key}` |
| Key expiry | 30 days from creation |

**Get/manage keys at:** https://console.anthropic.com

---

## 5. Database

| Setting | Value |
|---------|-------|
| Host | localhost |
| Port | 5432 |
| Database name | pantrychef |
| Username | postgres |
| Password | postgres |
| Tables auto-created | Yes (Hibernate DDL = update) |

### Tables created:
- `ingredients` — pantry items
- `user_preferences` — cooking preferences

---

## 6. Security Notes

- `application-local.properties` is in `.gitignore` — never committed
- Claude API key never appears in any committed file
- Production API key set as Railway environment variable on Day 9
- Database password changed from default for production on Day 9

---

## 7. Ports

| Service | Port | URL |
|---------|------|-----|
| Spring Boot backend | 8080 | http://localhost:8080 |
| React frontend (Day 4) | 5173 | http://localhost:5173 |
| PostgreSQL | 5432 | jdbc:postgresql://localhost:5432/pantrychef |
