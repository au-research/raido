/// <reference types="astro/client" />

import type { RaidDto } from "@/generated/raid/models/RaidDto";

const apiEndpoint = import.meta.env.API_ENDPOINT;
const iamEndpoint = import.meta.env.IAM_ENDPOINT;

const iamClientId = import.meta.env.IAM_CLIENT_ID;
const iamClientSecret = import.meta.env.IAM_CLIENT_SECRET;

let cachedData: RaidDto[] | null = null;

async function getAuthToken(): Promise<string> {
  const TOKEN_PARAMS = {
    grant_type: "client_credentials",
    client_id: iamClientId,
    client_secret: iamClientSecret,
  };

  const requestOptions = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams(TOKEN_PARAMS),
  };

  try {
    const response = await fetch(
      `${iamEndpoint}/realms/raid/protocol/openid-connect/token`,
      requestOptions
    );

    if (!response.ok) {
      const errorBody = await response.text();
      throw new Error(
        `Authentication failed: ${response.status} - ${errorBody}`
      );
    }

    const { access_token } = await response.json();

    return access_token;
  } catch (error) {
    console.error(
      "Authentication token fetch failed. ",
      error instanceof Error ? error.message : ""
    );
    throw new Error("Failed to obtain authentication token");
  }
}

export async function fetchRaids(): Promise<RaidDto[]> {
  if (cachedData) return cachedData;
  try {
    const token = await getAuthToken();

    const response = await fetch(`${apiEndpoint}/raid/`, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    cachedData = (await response.json()) as RaidDto[];
    return cachedData;
  } catch (error) {
    console.error("There was a problem fetching the raids:", error);
    throw error;
  }
}
