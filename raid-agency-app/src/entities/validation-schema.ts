import { alternateIdentifierValidationSchema } from "@/entities/alternate-identifier/data-components/alternate-identifier-validation-schema";
import { descriptionValidationSchema } from "@/entities/description/data-components/description-validation-schema";
import { z } from "zod";
import { accessValidationSchema } from "@/entities/access/data-components/access-validation-schema";
import { alternateUrlValidationSchema } from "./alternate-url/data-components/alternate-url-validation-schema";
import { contributorValidationSchema } from "@/entities/contributor/data-components/contributor-validation-schema";
import dateValidationSchema from "./date/data-components/validator";
import { identifierValidationSchema } from "./identifier/identifier-validation-schema";
import { organisationValidationSchema } from "./organisation/data-components/organisation-validation-schema";
import { relatedObjectValidationSchema } from "./related-object/data-components/related-object-validation-schema";
import { relatedRaidValidationSchema } from "./related-raid/data-components/related-raid-validation-schema";
import { spatialCoverageValidationSchema } from "./spatial-coverage/spatial-coverage-validation-schema";
import { subjectValidationSchema } from "./subject/subject-validation-schema";
import { titleValidationSchema } from "./title/data-components/title-validation-schema";
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
