type ContributorPositionMapping = {
  "https://vocabulary.raid.org/contributor.position.schema/307": string;
  "https://vocabulary.raid.org/contributor.position.schema/308": string;
  "https://vocabulary.raid.org/contributor.position.schema/309": string;
  "https://vocabulary.raid.org/contributor.position.schema/310": string;
  "https://vocabulary.raid.org/contributor.position.schema/311": string;
};

export const contributorMapping: {
  contributorPosition: ContributorPositionMapping;
} = {
  contributorPosition: {
    "https://vocabulary.raid.org/contributor.position.schema/307":
      "Principal or Chief Investigator",
    "https://vocabulary.raid.org/contributor.position.schema/308":
      "Co-investigator or Collaborator",
    "https://vocabulary.raid.org/contributor.position.schema/309":
      "Partner Investigator",
    "https://vocabulary.raid.org/contributor.position.schema/310": "Consultant",
    "https://vocabulary.raid.org/contributor.position.schema/311":
      "Other Participant",
  },
};
