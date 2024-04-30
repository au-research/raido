import Keycloak from "keycloak-js";

let keycloakInstance: Keycloak | null = null;

export default function getKeycloakInstance() {
    const keycloakUrl = import.meta.env.VITE_KEYCLOAK_URL;
    const keycloakRealm = import.meta.env.VITE_KEYCLOAK_REALM;
    const keycloakClientId = import.meta.env.VITE_KEYCLOAK_CLIENT_ID;

    if (!keycloakInstance) {
        keycloakInstance = new Keycloak({
            url: keycloakUrl,
            realm: keycloakRealm,
            clientId: keycloakClientId,
        });
    }
    return keycloakInstance;
}
