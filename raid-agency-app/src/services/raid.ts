import { RaidDto } from "@/generated/raid";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";

const endpoint = getApiEndpoint();

export const fetchRaids = async ({
  servicePointId,
  fields,
  token,
}: {
  servicePointId: number | undefined;
  fields?: string[];
  token: string;
}): Promise<RaidDto[]> => {
  const url = new URL(`${endpoint}/raid/`);

  console.log("servicePointId", servicePointId);

  // Check if fields are provided and need to be included
  if (fields && fields.length > 0) {
    const fieldsQuery = fields.join(",");
    url.searchParams.set("includeFields", fieldsQuery);
  }

  if (servicePointId) {
    url.searchParams.set("servicePointId", servicePointId.toString());
  }

  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
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
