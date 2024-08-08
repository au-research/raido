import { ModelDate } from "@/generated/raid";
import dayjs from "dayjs";

export const dateGenerator = (): ModelDate => {
  const todaysDate = dayjs(new Date()).format("YYYY-MM-DD");

  return {
    startDate: todaysDate,
    endDate: undefined,
  };
};
