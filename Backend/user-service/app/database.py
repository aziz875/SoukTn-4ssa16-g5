from collections.abc import Generator

from fastapi import Request
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from sqlalchemy.orm import DeclarativeBase, Session, sessionmaker


class Base(DeclarativeBase):
    pass


def create_database_engine(database_url: str) -> Engine:
    connect_args = {"check_same_thread": False} if database_url.startswith("sqlite") else {}
    return create_engine(database_url, pool_pre_ping=True, connect_args=connect_args)


def ensure_database_exists(engine: Engine) -> None:
    if engine.dialect.name != "mysql":
        return
    database_name = engine.url.database
    if not database_name:
        raise ValueError("A database name is required for the MySQL connection")

    server_engine = create_engine(engine.url.set(database=""), pool_pre_ping=True)
    quoted_name = engine.dialect.identifier_preparer.quote(database_name)
    try:
        with server_engine.begin() as connection:
            connection.exec_driver_sql(f"CREATE DATABASE IF NOT EXISTS {quoted_name} CHARACTER SET utf8mb4")
    finally:
        server_engine.dispose()


def create_session_factory(engine: Engine) -> sessionmaker[Session]:
    return sessionmaker(bind=engine, autoflush=False, expire_on_commit=False)


def get_db(request: Request) -> Generator[Session, None, None]:
    session_factory: sessionmaker[Session] = request.app.state.session_factory
    with session_factory() as session:
        yield session
