import Keycloak from "keycloak-js";

const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
};

// Validate environment variables
Object.entries(keycloakConfig).forEach(([key, value]) => {
  if (!value) {
    throw new Error(`Missing required Keycloak configuration: ${key}`);
  }
});

export const clientId = keycloakConfig.clientId;

export const keycloak = new Keycloak(keycloakConfig);
