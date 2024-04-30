import { combinedPattern } from "@/Util/DateUtil";
import { z } from "zod";

export const dateValidationSchema = z.object({
  startDate: z.string().regex(combinedPattern).min(1),
  endDate: z.string().regex(combinedPattern).optional(),
});
