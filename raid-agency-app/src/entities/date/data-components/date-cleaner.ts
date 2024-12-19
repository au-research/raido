import { ModelDate } from "@/generated/raid";

export default function dateCleaner(date?: ModelDate): ModelDate | undefined {
  return (
    date && {
      startDate: date.startDate,
      endDate: date.endDate ?? undefined,
    }
  );
}
