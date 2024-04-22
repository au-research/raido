import { dateDisplayFormatter } from "@/Util/DateUtil";
import { GridColDef } from "@mui/x-data-grid";

export const startDateColumn: GridColDef = {
  field: "date.startDate",
  headerName: "Start Date",
  flex: 1,
  valueGetter: (params) => {
    return dateDisplayFormatter(params.row.date.startDate);
  },
};
