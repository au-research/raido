import dayjs from "dayjs";

export const dateGenerator = () => {

    const todaysDate = dayjs(new Date()).format("YYYY-MM-DD")

    return {
        startDate: todaysDate,
        endDate: undefined,
    };
}