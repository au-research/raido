type DescriptionTypeMapping = {
  "https://vocabulary.raid.org/description.type.schema/3": string;
  "https://vocabulary.raid.org/description.type.schema/318": string;
  "https://vocabulary.raid.org/description.type.schema/319": string;
  "https://vocabulary.raid.org/description.type.schema/6": string;
  "https://vocabulary.raid.org/description.type.schema/7": string;
  "https://vocabulary.raid.org/description.type.schema/8": string;
  "https://vocabulary.raid.org/description.type.schema/9": string;
};

export const descriptionMapping: { descriptionType: DescriptionTypeMapping } = {
  descriptionType: {
    "https://vocabulary.raid.org/description.type.schema/3": "Brief",
    "https://vocabulary.raid.org/description.type.schema/318": "Primary",
    "https://vocabulary.raid.org/description.type.schema/319": "Alternative",
    "https://vocabulary.raid.org/description.type.schema/6": "Other",
    "https://vocabulary.raid.org/description.type.schema/7": "Objectives",
    "https://vocabulary.raid.org/description.type.schema/8": "Methods",
    "https://vocabulary.raid.org/description.type.schema/9":
      "Significance statement",
  },
};
