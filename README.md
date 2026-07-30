# 🍽️ PantryChef AI — Backend

> Spring Boot REST API powering PantryChef AI — a conversational AI cooking companion that suggests dishes, generates recipes, and finds missing ingredients based on your actual pantry.

**Live API:** Deployed on Railway.app  
**Frontend:** [cookwithpantrychef-ai.vercel.app](https://cookwithpantrychef-ai.vercel.app)  
**Part of:** [AB Talks 60-Day Claude AI Challenge](https://www.youtube.com/@ABTalks)

---

## 🚀 What This Does

This backend powers three core capabilities:

- **Pantry Manager** — CRUD API for storing ingredients in PostgreSQL
- **AI Chat** — Sends pantry context + user preferences + conversation history to Gemini AI and returns personalised cooking suggestions
- **Preference Memory** — Stores spice level, cuisine preferences, dietary restrictions, and disliked ingredients — applied to every AI interaction

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| AI | Google Gemini API (free tier) |
| HTTP Client | Spring RestTemplate |
| Validation | Jakarta Bean Validation (`@NotBlank`, `@Size`) |
| Utilities | Lombok |
| Deployment | Railway.app |

---

## 📁 Project Structure

```
src/main/java/com/pantrychef/
├── config/
│   ├── AppConfig.java              # RestTemplate bean
│   └── CorsConfig.java             # CORS — allows frontend origins
├── controller/
│   ├── ChatController.java         # POST /api/chat
│   ├── PantryController.java       # GET / POST / DELETE /api/pantry
│   └── PreferenceController.java   # GET / PUT /api/preferences
├── dto/
│   ├── ChatRequest.java            # Validated chat request body
│   ├── ChatResponse.java           # Chat response with reply + timestamp
│   ├── ConversationMessage.java    # Single message in history
│   └── IngredientDto.java          # Ingredient request/response body
├── entity/
│   ├── Ingredient.java             # JPA entity — pantry items
│   └── UserPreference.java         # JPA entity — user preferences
├── exception/
│   └── GlobalExceptionHandler.java # @ControllerAdvice — structured error responses
├── repository/
│   ├── IngredientRepository.java
│   └── PreferenceRepository.java
└── service/
    ├── ClaudeService.java          # Gemini API integration + system prompt builder
    ├── IngredientService.java      # Pantry business logic
    └── PreferenceService.java      # Preferences upsert logic
```

---

## 🔗 API Endpoints

### Pantry

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| `GET` | `/api/pantry` | List all ingredients | — | `200` Array of ingredients |
| `POST` | `/api/pantry` | Add ingredient | `{ name, quantity, unit }` | `201` Created ingredient |
| `DELETE` | `/api/pantry/{id}` | Remove ingredient | — | `204` No Content |

### Chat

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| `POST` | `/api/chat` | Send message to AI | `{ message, conversationHistory[] }` | `200` `{ reply, timestamp }` |

**Chat error responses:**
- `400 Bad Request` — message is blank or exceeds 2000 characters
- `503 Service Unavailable` — Gemini API is unreachable

### Preferences

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|-------------|----------|
| `GET` | `/api/preferences` | Get saved preferences | — | `200` Preferences object |
| `PUT` | `/api/preferences` | Save preferences | `{ spiceLevel, preferredCuisines, dietaryNotes, dislikedIngredients }` | `200` Updated preferences |

### Health

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Backend health check |

---

## ⚙️ Environment Variables

Create a file called `application-local.properties` in the project root for local development (this file is gitignored):

```properties
# Local database
DATABASE_URL=jdbc:postgresql://localhost:5432/pantrychef
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# Gemini API key — get one free at https://aistudio.google.com/
GEMINI_API_KEY=your_gemini_api_key_here

# Allowed frontend origins (comma-separated)
ALLOWED_ORIGINS=http://localhost:5173
```

**For production (Railway.app),** set these as environment variables in the Railway dashboard:

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | Provided by Railway PostgreSQL plugin |
| `DATABASE_USERNAME` | Provided by Railway PostgreSQL plugin |
| `DATABASE_PASSWORD` | Provided by Railway PostgreSQL plugin |
| `GEMINI_API_KEY` | Your Google Gemini API key |
| `ALLOWED_ORIGINS` | Your Vercel frontend URL (comma-separated for multiple) |

---

## 🏃 Running Locally

### Prerequisites

- Java 17+ (`java -version` to check)
- Maven 3.8+ (`mvn -version` to check)
- PostgreSQL running locally
- A free Gemini API key from [aistudio.google.com](https://aistudio.google.com/)

### Steps

**1. Clone the repo**
```bash
git clone https://github.com/dverma9/pantrychef-backend.git
cd pantrychef-backend
```

**2. Create the local database**
```bash
psql -U postgres
CREATE DATABASE pantrychef;
\q
```

**3. Create `application-local.properties`** in the project root with your credentials (see Environment Variables section above)

**4. Run the application**
```bash
mvn spring-boot:run
```

**5. Verify it's running**
```bash
curl http://localhost:8080/api/pantry
# Expected: []

curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

---

## 🤖 How the AI Works

Every chat message goes through this pipeline:

1. **Load pantry** — all ingredients fetched from PostgreSQL
2. **Load preferences** — spice level, cuisines, dietary notes fetched from PostgreSQL
3. **Build system prompt** — pantry + preferences injected into a structured prompt
4. **Append conversation history** — last 20 messages included for context (capped to avoid token overflow)
5. **Call Gemini API** — full prompt sent to `gemini-2.5-flash-lite`
6. **Parse + return** — response extracted and returned to frontend

The system prompt instructs the AI to:
- Suggest only dishes the user can make with current pantry items
- Generate numbered step-by-step recipes on request
- Clearly list missing ingredients when a desired dish can't be made
- Always respect spice level and dietary restrictions
- Never suggest disliked ingredients

---

## 🚂 Deploying to Railway

1. Push this repo to GitHub
2. Go to [railway.app](https://railway.app) → **New Project → Deploy from GitHub**
3. Select this repository
4. Add a **PostgreSQL** plugin to the project
5. Copy the `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` from the PostgreSQL plugin's **Connect** tab
6. Add all environment variables listed above under **Variables** in Railway
7. Railway auto-detects Maven and builds the project
8. Once deployed, note your Railway URL — you'll need it for the frontend `.env`

---

## 🔒 Security Notes

- The Gemini API key is **never** stored in code — always loaded from environment variables
- Single-user app — no authentication required for v1.0
- CORS is restricted to specific frontend origins via `ALLOWED_ORIGINS` env variable
- Input validation on all endpoints via Jakarta Bean Validation

---

## 📄 License

Built as a capstone project for the AB Talks 60-Day Claude AI Challenge.  
Free to use and learn from.

---

*Built with ☕ Java + 🤖 AI — AB Talks 60-Day Claude AI Challenge*
