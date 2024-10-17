import { RaidDto } from "@/generated/raid";
import { fetchServicePoints } from "@/services/service-points";
import { RaidHistoryType } from "@/types";
import { httpStatusCodes } from "@/utils";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";
import type Keycloak from "keycloak-js";

const endpoint = getApiEndpoint();
const API_ENDPOINT = `${endpoint}/raid/`;

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
    } catch (error) {
      console.error("Failed to fetch service points:", error);
    }
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
      "X-Raid-Api-Version": "3",
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
  try {
    const response = await fetch(`${endpoint}/raid/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "X-Raid-Api-Version": "3",
      },
    });

    if (!response.ok) {
      if (response.status && httpStatusCodes.has(response.status)) {
        throw new Error(`HTTP error: ${httpStatusCodes.get(response.status)} (${response.status})`);
      }
      throw new Error(`HTTP error. Status code: ${response.status}`);
    }

    const data = await response.json();

    if (!data) {
      throw new Error("No data received from the server");
    }

    return data as RaidDto;
  } catch (error) {
    if (error instanceof Error) {
      console.error("Error fetching raid:", error.message);
    } else {
      console.error("An unknown error occurred while fetching raid");
    }
    throw error; // Re-throw the error for the caller to handle
  }
};
export const fetchRaidHistory = async ({
  id,
  token,
}: {
  id: string;
  token: string;
}): Promise<RaidHistoryType[]> => {
  const response = await fetch(`${endpoint}/raid/${id}/history`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      "X-Raid-Api-Version": "3",
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
  try {
    for (const contributor of data?.contributor || []) {
      if (contributor.id === "") {
        contributor.id = null!;
      }
    }
    const response = await fetch(API_ENDPOINT, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "X-Raid-Api-Version": "3",
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(JSON.stringify(errorData) || "Failed to create raid");
    }

    const responseData = await response.json();
    return responseData;
  } catch (error) {
    let errorMessage = "Failed to create raid";
    if (error instanceof Error) {
      errorMessage = error.message;
    }
    throw new Error(errorMessage);
  }
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
  try {
    for (const contributor of data?.contributor || []) {
      if (contributor.id === "") {
        contributor.id = null!;
      }
    }

    const response = await fetch(`${endpoint}/raid/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        "X-Raid-Api-Version": "3",
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(JSON.stringify(errorData) || "Failed to create raid");
    }

    const responseData = await response.json();
    return responseData;
  } catch (error) {
    let errorMessage = "Failed to create raid";
    if (error instanceof Error) {
      errorMessage = error.message;
    }
    throw new Error(errorMessage);
  }
};
