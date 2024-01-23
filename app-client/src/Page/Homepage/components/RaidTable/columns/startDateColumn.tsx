import {GridColDef} from "@mui/x-data-grid";
import {dateDisplayFormatter} from "../../../../../date-utils";
import dayjs from "dayjs";

export const startDateColumn: GridColDef = {
    field: "date",
    headerName: "Start Date",
    width: 100,
    renderCell: (params) => {
        const formattedDate = dateDisplayFormatter(
            params.row.date.startDate ? dayjs(params.row.date.startDate).format(
                "YYYY-MM-DD"
            ) : "tba"
        )
        return formattedDate;
    },
    sortComparator: (v1, v2, param1, param2) => {
        return new Date(v1.startDate).getTime() - new Date(v2.startDate).getTime();
    },
    sortable: true,
}