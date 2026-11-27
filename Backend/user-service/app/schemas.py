from datetime import datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field


class UserProfileInput(BaseModel):
    first_name: str = Field(min_length=1, max_length=100)
    last_name: str = Field(min_length=1, max_length=100)
    email: EmailStr
    phone: str | None = Field(default=None, max_length=30)


class UserProfileResponse(UserProfileInput):
    model_config = ConfigDict(from_attributes=True)

    id: str
    created_at: datetime
    updated_at: datetime
