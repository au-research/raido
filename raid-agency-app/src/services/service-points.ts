import { ServicePoint } from "@/generated/raid";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";

const endpoint = getApiEndpoint();

export const fetchServicePoints = async ({
  token,
}: {
  token: string;
}): Promise<ServicePoint[]> => {
  console.log("endpoint", endpoint);
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
