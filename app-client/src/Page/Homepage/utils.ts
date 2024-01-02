import { RaidDto } from "../../Generated/Raidv2";
import { formatLocalDateAsIso } from "../../Util/DateUtil";

export const extractValuesFromRaid = (raid: RaidDto) => {
    // Destructure with default empty objects to avoid null checks later
    const { title: [{ text: title = "" } = {}] = [], identifier, date = {startDate:"", endDate:""} } = raid || {};

    // Use the URL and Date constructors directly in the return object
    // only when the values exist
    return {
        title,
        handle: identifier?.id ? new URL(identifier.id).pathname.substring(1) : "",
        startDate: date.startDate ? formatLocalDateAsIso(new Date(date.startDate)) : "",
        endDate: date.endDate ? formatLocalDateAsIso(new Date(date.endDate)) : "",
    };
};
