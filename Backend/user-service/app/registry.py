import asyncio
import logging
import socket

import httpx

from app.config import Settings

logger = logging.getLogger(__name__)


class EurekaRegistration:
    def __init__(self, settings: Settings):
        self.settings = settings
        self.base_url = settings.eureka_server_url.rstrip("/")
        self.instance_id = f"{socket.gethostname()}:user:{settings.server_port}"
        self._registered = False

    async def register(self) -> None:
        if not self.settings.eureka_enabled:
            return

        payload = {
            "instance": {
                "instanceId": self.instance_id,
                "hostName": self.settings.eureka_instance_host,
                "app": self.settings.service_name.upper(),
                "ipAddr": self.settings.eureka_instance_host,
                "status": "UP",
                "port": {"$": self.settings.server_port, "@enabled": "true"},
                "securePort": {"$": 443, "@enabled": "false"},
                "homePageUrl": f"http://{self.settings.eureka_instance_host}:{self.settings.server_port}/",
                "statusPageUrl": (
                    f"http://{self.settings.eureka_instance_host}:{self.settings.server_port}/actuator/health"
                ),
                "healthCheckUrl": (
                    f"http://{self.settings.eureka_instance_host}:{self.settings.server_port}/actuator/health"
                ),
                "dataCenterInfo": {
                    "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                    "name": "MyOwn",
                },
            }
        }
        try:
            async with httpx.AsyncClient(timeout=self.settings.eureka_request_timeout) as client:
                response = await client.post(
                    f"{self.base_url}/apps/{self.settings.service_name.upper()}",
                    json=payload,
                    headers={"Accept": "application/json"},
                )
                response.raise_for_status()
            self._registered = True
            logger.info("Registered %s with Eureka at %s", self.instance_id, self.base_url)
        except httpx.HTTPError:
            logger.exception("Could not register %s with Eureka; will retry", self.instance_id)

    async def heartbeat(self) -> None:
        if not self.settings.eureka_enabled:
            return
        if not self._registered:
            await self.register()
            return
        try:
            async with httpx.AsyncClient(timeout=self.settings.eureka_request_timeout) as client:
                response = await client.put(
                    f"{self.base_url}/apps/{self.settings.service_name.upper()}/{self.instance_id}",
                    headers={"Accept": "application/json"},
                )
                if response.status_code == httpx.codes.NOT_FOUND:
                    self._registered = False
                    await self.register()
                    return
                response.raise_for_status()
        except httpx.HTTPError:
            logger.exception("Eureka heartbeat failed for %s", self.instance_id)

    async def run_heartbeats(self) -> None:
        while True:
            await asyncio.sleep(self.settings.eureka_heartbeat_interval)
            await self.heartbeat()

    async def deregister(self) -> None:
        if not self.settings.eureka_enabled or not self._registered:
            return
        try:
            async with httpx.AsyncClient(timeout=self.settings.eureka_request_timeout) as client:
                response = await client.delete(
                    f"{self.base_url}/apps/{self.settings.service_name.upper()}/{self.instance_id}",
                    headers={"Accept": "application/json"},
                )
                response.raise_for_status()
            logger.info("Deregistered %s from Eureka", self.instance_id)
        except httpx.HTTPError:
            logger.exception("Could not deregister %s from Eureka", self.instance_id)
        finally:
            self._registered = False
