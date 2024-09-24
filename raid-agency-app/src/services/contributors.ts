import { OrcidContributorResponse, OrcidLookupResponse } from "@/types";

export async function fetchOrcidContributors({
  handle,
}: {
  handle: string;
}): Promise<OrcidContributorResponse[]> {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    handle,
  });

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
  };

  const response = await fetch(
    "https://orcid.test.raid.org.au/contributors",
    requestOptions
  );
  return await response.json();
}

export async function fetchOrcidLookup({
  handle,
}: {
  handle: string;
}): Promise<OrcidLookupResponse[]> {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    handle,
  });

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
  };

  const response = await fetch(
    "https://orcid.test.raid.org.au/lookup",
    requestOptions
  );
  return await response.json();
}
