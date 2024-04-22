import { KeycloakContext } from "@/contexts/KeycloakContext";
import { KeycloakContextValue } from "@/types";
import { useContext } from "react";

export const useCustomKeycloak = (): KeycloakContextValue => {
  const context = useContext(KeycloakContext);
  if (context === undefined) {
    throw new Error("useCustomKeycloak must be used within a KeycloakProvider");
  }
  return context;
};
