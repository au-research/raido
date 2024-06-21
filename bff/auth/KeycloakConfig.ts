

interface KeycloakConfig {
    authServerUrl: () => string;
    realm: () => string,
    clientId: () => string,
    secret: () => string
}

export const keycloakConfig :KeycloakConfig = {
    authServerUrl : () => {
        const authServerUrl = process.env.OIDC_AUTH_SERVER_URL;
        if (!authServerUrl) {
            throw new Error('OIDC_AUTH_SERVER_URL not set');
        }
        return authServerUrl;
    },
    realm: () => {
        const realm = process.env.OIDC_REALM;
        if (!realm) {
            throw new Error('OIDC_REALM not set');
        }
        return realm;
    },
    clientId: () => {
        const clientId = process.env.OIDC_CLIENT_ID;
        if (!clientId) {
            throw new Error('OIDC_CLIENT_ID not set');
        }
        return clientId;
    },
    secret: () => {
        const secret = process.env.OIDC_CLIENT_SECRET;

        if (!secret) {
            throw new Error('OIDC_CLIENT_SECRET not set');
        }

        return secret;
    }
}