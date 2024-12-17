import { OrcidLookupResponse } from "./types";

const BASE_URL = "https://orcid.test.raid.org.au";

async function makeOrcidRequest<T>(
  endpoint: string,
  handle: string
): Promise<T> {
  const requestOptions = {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ handle }),
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, requestOptions);
  return response.json();
}

export async function fetchOrcidContributors({
  handle,
}: {
  handle: string;
}): Promise<OrcidLookupResponse[]> {
  return makeOrcidRequest<OrcidLookupResponse[]>("/contributors", handle);
}

export async function fetchOrcidLookup({
  handle,
}: {
  handle: string;
}): Promise<OrcidLookupResponse[]> {
  return makeOrcidRequest<OrcidLookupResponse[]>("/lookup", handle);
}
