import {GridColDef} from "@mui/x-data-grid";
import {dateDisplayFormatter} from "../../../../../date-utils";
import dayjs from "dayjs";

export const startDateColumn: GridColDef = {
    field: "date",
    headerName: "Start Date",
    width: 100,
    renderCell: (params) => {
        return dateDisplayFormatter(
            params.row.date.startDate ? dayjs(params.row.date.startDate).format(
                "YYYY-MM-DD"
            ) : "tba"
        )
    },
    sortComparator: (v1, v2) => {
        return new Date(v1.startDate).getTime() - new Date(v2.startDate).getTime();
    },
    sortable: true,
}