import { ModelDate } from "@/generated/raid";

const cleaner = (date: ModelDate | undefined): ModelDate | undefined => {
  if (date) {
    return {
      startDate: date.startDate,
      endDate: date.endDate || undefined,
    };
  }
};
export default cleaner;
