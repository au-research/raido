import { ModelDate } from "@/generated/raid";
import dayjs from "dayjs";

const generator = (): ModelDate => {
  const todaysDate = dayjs(new Date()).format("YYYY-MM-DD");
  return {
    startDate: todaysDate,
    endDate: undefined,
  };
};

export default generator;
