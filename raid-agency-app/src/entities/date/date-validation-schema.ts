import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

export const dateValidationSchema = z
  .object({
    startDate: z.string().regex(combinedPattern).min(1),
    endDate: z.string().regex(combinedPattern).optional(),
  })
  .refine(
    (data) => {
      if (!data.endDate) return true;
      // Pre-parse the dates once and use the parsed values for comparison
      const startDate = new Date(data.startDate);
      const endDate = new Date(data.endDate);
      return startDate <= endDate;
    },
    {
      message: "startDate must be before endDate",
      path: ["endDate"],
    }
  );
