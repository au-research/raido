export async function sendInvite({
  email,
  handle,
  token,
}: {
  email: string;
  handle: string;
  token: string;
}) {
  const response = await fetch("https://orcid.test.raid.org.au/invite", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      inviteeEmail: email,
      handle,
    }),
  });
  return await response.json();
}
