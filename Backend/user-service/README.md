# User Service (FastAPI)

Standalone Python user-profile service for SoukTn. Keycloak remains responsible
for authentication and credentials; this service stores profile data only.

## Run locally

From `Backend/user-service`:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
Copy-Item .env.example .env
uvicorn app.main:app --host 0.0.0.0 --port 8081
```

Set `DB_PASSWORD` in `.env` to the local MySQL password. The service uses the
existing `microservice.user_profiles` table and creates the configured
database/table only if they do not exist. Configuration defaults match
`Backend/config-repo/user.yml` and the shared `marketplace` Keycloak realm.
Set `KEYCLOAK_ISSUER_URL`, `EUREKA_SERVER_URL`, and `EUREKA_ENABLED` to
override local integration settings.

## Eureka discovery

Start the Eureka server from `Backend/eurekaServer` before starting this
service. The user service registers as `USER` at `http://localhost:8761/eureka`
by default, sends periodic heartbeats, and retries registration if Eureka is
temporarily unavailable. Check the Eureka dashboard at
`http://localhost:8761` and look for the `USER` application. The registered
health URL is `http://localhost:8081/actuator/health`.

## API

- Swagger UI: `http://localhost:8081/docs`
- OpenAPI JSON: `http://localhost:8081/openapi.json`
- Health: `http://localhost:8081/actuator/health`
- `POST /api/users` is public and creates a profile without a Keycloak token.
  The service assigns a UUID as the profile ID.
- `GET /api/users/{user_id}`, `PUT /api/users/{user_id}`, and
  `DELETE /api/users/{user_id}` are available to the profile owner or an
  account with the Keycloak realm role `ADMIN`.
- `GET /api/users` is restricted to the Keycloak realm role `ADMIN`.

Use **Authorize** in Swagger with a Keycloak access token for the protected
read, update, and delete operations. Public profile creation does not require
authorization.

## Tests

```powershell
python -m pytest
```

Tests use in-memory SQLite and do not require MySQL, Keycloak, or Eureka.
