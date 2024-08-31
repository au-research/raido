import { OrcidContributor } from "@/types";

export const fetchRaidOrcidContributors = async ({
  handle,
}: {
  handle: string;
}): Promise<OrcidContributor[]> => {
  const response = await fetch(
    `https://orcid.test.raid.org.au/fetch-contributors`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        handle: handle,
      }),
    }
  );
  const data = await response.json();
  const { statusCode, body } = data.contributors;
  const contributorsData = JSON.parse(body);
  if (statusCode !== 200) {
    return [];
  }

  return contributorsData;
};
