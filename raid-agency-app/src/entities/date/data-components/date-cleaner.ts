import { ModelDate } from "@/generated/raid";

export const dateCleaner = (
  date: ModelDate | undefined
): ModelDate | undefined => {
  if (date) {
    return {
      startDate: date.startDate,
      endDate: date.endDate || undefined,
    };
  }
};
