const kcUrl = import.meta.env.VITE_KEYCLOAK_URL as string;
const kcRealm = import.meta.env.VITE_KEYCLOAK_REALM as string;

export async function fetchCurrentUserKeycloakGroups({
  token,
}: {
  token: string | undefined;
}) {
  const requestUrl = `${kcUrl}/realms/${kcRealm}/group/user-groups`;

  try {
    if (token === undefined) {
      throw new Error("Error: Keycloak token not set");
    }
    const response = await fetch(requestUrl, {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    return await response.json();
  } catch (error) {
    const errorMessage = "Error: Keycloak groups could not be fetched";
    console.error(errorMessage);
    throw new Error(errorMessage);
  }
}

export async function setKeycloakUserAttribute({
  groupId,
  token,
}: {
  groupId: string;
  token: string | undefined;
}) {
  const requestUrl = `${kcUrl}/realms/${kcRealm}/group/active-group`;
  try {
    if (token === undefined) {
      throw new Error("Error: Keycloak token not set");
    }

    await fetch(requestUrl, {
      method: "PUT",
      body: JSON.stringify({ activeGroupId: groupId }),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (error) {
    const errorMessage = "Error: Keycloak group could not be joined";
    console.error(errorMessage);
    throw new Error(errorMessage);
  }
}
