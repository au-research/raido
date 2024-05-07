import {
  CreateServicePointRequest,
  ServicePoint,
  UpdateServicePointRequest,
} from "@/generated/raid";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";

const endpoint = getApiEndpoint();

export const fetchServicePoints = async ({
  token,
}: {
  token: string;
}): Promise<ServicePoint[]> => {
  const url = new URL(`${endpoint}/service-point/`);

  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  return await response.json();
};

export const fetchServicePoint = async ({
  id,
  token,
}: {
  id: number;
  token: string;
}): Promise<ServicePoint> => {
  const url = new URL(`${endpoint}/service-point/${id}`);

  const response = await fetch(url, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  return await response.json();
};

export const createServicePoint = async ({
  data,
  token,
}: {
  data: CreateServicePointRequest;
  token: string;
}): Promise<ServicePoint> => {
  const url = new URL(`${endpoint}/service-point/`);

  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data.servicePointCreateRequest),
  });
  return await response.json();
};

export const updateServicePoint = async ({
  id,
  data,
  token,
}: {
  id: number;
  data: UpdateServicePointRequest;
  token: string;
}): Promise<ServicePoint> => {
  const url = new URL(`${endpoint}/service-point/${id}`);

  const response = await fetch(url, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data.servicePointUpdateRequest),
  });
  return await response.json();
};
