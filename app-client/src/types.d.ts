import {
  LegacyMetadataSchemaV1,
  RaidoMetadataSchemaV1,
  RaidoMetadataSchemaV2,
  ReadRaidResponseV2,
} from "Generated/Raidv2/models";

export interface ReadData {
  readonly raid: ReadRaidResponseV2;
  readonly metadata:
    | RaidoMetadataSchemaV1
    | RaidoMetadataSchemaV2
    | LegacyMetadataSchemaV1;
}
