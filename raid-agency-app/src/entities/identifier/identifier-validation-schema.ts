import { z } from "zod";

const identifierOwnerValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.string().min(1),
  servicePoint: z.number().int(),
});

const identifierRegistrationAgencyValidationSchema = z.object({
  id: z.string().min(1),
  schemaUri: z.string().min(1),
});

export const identifierValidationSchema = z
  .object({
    id: z.string().min(1),
    owner: identifierOwnerValidationSchema,
    license: z.string().min(1),
    version: z.number().int(),
    schemaUri: z.string().min(1),
    raidAgencyUrl: z.string().min(1),
    registrationAgency: identifierRegistrationAgencyValidationSchema,
  })
  .optional();
