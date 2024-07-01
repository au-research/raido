import {
  Access,
  AlternateIdentifier,
  AlternateUrl,
  Contributor,
  Description,
  Id,
  ModelDate,
  Organisation,
  RaidCreateRequest,
  RaidDto,
  RelatedObject,
  RelatedRaid,
  SpatialCoverage,
  Subject,
  Title,
  TraditionalKnowledgeLabel,
} from "@/generated/raid";
import { accessGenerator } from "@/entities/access/access-generator";
import { contributorGenerator } from "@/entities/contributor/contributor-generator";
import { dateGenerator } from "@/entities/date/date-generator";
import { titleGenerator } from "@/entities/title/title-generator";
import { dateCleaner } from "@/entities/date/date-cleaner";

export const raidRequest = (data: RaidDto): RaidDto => {
  return {
    identifier: data?.identifier || ({} as Id),
    description: data?.description || ([] as Description[]),
    title: data?.title || ([] as Title[]),
    access: data?.access || ({} as Access),
    alternateUrl: data?.alternateUrl || ([] as AlternateUrl[]),
    relatedRaid: data?.relatedRaid || ([] as RelatedRaid[]),
    date: dateCleaner(data?.date) || ({} as ModelDate),
    contributor: data?.contributor || ([] as Contributor[]),
    alternateIdentifier:
      data?.alternateIdentifier || ([] as AlternateIdentifier[]),
    organisation: data?.organisation || ([] as Organisation[]),
    relatedObject: data?.relatedObject || ([] as RelatedObject[]),
    spatialCoverage: data?.spatialCoverage || ([] as SpatialCoverage[]),
    subject: data?.subject || ([] as Subject[]),
    traditionalKnowledgeLabel:
      data?.traditionalKnowledgeLabel || ([] as TraditionalKnowledgeLabel[]),
  };
};

export const newRaid: RaidCreateRequest = {
  title: [titleGenerator()],
  // description: [descriptionGenerator()],
  date: dateGenerator(),
  access: accessGenerator(),
  // organisation: [organisationGenerator()],
  contributor: [contributorGenerator()],
  // subject: [subjectGenerator()],
  // relatedRaid: [],
  alternateUrl: [],
  // spatialCoverage: [spatialCoverageGenerator()],
  // relatedObject: [
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  //   relatedObjectGenerator(),
  // ],
  // alternateIdentifier: [
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  //   alternateIdentifierGenerator(),
  // ],
};
