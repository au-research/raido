import { z } from "zod";
import { accessValidationSchema } from "./access/access-validation-schema";
import { alternateIdentifierValidationSchema } from "./alternate-identifier/alternate-identifier-validation-schema";
import { alternateUrlValidationSchema } from "./alternate-url/alternate-url-validation-schema";
import { contributorValidationSchema } from "./contributor/contributor-validation-schema";
import { dateValidationSchema } from "./date/date-validation-schema";
import { descriptionValidationSchema } from "./description/description-validation-schema";
import { identifierValidationSchema } from "./identifier/identifier-validation-schema";
import { organisationValidationSchema } from "./organisation/organisation-validation-schema";
import { relatedObjectValidationSchema } from "./related-object/related-object-validation-schema";
import { relatedRaidValidationSchema } from "./related-raid/related-raid-validation-schema";
import { spatialCoverageValidationSchema } from "./spatial-coverage/spatial-coverage-validation-schema";
import { subjectValidationSchema } from "./subject/subject-validation-schema";
import { titleValidationSchema } from "./title/title-validation-schema";
import { traditionalKnowledgeIdentifiersValidationSchema } from "./traditional-knowledge-identifier/traditional-knowledge-identifier-validation-schema";

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
