from dataclasses import dataclass
from functools import lru_cache
from typing import Annotated, Any

import jwt
from fastapi import Depends, HTTPException, Request, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from jwt import InvalidTokenError
from jwt import PyJWKClient
from jwt.exceptions import PyJWKClientError

bearer_scheme = HTTPBearer(auto_error=False)


@dataclass(frozen=True)
class Principal:
    subject: str
    roles: frozenset[str]

    @property
    def is_admin(self) -> bool:
        return "admin" in {role.lower() for role in self.roles}


@lru_cache
def get_jwks_client(issuer: str) -> PyJWKClient:
    return PyJWKClient(f"{issuer.rstrip('/')}/protocol/openid-connect/certs", timeout=5)


def get_current_principal(
    credentials: Annotated[HTTPAuthorizationCredentials | None, Depends(bearer_scheme)],
    request: Request,
) -> Principal:
    unauthorized = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="A valid Keycloak bearer token is required",
        headers={"WWW-Authenticate": "Bearer"},
    )
    if credentials is None or credentials.scheme.lower() != "bearer":
        raise unauthorized

    issuer = request.app.state.settings.keycloak_issuer_url.rstrip("/")
    try:
        jwks_client = get_jwks_client(issuer)
        signing_key = jwks_client.get_signing_key_from_jwt(credentials.credentials)
        claims: dict[str, Any] = jwt.decode(
            credentials.credentials,
            signing_key.key,
            algorithms=["RS256"],
            issuer=issuer,
            options={"verify_aud": False, "require": ["exp", "sub"]},
        )
    except (InvalidTokenError, PyJWKClientError) as exception:
        raise unauthorized from exception

    subject = claims.get("sub")
    if not isinstance(subject, str) or not subject:
        raise unauthorized

    realm_access = claims.get("realm_access", {})
    roles = realm_access.get("roles", []) if isinstance(realm_access, dict) else []
    return Principal(subject=subject, roles=frozenset(role for role in roles if isinstance(role, str)))


CurrentPrincipal = Annotated[Principal, Depends(get_current_principal)]
