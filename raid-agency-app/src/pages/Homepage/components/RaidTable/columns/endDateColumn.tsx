import { GridColDef } from "@mui/x-data-grid";

import { dateDisplayFormatter } from "@/Util/DateUtil";

export const endDateColumn: GridColDef = {
  field: "date.endDate",
  headerName: "End Date",
  flex: 1,
  valueGetter: (params) => {
    return dateDisplayFormatter(params.row.date.endDate);
  },
};
