// KeycloakContext.tsx
import type Keycloak from "keycloak-js";
import { createContext } from "react";

interface KeycloakContextValue {
  keycloak: Keycloak;
  initialized: boolean;
}

export const KeycloakContext = createContext<KeycloakContextValue | undefined>(
  undefined
);
