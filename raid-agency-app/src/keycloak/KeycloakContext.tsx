// KeycloakContext.tsx
import type Keycloak from "keycloak-js";
import { createContext, useContext } from "react";

interface KeycloakContextValue {
  keycloak: Keycloak;
  initialized: boolean;
}

export const KeycloakContext = createContext<KeycloakContextValue | null>(null);
