import { useContext } from "react";
import { KeycloakContext } from "../KeycloakContext";

export const useKeycloakContext = () => {
  const context = useContext(KeycloakContext);
  if (!context) {
    throw new Error(
      "useKeycloakContext must be used within a KeycloakProvider"
    );
  }
  return context;
};
