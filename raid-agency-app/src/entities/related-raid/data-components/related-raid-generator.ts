import { RelatedRaid, RelatedRaidType } from "@/generated/raid";
import relatedRaidType from "@/references/related_raid_type.json";
import relatedRaidTypeSchema from "@/references/related_raid_type_schema.json";

const relatedRaidTypeGenerator = (): RelatedRaidType => {
  const randomIndex = Math.floor(Math.random() * relatedRaidType.length);
  return {
    id: relatedRaidType[randomIndex].uri,
    schemaUri: relatedRaidTypeSchema[0].uri,
  };
};

export const relatedRaidGenerator = (): RelatedRaid => {
  return {
    type: relatedRaidTypeGenerator(),
  };
};
