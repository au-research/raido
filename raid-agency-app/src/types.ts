import type {
  RaidDto,
  ServicePoint,
  ServicePointCreateRequest,
  ServicePointUpdateRequest,
} from "@/generated/raid";

import type Keycloak from "keycloak-js";
import { FieldErrors } from "react-hook-form";

export interface KeycloakContextValue {
  keycloak: Keycloak;
  initialized: boolean;
}

export type KeycloakGroup = {
  id: string;
  name: string;
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

export type ApiTokenRequest = {
  refreshToken: string;
};

export type RequestTokenResponse = {
  access_token: string;
  expires_in: number;
  id_token: string;
  "not-before-policy": number;
  refresh_expires_in: number;
  refresh_token: string;
  scope: string;
  session_state: string;
  token_type: string;
};

export interface ChildConfig {
  fieldKey: string;
  label: string;
  labelPlural: string;
  DetailsComponent: React.ComponentType<{
    parentIndex: number;
    index: number;
    errors?: FieldErrors<RaidDto>;
  }>;
  generator: () => any;
}

/// /// ///

export interface CreateServicePointRequest {
  servicePointCreateRequest: ServicePointCreateRequest;
}

export interface UpdateServicePointRequest {
  id: number;
  servicePointUpdateRequest: ServicePointUpdateRequest;
}
