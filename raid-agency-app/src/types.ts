import { ReactElement } from "react";

export type Breadcrumb = {
  icon: ReactElement;
  label: string;
  to: string;
};

import type Keycloak from "keycloak-js";

// Define the shape of the context value
export interface KeycloakContextValue {
  keycloak: Keycloak;
  initialized: boolean;
}

export type Failure = {
  fieldId: string;
  errorType: string;
  message: string;
};
