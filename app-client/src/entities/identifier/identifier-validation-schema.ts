import { z } from "zod";

export const identifierValidationSchema = z
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
  .optional();
