import {GridColDef} from "@mui/x-data-grid";
import {dateDisplayFormatter} from "../../../../../date-utils";
import dayjs from "dayjs";

export const handleColumn: GridColDef = {
    field: "identifier",
    headerName: "Handle",
    width: 150,
    renderCell: (params) => {
        return dateDisplayFormatter(
            params.row.date.endDate ? dayjs(params.row.date.endDate).format(
                "YYYY-MM-DD"
            ) : "tba"
        )
    },
    sortComparator: (v1, v2) => {
        return new Date(v1.endDate).getTime() - new Date(v2.endDate).getTime();
    },
    sortable: true
}