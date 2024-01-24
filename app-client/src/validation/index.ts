import {z} from "zod";
import {titlesValidationSchema} from "../Forms/RaidForm/components/FormTitlesComponent";
import {datesValidationSchema} from "Forms/RaidForm/components/FormDatesComponent";
import {descriptionsValidationSchema} from "Forms/RaidForm/components/FormDescriptionsComponent";
import {accessValidationSchema} from "Forms/RaidForm/components/FormAccessComponent";
import {contributorsValidationSchema} from "Forms/RaidForm/components/FormContributorsComponent";
import {organisationsValidationSchema} from "Forms/RaidForm/components/FormOrganisationsComponent";
import {subjectsValidationSchema} from "Forms/RaidForm/components/FormSubjectsComponent";
import {alternateUrlValidationSchema} from "Forms/RaidForm/components/FormAlternateUrlsComponent";
import {relatedRaidValidationSchema} from "Forms/RaidForm/components/FormRelatedRaidsComponent";
import {relatedObjectValidationSchema} from "Forms/RaidForm/components/FormRelatedObjectsComponent";
import {alternateIdentifierValidationSchema} from "Forms/RaidForm/components/FormAlternateIdentifiersComponent";
import {spatialCoverageValidationSchema} from "Forms/RaidForm/components/FormSpatialCoveragesComponent";

export const ValidationFormSchema = z.object({
  identifier: z
    .object({
      id: z.string().min(1),
      owner: z.object({
        id: z.string().min(1),
        schemaUri: z.string().min(1),
        servicePoint: z.number().int(),
      }),
      license: z.string().min(1),
      version: z.number().int(),
      schemaUri: z.string().min(1),
      raidAgencyUrl: z.string().min(1),
      registrationAgency: z.object({
        id: z.string().min(1),
        schemaUri: z.string().min(1),
      }),
    })
    .optional(),
  title: titlesValidationSchema,
  date: datesValidationSchema,
  description: descriptionsValidationSchema,
  access: accessValidationSchema,
  alternateUrl: alternateUrlValidationSchema,
  contributor: contributorsValidationSchema,
  organisation: organisationsValidationSchema,
  subject: subjectsValidationSchema,
  relatedRaid: relatedRaidValidationSchema,
  relatedObject: relatedObjectValidationSchema,
  alternateIdentifier: alternateIdentifierValidationSchema,
  spatialCoverage: spatialCoverageValidationSchema,
  // traditionalKnowledgeLabel: z.array(
  //   z.object({
  //     id: z.string().nonempty(),
  //     schemaUri: z.string().nonempty(),
  //   })
  // ),
});
