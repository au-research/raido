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
