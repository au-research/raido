import accessGenerator from "@/entities/access/data-components/access-generator";
import dateCleaner from "@/entities/date/data-components/date-cleaner";
import dateGenerator from "@/entities/date/data-components/date-generator";
import titleGenerator from "@/entities/title/data-components/title-generator";
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

import AccessDisplay from "@/entities/access/AccessDisplay";
import AlternateIdentifiersDisplay from "@/entities/alternateIdentifier/AlternateIdentifiersDisplay";
import AlternateUrlDisplay from "@/entities/alternateUrl/AlternateUrlDisplay";
import ContributorDisplay from "@/entities/contributor/ContributorDisplay";
import DateDisplay from "@/entities/date/DateDisplay";
import DescriptionDisplay from "@/entities/description/DescriptionDisplay";
import OrganisationDisplay from "@/entities/organisation/OrganisationDisplay";
import RelatedObjectsDisplay from "@/entities/relatedObject/RelatedObjectsDisplay";
import RelatedRaidDisplay from "@/entities/relatedRaid/RelatedRaidDisplay";
import SpatialCoverageDisplay from "@/entities/spatialCoverage/SpatialCoverageDisplay";
import SubjectDisplay from "@/entities/subject/SubjectDisplay";
import TitleDisplay from "@/entities/title/display-components/TitleDisplay";
import TraditionalKnowledgeLabelDisplay from "@/entities/traditionalKnowledgeLabel/TraditionalKnowledgeLabelDisplay";
import organisationGenerator from "@/entities/organisation/data-components/organisation-generator";

export const displayItems = [
  {
    itemKey: "date",
    label: "Dates",
    Component: DateDisplay,
  },
  {
    itemKey: "title",
    label: "Titles",
    Component: TitleDisplay,
  },
  {
    itemKey: "description",
    label: "Descriptions",
    Component: DescriptionDisplay,
  },
  {
    itemKey: "contributor",
    label: "Contributors",
    Component: ContributorDisplay,
  },
  {
    itemKey: "organisation",
    label: "Organisations",
    Component: OrganisationDisplay,
  },
  {
    itemKey: "relatedObject",
    label: "Related Objects",
    Component: RelatedObjectsDisplay,
  },
  {
    itemKey: "alternateIdentifier",
    label: "Alternate Identifiers",
    Component: AlternateIdentifiersDisplay,
  },
  {
    itemKey: "alternateUrl",
    label: "Alternate URLs",
    Component: AlternateUrlDisplay,
  },
  {
    itemKey: "relatedRaid",
    label: "Related RAiDs",
    Component: RelatedRaidDisplay,
  },
  {
    itemKey: "access",
    label: "Access",
    Component: AccessDisplay,
  },
  {
    itemKey: "subject",
    label: "Subjects",
    Component: SubjectDisplay,
  },
  {
    itemKey: "traditionalKnowledgeLabel",
    label: "Traditional Knowledge Labels",
    Component: TraditionalKnowledgeLabelDisplay,
  },
  {
    itemKey: "spatialCoverage",
    label: "Spatial Coverages",
    Component: SpatialCoverageDisplay,
  },
];

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
  organisation: [organisationGenerator()],
  contributor: [],
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getErrorMessageForField(obj: any, keyString: string): any {
  const keys = keyString.split("."); // Split the keyString into an array of keys
  let value = obj;

  for (const key of keys) {
    if (value && key in value) {
      value = value[key]; // Traverse the object
    } else {
      return undefined; // Return undefined if any key is not found
    }
  }

  return value;
}
