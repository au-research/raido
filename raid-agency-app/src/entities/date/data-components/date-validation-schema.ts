import { combinedPattern } from "@/utils/date-utils/date-utils";
import { z } from "zod";

const dateStringSchema = z.string().regex(combinedPattern, {
  message: "YYYY or YYYY-MM or YYYY-MM-DD",
});

export const dateValidationSchema = z
  .object({
    startDate: dateStringSchema,
    endDate: dateStringSchema.optional(),
  })
  .refine(
    ({ startDate, endDate }) => {
      if (!endDate) return true;
      return new Date(startDate) <= new Date(endDate);
    },
    {
      message: "Start date must be before or equal to end date",
      path: ["endDate"],
    }
  );
