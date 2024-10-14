import { DisplayItem } from "@/components/DisplayItem";
import { ModelDate } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";

interface FieldsProps<T> {
  data: T | undefined;
}

export default function Fields<T extends ModelDate>({ data }: FieldsProps<T>) {
  return (
    <>
      <DisplayItem
        label="Start Date"
        value={dateDisplayFormatter(data?.startDate)}
        width={3}
      />
      <DisplayItem
        label="End Date"
        value={dateDisplayFormatter(data?.endDate)}
        width={3}
      />
    </>
  );
}
