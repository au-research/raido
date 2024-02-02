import relatedRaidType from "References/related_raid_type.json";
import relatedRaidTypeSchema from "References/related_raid_type_schema.json";

export const relatedRaidGenerator = () => {
  return {
    type: {
      id: relatedRaidType[Math.floor(Math.random() * relatedRaidType.length)]
        .uri,
      schemaUri: relatedRaidTypeSchema[0].uri,
    },
  };
};
