import { ModelDate } from "@/generated/raid";
import dayjs from "dayjs";

export default function generator(): ModelDate {
  const todaysDate = dayjs(new Date()).format("YYYY-MM-DD");
  const nextYear = dayjs(new Date()).add(1, "year").format("YYYY-MM-DD");
  return {
    startDate: todaysDate,
    endDate: nextYear,
  };
}
