import { accessValidationSchema } from "entities/access/access-validation-schema";
import { alternateIdentifierValidationSchema } from "entities/alternate-identifier/alternate-identifier-validation-schema";
import { alternateUrlValidationSchema } from "entities/alternate-url/alternate-url-validation-schema";
import { contributorValidationSchema } from "entities/contributor/contributor-validation-schema";
import { dateValidationSchema } from "entities/date/date-validation-schema";
import { descriptionValidationSchema } from "entities/description/description-validation-schema";
import { identifierValidationSchema } from "entities/identifier/identifier-validation-schema";
import { organisationValidationSchema } from "entities/organisation/organisation-validation-schema";
import { relatedObjectValidationSchema } from "entities/related-object/related-object-validation-schema";
import { relatedRaidValidationSchema } from "entities/related-raid/related-raid-validation-schema";
import { spatialCoverageValidationSchema } from "entities/spatial-coverage/spatial-coverage-validation-schema";
import { subjectValidationSchema } from "entities/subject/subject-validation-schema";
import { titleValidationSchema } from "entities/title/title-validation-schema";
import { traditionalKnowledgeIdentifiersValidationSchema } from "entities/traditional-knowledge-identifier/traditional-knowledge-identifier-validation-schema";
import { z } from "zod";

export const ValidationFormSchema = z.object({
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
  traditionalKnowledgeIdentifier:
    traditionalKnowledgeIdentifiersValidationSchema,
});
