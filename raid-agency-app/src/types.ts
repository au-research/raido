import { ReactElement } from "react";
import type { ServicePoint } from "@/generated/raid";
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

type ServicePointMemberAttributes = {
  firstName: (string | null)[];
  lastName: (string | null)[];
  activeGroupId: string[];
  email: (string | null)[];
  username: string[];
};

export type ServicePointMember = {
  roles: string[];
  attributes: ServicePointMemberAttributes;
  id: string;
};

export type ServicePointWithMembers = ServicePoint & {
  members: ServicePointMember[];
};

type FormFieldType = "text";

export interface FormFieldProps {
  name: string;
  label: string;
  placeholder?: string;
  type?: FormFieldType;
  helperText?: string;
  errorText?: string;
  required?: boolean;
  width?: number;
  multiline?: boolean;
  keyField?: string;
}

export interface OrcidContributor {
  email: string;
  orcid: string;
  access_token: string;
  expires_on: number;
  handle: string;
  name: string;
  refresh_token: string;
  scope: string;
  status: string;
  token_type: string;
  uuid: string;
}
