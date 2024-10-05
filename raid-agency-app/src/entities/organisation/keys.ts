import { organisationRoleGenerator } from "./child-component/data-components/organisation-role-generator";
import { organisationGenerator } from "./data-components/organisation-generator";

export const ENTITY_KEY = "organisation";
export const ENTITY_LABEL = "Organisation";
export const ENTITY_LABEL_PLURAL = "Organisations";

export const CHILD_ENTITY_KEY = "role";
export const CHILD_ENTITY_LABEL = "Role";

export const ENTITY_GENERATOR = organisationGenerator();

export const CHILD_ENTITY_GENERATOR = organisationRoleGenerator();
