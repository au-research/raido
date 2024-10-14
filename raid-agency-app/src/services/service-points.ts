import {
  CreateServicePointRequest,
  ServicePoint,
  UpdateServicePointRequest,
} from "@/generated/raid";
import { ServicePointMember, ServicePointWithMembers } from "@/types";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";

const kcUrl = import.meta.env.VITE_KEYCLOAK_URL as string;
const kcRealm = import.meta.env.VITE_KEYCLOAK_REALM as string;

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
      "X-Raid-Api-Version": "3",
    },
  });
  return await response.json();
};

export const fetchServicePointsWithMembers = async ({
  token,
}: {
  token: string;
}): Promise<ServicePointWithMembers[]> => {
  const servicePointUrl = new URL(`${endpoint}/service-point/`);
  const servicePointMembersUrl = `${kcUrl}/realms/${kcRealm}/group`;

  const members = new Map<string, ServicePointMember[]>();

  const servicePointResponse = await fetch(servicePointUrl, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      "X-Raid-Api-Version": "3",
    },
  });
  const servicePoints = await servicePointResponse.json();

  for (const servicePoint of servicePoints) {
    if (servicePoint.groupId) {
      const servicePointMembersResponse = await fetch(
        `${servicePointMembersUrl}?groupId=${servicePoint.groupId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
            "X-Raid-Api-Version": "3",
          },
        }
      );
      const servicePointMembers = await servicePointMembersResponse.json();
      members.set(
        servicePoint.groupId,
        servicePointMembers.members as ServicePointMember[]
      );
    }
  }

  const servicePointsWithMembers = servicePoints.map(
    (servicePoint: ServicePoint) => {
      return {
        ...servicePoint,
        members: members.has(servicePoint?.groupId as string)
          ? members.get(servicePoint.groupId as string)
          : [],
      };
    }
  );

  return servicePointsWithMembers;
};

export const fetchServicePointWithMembers = async ({
  id,
  token,
}: {
  id: number;
  token: string;
}): Promise<ServicePointWithMembers> => {
  const servicePointUrl = new URL(`${endpoint}/service-point/${id}`);
  const servicePointMembersUrl = `${kcUrl}/realms/${kcRealm}/group`;

  const members = new Map<string, ServicePointMember[]>();

  const servicePointResponse = await fetch(servicePointUrl, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      "X-Raid-Api-Version": "3",
    },
  });
  const servicePoint = await servicePointResponse.json();

  if (servicePoint.groupId) {
    const servicePointMembersResponse = await fetch(
      `${servicePointMembersUrl}?groupId=${servicePoint.groupId}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
          "X-Raid-Api-Version": "3",
        },
      }
    );
    const servicePointMembers = await servicePointMembersResponse.json();
    members.set(
      servicePoint.groupId,
      servicePointMembers.members as ServicePointMember[]
    );
  }

  const servicePointsWithMembers = {
    ...servicePoint,
    members: members.has(servicePoint?.groupId as string)
      ? members.get(servicePoint.groupId as string)
      : [],
  };

  return servicePointsWithMembers;
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
      "X-Raid-Api-Version": "3",
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
      "X-Raid-Api-Version": "3",
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
      "X-Raid-Api-Version": "3",
    },
    body: JSON.stringify(data.servicePointUpdateRequest),
  });
  return await response.json();
};

export const updateUserServicePointUserRole = async ({
  userId,
  userGroupId,
  operation,
  token,
}: {
  userId: string;
  userGroupId: string;
  operation: "grant" | "revoke";
  token: string;
}): Promise<ServicePoint> => {
  const url = `${kcUrl}/realms/${kcRealm}/group`;

  const response = await fetch(`${url}/${operation}`, {
    method: "PUT",
    credentials: "include",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
      "X-Raid-Api-Version": "3",
    },
    body: JSON.stringify({ userId, groupId: userGroupId }),
  });
  if (!response.ok) {
    throw new Error(`Failed to ${operation}`);
  }
  return response.json();
};
