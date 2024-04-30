type AccessTypeMapping = {
  "https://vocabularies.coar-repositories.org/access_rights/c_abf2/": string;
  "https://vocabularies.coar-repositories.org/access_rights/c_f1cf/": string;
};

export const accessMapping: { accessType: AccessTypeMapping } = {
  accessType: {
    "https://vocabularies.coar-repositories.org/access_rights/c_abf2/":
      "Open Access",
    "https://vocabularies.coar-repositories.org/access_rights/c_f1cf/":
      "Embargoed Access",
  },
};
