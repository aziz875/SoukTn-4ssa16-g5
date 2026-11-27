from collections.abc import Generator
from uuid import UUID

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.auth import Principal, get_current_principal
from app.config import Settings
from app.database import Base, get_db
from app.main import create_app


@pytest.fixture
def engine() -> Generator[Engine, None, None]:
    test_engine = create_engine(
        "sqlite+pysqlite:///:memory:",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    yield test_engine
    test_engine.dispose()


@pytest.fixture
def client(engine: Engine) -> Generator[TestClient, None, None]:
    session_factory = sessionmaker(bind=engine, autoflush=False, expire_on_commit=False)
    test_app = create_app(
        settings=Settings(eureka_enabled=False),
        database_url="sqlite+pysqlite:///:memory:",
    )
    test_app.dependency_overrides[get_current_principal] = lambda: Principal("keycloak-subject-1", frozenset())

    def get_test_db() -> Generator:
        with session_factory() as session:
            yield session

    test_app.dependency_overrides[get_db] = get_test_db
    Base.metadata.create_all(bind=engine)
    with TestClient(test_app) as test_client:
        yield test_client


def test_profile_crud_remains_protected_after_public_creation(client: TestClient) -> None:
    create_response = client.post(
        "/api/users",
        json={
            "first_name": "Ahmed",
            "last_name": "Test",
            "email": "ahmed@example.com",
            "phone": "+21612345678",
        },
    )
    assert create_response.status_code == 201
    profile_id = create_response.json()["id"]
    assert str(UUID(profile_id)) == profile_id

    client.app.dependency_overrides[get_current_principal] = lambda: Principal(profile_id, frozenset())
    profile_url = f"/api/users/{profile_id}"
    assert client.get(profile_url).json()["email"] == "ahmed@example.com"

    update_response = client.put(
        profile_url,
        json={
            "first_name": "Ahmed",
            "last_name": "Updated",
            "email": "ahmed@example.com",
            "phone": "+21698765432",
        },
    )
    assert update_response.status_code == 200
    assert update_response.json()["last_name"] == "Updated"

    assert client.delete(profile_url).status_code == 204
    assert client.get(profile_url).status_code == 404


def test_profile_operations_require_authentication() -> None:
    test_app = create_app(
        settings=Settings(eureka_enabled=False),
        database_url="sqlite+pysqlite:///:memory:",
    )
    with TestClient(test_app) as test_client:
        response = test_client.get("/api/users")
    assert response.status_code == 401


def test_create_profile_does_not_require_authentication(engine: Engine) -> None:
    test_app = create_app(
        settings=Settings(eureka_enabled=False),
        database_url="sqlite+pysqlite:///:memory:",
    )

    def get_test_db() -> Generator:
        with sessionmaker(bind=engine, autoflush=False, expire_on_commit=False)() as session:
            yield session

    test_app.dependency_overrides[get_db] = get_test_db
    Base.metadata.create_all(bind=engine)
    with TestClient(test_app) as test_client:
        response = test_client.post(
            "/api/users",
            json={
                "first_name": "Public",
                "last_name": "Registration",
                "email": "public@example.com",
            },
        )
    assert response.status_code == 201
    assert str(UUID(response.json()["id"])) == response.json()["id"]


def test_non_admin_cannot_list_profiles(client: TestClient) -> None:
    response = client.get("/api/users")
    assert response.status_code == 403


def test_admin_can_list_profiles_and_other_users_cannot_read_them(client: TestClient) -> None:
    client.post(
        "/api/users",
        json={"first_name": "Ahmed", "last_name": "Test", "email": "ahmed@example.com"},
    )
    client.app.dependency_overrides[get_current_principal] = lambda: Principal("another-user", frozenset())
    assert client.get("/api/users/keycloak-subject-1").status_code == 403

    client.app.dependency_overrides[get_current_principal] = lambda: Principal("admin-user", frozenset({"ADMIN"}))
    response = client.get("/api/users")
    assert response.status_code == 200
    assert len(response.json()) == 1


def test_swagger_includes_bearer_authentication(client: TestClient) -> None:
    openapi = client.get("/openapi.json").json()
    assert openapi["components"]["securitySchemes"]["HTTPBearer"]["scheme"] == "bearer"
    assert "/api/users" in openapi["paths"]
    assert "security" not in openapi["paths"]["/api/users"]["post"]
    assert openapi["paths"]["/api/users"]["get"]["security"]


def test_duplicate_email_is_rejected(client: TestClient) -> None:
    profile = {
        "first_name": "Ahmed",
        "last_name": "Test",
        "email": "ahmed@example.com",
    }
    assert client.post("/api/users", json=profile).status_code == 201
    assert client.post("/api/users", json=profile).status_code == 409
