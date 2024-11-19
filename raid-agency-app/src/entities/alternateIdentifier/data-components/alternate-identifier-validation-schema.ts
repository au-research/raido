import { z } from "zod";

export const alternateIdentifierValidationSchema = z.array(
  z.object({
    id: z.string().min(1),
    type: z.string().min(1),
  }),
);
