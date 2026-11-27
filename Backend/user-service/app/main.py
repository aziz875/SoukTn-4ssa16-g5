import asyncio
import logging
from contextlib import asynccontextmanager, suppress
from datetime import datetime, timezone
from typing import Annotated
from uuid import uuid4

from fastapi import Depends, FastAPI, HTTPException, Query, Response, status
from sqlalchemy import select, text
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.auth import CurrentPrincipal, Principal
from app.config import Settings, get_settings
from app.database import Base, create_database_engine, create_session_factory, ensure_database_exists, get_db
from app.models import UserProfile
from app.registry import EurekaRegistration
from app.schemas import UserProfileInput, UserProfileResponse

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def create_app(settings: Settings | None = None, database_url: str | None = None) -> FastAPI:
    service_settings = settings or get_settings()
    engine = create_database_engine(database_url or service_settings.database_url)
    session_factory = create_session_factory(engine)
    eureka = EurekaRegistration(service_settings)

    @asynccontextmanager
    async def lifespan(app: FastAPI):
        ensure_database_exists(engine)
        Base.metadata.create_all(bind=engine)
        await eureka.register()
        heartbeat_task = asyncio.create_task(eureka.run_heartbeats())
        try:
            yield
        finally:
            heartbeat_task.cancel()
            with suppress(asyncio.CancelledError):
                await heartbeat_task
            await eureka.deregister()
            engine.dispose()

    app = FastAPI(
        title="SoukTn User Service API",
        description="Public profile creation with Keycloak-protected profile management.",
        version="1.0.0",
        lifespan=lifespan,
    )
    app.state.session_factory = session_factory
    app.state.settings = service_settings

    def require_owner_or_admin(user_id: str, principal: Principal) -> None:
        if user_id != principal.subject and not principal.is_admin:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Not allowed to access this profile")

    @app.get("/actuator/health", tags=["Health"])
    def health(db: Annotated[Session, Depends(get_db)]) -> dict[str, object]:
        db.execute(text("SELECT 1"))
        return {"status": "UP", "components": {"db": {"status": "UP"}}}

    @app.post(
        "/api/users",
        response_model=UserProfileResponse,
        status_code=status.HTTP_201_CREATED,
        tags=["User Profiles"],
    )
    def create_user(
        payload: UserProfileInput,
        db: Annotated[Session, Depends(get_db)],
    ) -> UserProfile:
        if db.scalar(select(UserProfile).where(UserProfile.email == payload.email)) is not None:
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email is already in use")

        now = datetime.now(timezone.utc).replace(tzinfo=None)
        profile = UserProfile(
            id=str(uuid4()),
            created_at=now,
            updated_at=now,
            **payload.model_dump(),
        )
        db.add(profile)
        try:
            db.commit()
        except IntegrityError as exception:
            db.rollback()
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="A profile or email already exists",
            ) from exception
        db.refresh(profile)
        return profile

    @app.get("/api/users", response_model=list[UserProfileResponse], tags=["User Profiles"])
    def list_users(
        principal: CurrentPrincipal,
        db: Annotated[Session, Depends(get_db)],
        offset: Annotated[int, Query(ge=0)] = 0,
        limit: Annotated[int, Query(ge=1, le=100)] = 50,
    ) -> list[UserProfile]:
        if not principal.is_admin:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Administrator role required")
        return list(db.scalars(select(UserProfile).offset(offset).limit(limit)).all())

    @app.get("/api/users/{user_id}", response_model=UserProfileResponse, tags=["User Profiles"])
    def get_user(user_id: str, principal: CurrentPrincipal, db: Annotated[Session, Depends(get_db)]) -> UserProfile:
        require_owner_or_admin(user_id, principal)
        profile = db.get(UserProfile, user_id)
        if profile is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User profile not found")
        return profile

    @app.put("/api/users/{user_id}", response_model=UserProfileResponse, tags=["User Profiles"])
    def update_user(
        user_id: str,
        payload: UserProfileInput,
        principal: CurrentPrincipal,
        db: Annotated[Session, Depends(get_db)],
    ) -> UserProfile:
        require_owner_or_admin(user_id, principal)
        profile = db.get(UserProfile, user_id)
        if profile is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User profile not found")
        duplicate = db.scalar(select(UserProfile).where(UserProfile.email == payload.email, UserProfile.id != user_id))
        if duplicate is not None:
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email is already in use")
        for field, value in payload.model_dump().items():
            setattr(profile, field, value)
        try:
            db.commit()
        except IntegrityError as exception:
            db.rollback()
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email is already in use") from exception
        db.refresh(profile)
        return profile

    @app.delete("/api/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT, tags=["User Profiles"])
    def delete_user(
        user_id: str,
        principal: CurrentPrincipal,
        db: Annotated[Session, Depends(get_db)],
    ) -> Response:
        require_owner_or_admin(user_id, principal)
        profile = db.get(UserProfile, user_id)
        if profile is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User profile not found")
        db.delete(profile)
        db.commit()
        return Response(status_code=status.HTTP_204_NO_CONTENT)

    return app


app = create_app()
