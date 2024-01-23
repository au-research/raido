import {GridColDef} from "@mui/x-data-grid";
import {dateDisplayFormatter} from "../../../../../date-utils";

export const endDateColumn: GridColDef = {
    field: "date.endDate",
    headerName: "End Date",
    width: 120,
    valueGetter: (params) => {
        return dateDisplayFormatter(params.row.date.endDate)
    }
}