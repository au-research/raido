import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

export const dateValidationSchema = z.object({
  startDate: z.string().regex(combinedPattern).min(1),
  endDate: z.string().regex(combinedPattern).optional(),
});
