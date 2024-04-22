type TitleTypeMapping = {
  "https://vocabulary.raid.org/title.type.schema/156": string;
  "https://vocabulary.raid.org/title.type.schema/157": string;
  "https://vocabulary.raid.org/title.type.schema/4": string;
  "https://vocabulary.raid.org/title.type.schema/5": string;
};

export const titleMapping: { titleType: TitleTypeMapping } = {
  titleType: {
    "https://vocabulary.raid.org/title.type.schema/156": "Acronym",
    "https://vocabulary.raid.org/title.type.schema/157": "Short",
    "https://vocabulary.raid.org/title.type.schema/4": "Alternative",
    "https://vocabulary.raid.org/title.type.schema/5": "Primary",
  },
};
