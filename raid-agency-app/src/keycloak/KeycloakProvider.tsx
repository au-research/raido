// KeycloakProvider.tsx
import { KeycloakContext } from "./KeycloakContext";
import { useKeycloak } from "@react-keycloak/web";
import { ReactNode } from "react";

interface KeycloakProviderProps {
  children: ReactNode;
}

export const KeycloakProvider = ({ children }: KeycloakProviderProps) => {
  const keycloakState = useKeycloak();

  return (
    <KeycloakContext.Provider value={keycloakState}>
      {children}
    </KeycloakContext.Provider>
  );
};
