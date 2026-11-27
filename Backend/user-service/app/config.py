from functools import lru_cache

from pydantic import SecretStr
from pydantic_settings import BaseSettings, SettingsConfigDict
from sqlalchemy import URL


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    service_name: str = "USER"
    server_port: int = 8081

    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "microservice"
    db_user: str = "root"
    db_password: SecretStr = SecretStr("")

    keycloak_issuer_url: str = "http://localhost:8180/realms/marketplace"

    eureka_server_url: str = "http://localhost:8761/eureka"
    eureka_enabled: bool = True
    eureka_instance_host: str = "127.0.0.1"
    eureka_heartbeat_interval: float = 30.0
    eureka_request_timeout: float = 3.0

    @property
    def database_url(self) -> str:
        return URL.create(
            "mysql+pymysql",
            username=self.db_user,
            password=self.db_password.get_secret_value(),
            host=self.db_host,
            port=self.db_port,
            database=self.db_name,
            query={"charset": "utf8mb4"},
        ).render_as_string(hide_password=False)


@lru_cache
def get_settings() -> Settings:
    return Settings()
