import { RaidDto } from "@/generated/raid";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";
import type Keycloak from "keycloak-js";
import { fetchServicePoints } from "@/services/service-points";

const endpoint = getApiEndpoint();

export const fetchRaids = async ({
  fields,
  keycloak,
  spOnly,
}: {
  fields?: string[];
  keycloak: Keycloak;
  spOnly?: boolean;
}): Promise<RaidDto[]> => {
  const url = new URL(`${endpoint}/raid/`);

  const { service_point_group_id: servicePointGroupId } =
    keycloak.tokenParsed || {};

  if (servicePointGroupId && spOnly) {
    try {
      const servicePoints = await fetchServicePoints({
        token: keycloak.token || "",
      });

      const userServicePoint = servicePoints.find(
        ({ groupId }) => groupId === servicePointGroupId
      );

      if (userServicePoint?.id) {
        url.searchParams.set("servicePointId", userServicePoint?.id.toString());
      }

      console.log(userServicePoint);
    } catch (error) {
      console.error("Failed to fetch service points:", error);
    }
  } else {
    console.warn("No service point group ID provided");
  }

  if (fields && fields.length > 0) {
    const fieldsQuery = fields.join(",");
    url.searchParams.set("includeFields", fieldsQuery);
  }

  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${keycloak.token}`,
    },
  });
  return await response.json();
};

export const fetchRaid = async ({
  id,
  token,
}: {
  id: string;
  token: string;
}): Promise<RaidDto> => {
  const response = await fetch(`${endpoint}/raid/${id}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  return await response.json();
};

export const createRaid = async ({
  data,
  token,
}: {
  data: RaidDto;
  token: string;
}): Promise<RaidDto> => {
  const response = await fetch(`${endpoint}/raid/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  });
  return await response.json();
};

export const updateRaid = async ({
  id,
  data,
  token,
}: {
  id: string;
  data: RaidDto;
  token: string;
}): Promise<RaidDto> => {
  const response = await fetch(`${endpoint}/raid/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  });
  return await response.json();
};
