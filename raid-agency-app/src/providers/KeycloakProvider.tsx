import { KeycloakContext } from "@/contexts/KeycloakContext";
import { useKeycloak } from "@react-keycloak/web";
import { ReactNode } from "react";

interface KeycloakProviderProps {
  children: ReactNode;
}

export const KeycloakProvider: React.FC<KeycloakProviderProps> = ({
  children,
}) => {
  const { keycloak, initialized } = useKeycloak();

  return (
    <KeycloakContext.Provider value={{ keycloak, initialized }}>
      {children}
    </KeycloakContext.Provider>
  );
};
