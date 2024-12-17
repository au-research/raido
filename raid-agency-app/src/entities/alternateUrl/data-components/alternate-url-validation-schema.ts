import { z } from "zod";
export const alternateUrlValidationSchema = z.array(
  z.object({
    url: z.string().url().min(1),
  }),
);
