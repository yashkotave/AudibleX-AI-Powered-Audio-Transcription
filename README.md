# AudibleX — AI-Powered Audio Transcription Platform

AudibleX is a local full-stack audio transcription application. Its React dashboard uploads recordings to a Spring Boot API, which sends them to OpenAI's audio transcription endpoint and stores successful results in MySQL.

## Features

- Drag-and-drop and file-picker audio upload with filename, size, format, and 25 MB checks.
- Real OpenAI Whisper transcription; there are no mock transcripts or fallback AI responses.
- Searchable transcription library with view, copy, update API, and delete API.
- REST API with validation, consistent error responses, and CORS configuration.
- Responsive React dashboard with upload/loading/error states.

## Architecture

```text
frontend/                 React 18 + Vite dashboard
  src/api.js              Fetch client for the backend API
  src/components/         Navigation, upload, transcript, and history components
backend/                  Spring Boot 3.4 / Java 21 API
  .../controller/         REST endpoints
  .../service/            Business rules and OpenAI API client
  .../repository/         Spring Data JPA persistence
  .../entity/             MySQL transcription entity
  .../dto/                API request and response types
  .../exception/          Centralized API error handling
  .../config/             CORS setup
```

## Prerequisites

- Java 21
- Apache Maven 3.9+
- Node.js 20+ and npm
- MySQL 8+
- An OpenAI API key with access to audio transcription

## Local setup

### 1. Create the database

Start MySQL and create a local database/user. For example, in the MySQL client:

```sql
CREATE DATABASE audiblex CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'audiblex_app'@'localhost' IDENTIFIED BY 'choose-a-local-password';
GRANT ALL PRIVILEGES ON audiblex.* TO 'audiblex_app'@'localhost';
```

Hibernate creates or updates the `transcriptions` table on API startup. The table includes `id`, `title`, `transcript`, `audio_file_name`, and `created_at`; Java maps the last two fields to `audioFileName` and `createdAt`.

### 2. Configure and start the backend

Copy `.env.example` as a reference, then set the values in the terminal or IDE run configuration. Spring Boot does **not** automatically load a `.env` file.

PowerShell example (replace the placeholder with your own credentials):

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/audiblex?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME = "audiblex_app"
$env:DB_PASSWORD = "choose-a-local-password"
$env:OPENAI_API_KEY = "your-openai-api-key"
$env:OPENAI_MODEL = "whisper-1"
cd backend
mvn spring-boot:run
```

The backend listens on `http://localhost:8080`. The OpenAI key is read only by the backend and must never be put in a `VITE_` variable or frontend file. To obtain an API key, create one in the [OpenAI API platform](https://platform.openai.com/api-keys) and keep it private. API use may require billing/account access.

### 3. Configure and start the frontend

In another terminal:

```powershell
cd frontend
Copy-Item .env.example .env.local
npm install
npm run dev
```

Open the Vite URL shown in the terminal (normally `http://localhost:5173`). Set `VITE_API_BASE_URL` in `frontend/.env.local` only if the API is hosted at a different base URL. The frontend uses native Fetch and lets the browser set the multipart content type and boundary.

## OpenAI transcription behavior

The backend calls `POST https://api.openai.com/v1/audio/transcriptions` with the original audio file and `model=whisper-1` in a multipart form, then reads the JSON `text` response. `OPENAI_MODEL` can override the model. Only a successful non-empty OpenAI response is stored.

Uploads are limited to 25 MB and accept `.mp3`, `.mp4`, `.mpeg`, `.mpga`, `.m4a`, `.wav`, or `.webm`. Extension checks provide an early user-facing guard; OpenAI remains responsible for validating the actual audio payload. OpenAI errors and network failures are returned as API errors rather than fabricated transcript results.

The current request and file-format guidance is in the [OpenAI speech-to-text guide](https://developers.openai.com/api/docs/guides/speech-to-text) and the [Audio API reference](https://developers.openai.com/api/docs/api-reference/audio/createTranscription). The backend uses the Whisper model (`whisper-1`) and multipart `file` + `model` fields.

## REST API

Base URL: `http://localhost:8080/api`

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/transcriptions` | Transcribe and save an audio upload. Send multipart field `file`. |
| `GET` | `/transcriptions` | List records, newest first. |
| `GET` | `/transcriptions/{id}` | Retrieve one record. |
| `PUT` | `/transcriptions/{id}` | Update title and transcript using JSON. |
| `DELETE` | `/transcriptions/{id}` | Delete a record. |
| `GET` | `/transcriptions/search?keyword=...` | Search title and transcript text. |

### Postman examples

Set the Postman base URL variable `{{baseUrl}}` to `http://localhost:8080/api`.

1. **Upload and transcribe** — `POST {{baseUrl}}/transcriptions`
   - Body → form-data; key `file`, type **File**, choose a supported audio recording.
   - Do not manually set the multipart `Content-Type`; Postman supplies its boundary.
   - Successful response: `201 Created` and a JSON transcription record.

2. **List all** — `GET {{baseUrl}}/transcriptions`
   - Successful response: `200 OK` and an array of records.

3. **Get one** — `GET {{baseUrl}}/transcriptions/1`
   - Successful response: `200 OK`; unknown IDs return `404 Not Found`.

4. **Update** — `PUT {{baseUrl}}/transcriptions/1`
   - Header: `Content-Type: application/json`
   - Body → raw → JSON:
     ```json
     {
       "title": "Product planning call",
       "transcript": "The corrected transcript text."
     }
     ```
   - Successful response: `200 OK`; title and transcript are required.

5. **Delete** — `DELETE {{baseUrl}}/transcriptions/1`
   - Successful response: `204 No Content`; unknown IDs return `404 Not Found`.

6. **Search** — `GET {{baseUrl}}/transcriptions/search?keyword=planning`
   - Successful response: `200 OK` and matching records. URL-encode special characters in the keyword.

Validation or unsupported-format failures return `400 Bad Request`; files above the upload limit return `413 Payload Too Large`; transcription-provider failures return `502 Bad Gateway`.

## Build and run checks

```powershell
# Frontend production build
cd frontend
npm install
npm run build

# Backend compile and tests
cd ..\backend
mvn test
```

Both services need their respective local dependencies (Node packages for the frontend; Maven dependencies, MySQL, and an OpenAI key for actual transcription). An OpenAI API key is not needed to compile either application.
