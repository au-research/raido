const Roles = {
  LEAD_RESEARCH_ORG: "https://vocabulary.raid.org/organisation.role.schema/182",
  OTHER_RESEARCH_ORG:
    "https://vocabulary.raid.org/organisation.role.schema/183",
  PARTNER_ORG: "https://vocabulary.raid.org/organisation.role.schema/184",
  CONTRACTOR: "https://vocabulary.raid.org/organisation.role.schema/185",
  FUNDER: "https://vocabulary.raid.org/organisation.role.schema/186",
  FACILITY: "https://vocabulary.raid.org/organisation.role.schema/187",
  OTHER_ORG: "https://vocabulary.raid.org/organisation.role.schema/188",
} as const;

type OrganisationRoleIdValues = {
  [key in (typeof Roles)[keyof typeof Roles]]: string;
};

export const organisationRoles: OrganisationRoleIdValues = {
  [Roles.LEAD_RESEARCH_ORG]: "Lead Research Organisation",
  [Roles.OTHER_RESEARCH_ORG]: "Other Research Organisation",
  [Roles.PARTNER_ORG]: "Partner Organisation",
  [Roles.CONTRACTOR]: "Contractor",
  [Roles.FUNDER]: "Funder",
  [Roles.FACILITY]: "Facility",
  [Roles.OTHER_ORG]: "Other Organisation",
};
