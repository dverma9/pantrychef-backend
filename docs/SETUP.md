# PantryChef AI — Setup Guide

> **Day 3** | Backend Foundation Complete

---

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 17.0.13 | Runtime for Spring Boot |
| Maven | 3.9.16 | Build tool and dependency manager |
| PostgreSQL | 16.14 | Database |
| Git | Any | Version control |

---

## 1. Java 17 Installation

Already installed at: `C:\Program Files\Java\jdk-17`

Verify:
```
java -version
```
Expected: `java version "17.0.13"`

---

## 2. Maven Installation

Installed at: `C:\Program Files\Maven\apache-maven-3.9.16`

Added to PATH: `C:\Program Files\Maven\apache-maven-3.9.16\bin`

Verify:
```
mvn -version
```
Expected: `Apache Maven 3.9.16`

---

## 3. PostgreSQL Installation

Installed at: `C:\Program Files\PostgreSQL\16`

Added to PATH: `C:\Program Files\PostgreSQL\16\bin`

### Start PostgreSQL (run every time you restart your computer):
```
"C:\Program Files\PostgreSQL\16\bin\pg_ctl.exe" -D "C:\Program Files\PostgreSQL\16\data" -l "C:\Program Files\PostgreSQL\16\data\logfile.log" start
```

### Stop PostgreSQL:
```
"C:\Program Files\PostgreSQL\16\bin\pg_ctl.exe" -D "C:\Program Files\PostgreSQL\16\data" stop
```

### Check PostgreSQL status:
```
"C:\Program Files\PostgreSQL\16\bin\pg_ctl.exe" -D "C:\Program Files\PostgreSQL\16\data" status
```

### Database credentials:
- **Host:** localhost
- **Port:** 5432
- **Database:** pantrychef
- **Username:** postgres
- **Password:** postgres

---

## 4. Clone and Run the Backend

```
git clone https://github.com/dverma9/pantrychef-backend.git
cd pantrychef-backend
```

Create `application-local.properties` in the project root:
```
claude.api.key=YOUR_CLAUDE_API_KEY
```

Run the server:
```
mvn spring-boot:run
```

Server starts on: `http://localhost:8080`

---

## 5. Verify Everything Works

```
curl http://localhost:8080/api/health
```
Expected: `{"status":"UP","timestamp":"..."}`

```
curl http://localhost:8080/api/pantry
```
Expected: `[]`

---

## 6. Daily Startup Checklist

Every day before starting development:

1. Start PostgreSQL:
```
"C:\Program Files\PostgreSQL\16\bin\pg_ctl.exe" -D "C:\Program Files\PostgreSQL\16\data" -l "C:\Program Files\PostgreSQL\16\data\logfile.log" start
```

2. Start backend server:
```
cd C:\Users\deepi\Documents\pantrychef-backend
mvn spring-boot:run
```

3. Verify health:
```
curl http://localhost:8080/api/health
```

---

## 7. Project Structure

```
pantrychef-backend/
├── docs/                          # All design documents
├── src/main/java/com/pantrychef/
│   ├── config/                    # CorsConfig, AppConfig
│   ├── controller/                # REST controllers
│   ├── dto/                       # Request/Response objects
│   ├── entity/                    # JPA database entities
│   ├── exception/                 # Error handling
│   ├── repository/                # Database access
│   ├── service/                   # Business logic
│   └── PantrychefBackendApplication.java
├── src/main/resources/
│   └── application.properties     # Configuration
├── application-local.properties   # API keys (NOT in Git)
└── pom.xml                        # Maven dependencies
```
