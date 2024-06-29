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

export type RaidHistoryType = {
  handle: string;
  version: number;
  diff: string;
  timestamp: string;
};

export type RaidHistoryElementType = {
  op: string;
  path: string;
  value: unknown;
};

export type KeycloakGroup = {
  id: string;
  name: string;
};

export type MappingElement = {
  id: string;
  value: string;
  field: string;
  definition: string;
  source: string;
};
