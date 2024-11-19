import { accessValidationSchema } from "@/entities/access/data-components/access-validation-schema";
import { alternateIdentifierValidationSchema } from "@/entities/alternateIdentifier/data-components/alternate-identifier-validation-schema";
import { alternateUrlValidationSchema } from "@/entities/alternateUrl/data-components/alternate-url-validation-schema";
import { contributorValidationSchema } from "@/entities/contributor/data-components/contributor-validation-schema";
import { dateValidationSchema } from "@/entities/date/data-components/date-validation-schema";
import { descriptionValidationSchema } from "@/entities/description/data-components/description-validation-schema";
import { identifierValidationSchema } from "@/entities/identifier/data-components/identifier-validation-schema";
import { organisationValidationSchema } from "@/entities/organisation/data-components/organisation-validation-schema";
import { relatedObjectValidationSchema } from "@/entities/relatedObject/data-components/related-object-validation-schema";
import { relatedRaidValidationSchema } from "@/entities/relatedRaid/data-components/related-raid-validation-schema";
import { spatialCoverageValidationSchema } from "@/entities/spatialCoverage/spatial-coverage-validation-schema";
import { subjectValidationSchema } from "@/entities/subject/data-components/subject-validation-schema";
import { titleValidationSchema } from "@/entities/title/data-components/title-validation-schema";
import { traditionalKnowledgeLabelValidationSchema } from "@/entities/traditionalKnowledgeLabel/traditional-knowledge-label-validation-schema";
import { z } from "zod";

const ValidationFormSchema = z.object({
  identifier: identifierValidationSchema,
  title: titleValidationSchema,
  date: dateValidationSchema,
  description: descriptionValidationSchema,
  access: accessValidationSchema,
  alternateUrl: alternateUrlValidationSchema,
  contributor: contributorValidationSchema,
  organisation: organisationValidationSchema,
  subject: subjectValidationSchema,
  relatedRaid: relatedRaidValidationSchema,
  relatedObject: relatedObjectValidationSchema,
  alternateIdentifier: alternateIdentifierValidationSchema,
  spatialCoverage: spatialCoverageValidationSchema,
  traditionalKnowledgeLabel: traditionalKnowledgeLabelValidationSchema,
});

export default ValidationFormSchema;
